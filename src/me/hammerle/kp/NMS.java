package me.hammerle.kp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.v1_17_R1.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireballFireball;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.EntityWitherSkull;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;

public class NMS {
    private final static HashMap<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();

    private static EntityTypes<RawHuman.WrapperHuman> HUMAN_TYPE;

    @SuppressWarnings("unchecked")
    public static void init() {
        try {
            Method m = EntityTypes.class.getDeclaredMethod("a", String.class,
                    EntityTypes.Builder.class);
            m.setAccessible(true);
            HUMAN_TYPE = (EntityTypes<RawHuman.WrapperHuman>) m.invoke(null, "human",
                    EntityTypes.Builder.a(RawHuman.WrapperHuman::new, EnumCreatureType.a)
                            .a(0.6F, 1.95F).trackingRange(8));

            final Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

            Field attributesMapField = AttributeDefaults.class.getDeclaredField("b");
            attributesMapField.setAccessible(true);

            Object base = unsafe.staticFieldBase(attributesMapField);
            long offset = unsafe.staticFieldOffset(attributesMapField);

            Map<EntityTypes<? extends EntityLiving>, AttributeProvider> attributesMap =
                    (Map<EntityTypes<? extends EntityLiving>, AttributeProvider>) attributesMapField
                            .get(null);
            attributesMap = new HashMap<>(attributesMap);
            attributesMap.put(HUMAN_TYPE,
                    EntityMonster.fB().a(GenericAttributes.a, 20.0).a(GenericAttributes.f, 1)
                            .a(GenericAttributes.d, 0.1).a(GenericAttributes.h).a());
            unsafe.putObject(base, offset, attributesMap);

            AttributeDefaults.a();
        } catch(Exception ex) {
            ex.printStackTrace();
            KajetansPlugin.warn(ex.getMessage());
        }
    }

    private static CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public static interface Human extends Creature {
        public void setSkin(String texture, String signature);
    }

    private static class RawHuman extends CraftCreature implements Human {
        private static class WrapperHuman extends EntityCreature {
            private EntityPlayer player;

            public WrapperHuman(EntityTypes<? extends WrapperHuman> type,
                    net.minecraft.world.level.World world) {
                super(type, world);
                setPlayer(UUID.randomUUID(), "Default", world);
            }

            private void setPlayer(UUID uuid, String name, net.minecraft.world.level.World world) {
                player = new EntityPlayer(getCraftServer().getServer(), (WorldServer) world,
                        new GameProfile(uuid, name));
            }

            @Override
            public void tick() {
                super.tick();
            }

            @Override
            public boolean damageEntity(DamageSource ds, float amount) {
                if(ds.ignoresInvulnerability()) {
                    super.damageEntity(ds, amount);
                }
                return false;
            }

            @Override
            public CraftEntity getBukkitEntity() {
                return new RawHuman(this);
            }

            public EntityPlayer update(PacketPlayOutSpawnEntityLiving p) {
                player.e(p.b()); // set id, get id
                player.setLocation(p.e(), p.f(), p.g(), 0.0f, 0.0f);
                return player;
            }

            @Override
            public void saveData(NBTTagCompound nbt) {
                super.saveData(nbt);

                GameProfile gp = player.getProfile();
                nbt.a("HumanUUID", gp.getId());
                nbt.setString("HumanName", gp.getName());

                Collection<Property> c = gp.getProperties().get("textures");
                for(Property p : c) {
                    if(p.getName().equals("textures")) {
                        nbt.setString("HumanTexture", p.getValue());
                        nbt.setString("HumanSignature", p.getSignature());
                        break;
                    }
                }

                KajetansPlugin.log("saved");
            }

            @Override
            public void loadData(NBTTagCompound nbt) {
                super.loadData(nbt);
                if(nbt.b("HumanUUID") && nbt.hasKeyOfType("HumanName", 8)) {
                    UUID uuid = nbt.a("HumanUUID");
                    String name = nbt.getString("CustomName");
                    setPlayer(uuid, name, player.t);
                    KajetansPlugin.log(uuid + " " + name + " loaded");
                }
                if(nbt.hasKeyOfType("HumanTexture", 8) && nbt.hasKeyOfType("HumanSignature", 8)) {
                    String texture = nbt.getString("HumanTexture");
                    String signature = nbt.getString("HumanSignature");
                    setSkin(texture, signature);
                    KajetansPlugin.log(texture + " " + signature + " loaded");
                }
            }

            public void setSkin(String texture, String signature) {
                GameProfile gp = player.getProfile();
                gp.getProperties().clear();
                gp.getProperties().put("textures", new Property("textures", texture, signature));

                PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, player);
                PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);
                for(Player p : Bukkit.getOnlinePlayers()) {
                    var nmsPlayer = map(p);
                    nmsPlayer.b.sendPacket(info);
                    nmsPlayer.b.sendPacket(spawn);
                }
            }
        }

        public final WrapperHuman human;

        private RawHuman(WrapperHuman human) {
            super(getCraftServer(), human);
            this.human = human;
            setPersistent(true);
        }

        private RawHuman(WrapperHuman human, Location l, PlayerProfile profile) {
            this(human);
            human.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            human.getWorld().addEntity(human);
        }

        public RawHuman(Location l, PlayerProfile profile) {
            this(HUMAN_TYPE.a(map(l.getWorld())), l, profile);
        }

        public void setSkin(String texture, String signature) {
            human.setSkin(texture, signature);
            //String texture =
            //        "ewogICJ0aW1lc3RhbXAiIDogMTU4OTA1MzMyMjY3NiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4OTBjN2U1OGYyM2U5N2ZmNGRkYWMwNDhiYmZhMDJkNjYxMTEwMTNkMmYxNzdjNWY4ZjYyYThiMWIxYWZkZCIKICAgIH0KICB9Cn0=";
            //String signature =
            //        "Skp8QvxYFa4YUhw+XHgicva/8j3gnCcN9u0EDLpkSlVCWuYqUeETbgA3LAit4ftjDjNpUA40UPTvlebBsnjcUvEkiu765BvZE61yms2IcNeK7vDoLoeNfx+UqTAquMI6uOzBBNZi6yBeMSghRX2hwVGsiKuzoFb67o1HfFcPLfCOVR3QRd2D84VfduhQmc+MVSFrUhZFGluzOvslUzR8/tbi2ZarkURlLOlgT0UoT1yEX/pHM7GogtnQiJL7xOqfEnU0Ex+OKZYkjawatbD/L5bjbL1pV2QeuxZLrnvRoQFTVAnONhvPfd9f8WF0kdR8DaDe4Knq+SPJ357HOun9ZRci3RobXcyQaRsw5JezSrgUbBccoUr7SiSgdM4VBhtzGGZ8TYUBz5pocHYCaOALG71bZZ4aVjQKfw5Rtalj+q2Wqbub20IQd/7/z9NUvPB0d7zHBLqr8a1UtZoSKLbaVJZJaYqt0ygxff68MKKQlE0L4fupBHEIXdNgza8tp472rsB+o45IZ/xmFltH1jhRsYvV973ki0l4S6U/O6gWu699sUyHn4a3DnVNN0GIyNAIP9KpHhvQzvxPxJq0Z2gXw2rzRGDxt+fe8gYZJ+UF4t/i39IP9RBgryocdu0L0lzeQA0b7vrr1khvAHyBVuZJ0t2S/RHTnlcAcAxoDENP1Gk=";
        }
    }

    public static Human createHuman(String name, Location l) {
        return new RawHuman(l, Bukkit.createProfile(UUID.randomUUID(), name));
    }

    private static class PluginConnection extends PlayerConnection {
        public PluginConnection(PlayerConnection c) {
            super(getCraftServer().getServer(), c.a, c.b);
        }

        @Override
        public void a(PacketPlayInChat packet) {
            super.a(packet);
        }

        private boolean handle(PacketPlayOutSpawnEntityLiving p) {
            if(p.d() != IRegistry.Y.getId(HUMAN_TYPE)) {
                return false;
            }
            UUID uuid = p.c();
            Entity ent = Bukkit.getEntity(uuid);
            if(!(ent instanceof RawHuman)) {
                return false;
            }
            RawHuman raw = (RawHuman) ent;
            EntityPlayer player = raw.human.update(p);
            PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, player);
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);
            super.sendPacket(info);
            super.sendPacket(spawn);

            /*KajetansPlugin.scheduleTask(() -> {
                PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, player);
                sendPacket(playerInfoRemove);
            }, 20);*/
            return true;
        }

        @SuppressWarnings("deprecation")
        private boolean handle(PacketPlayOutEntityMetadata p) {
            int id = p.c();
            net.minecraft.world.entity.Entity ent = b.getWorldServer().b(id);
            return ent instanceof RawHuman.WrapperHuman;
        }

        @Override
        public void sendPacket(Packet<?> packet) {
            if(packet instanceof PacketPlayOutSpawnEntityLiving
                    && handle((PacketPlayOutSpawnEntityLiving) packet)) {
                return;
            } else if(packet instanceof PacketPlayOutEntityMetadata
                    && handle((PacketPlayOutEntityMetadata) packet)) {
                return;
            }
            super.sendPacket(packet);
        }
    }

    public static void patch(Player p) {
        map(p).b = new PluginConnection(map(p).b);
    }

    public static DamageSource getCurrentDamageSource() {
        return CraftEventFactory.currentDamageCause;
    }

    public static Entity getImmediateSource(DamageSource ds) {
        if(ds.k() == null) {
            return null;
        }
        return map(ds.k());
    }

    public static Entity getTrueSource(DamageSource ds) {
        if(ds.getEntity() == null) {
            return null;
        }
        return map(ds.getEntity());
    }

    public static EntityPlayer map(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

    public static net.minecraft.world.item.ItemStack map(ItemStack stack) {
        return CraftItemStack.asNMSCopy(stack);
    }

    public static net.minecraft.world.entity.Entity map(Entity e) {
        return ((CraftEntity) e).getHandle();
    }

    public static Entity map(net.minecraft.world.entity.Entity e) {
        return e.getBukkitEntity();
    }

    public static EntityLiving map(LivingEntity e) {
        return ((CraftLivingEntity) e).getHandle();
    }

    public static EntityArrow map(Arrow e) {
        return ((CraftArrow) e).getHandle();
    }

    public static EntityFireworks map(Firework e) {
        return ((CraftFirework) e).getHandle();
    }

    public static EntityFireballFireball map(LargeFireball e) {
        return ((CraftLargeFireball) e).getHandle();
    }

    public static EntityWitherSkull map(WitherSkull e) {
        return ((CraftWitherSkull) e).getHandle();
    }

    public static EntityItem map(Item e) {
        return (EntityItem) ((CraftItem) e).getHandle();
    }

    public static WorldServer map(World e) {
        return ((CraftWorld) e).getHandle();
    }

    public static GameProfile map(PlayerProfile profile) {
        return ((CraftPlayerProfile) profile).getGameProfile();
    }

    public static DamageSource toDamageSource(Object o) {
        return (DamageSource) o;
    }

    public static DamageSource parseDamageSource(String name) {
        if(DAMAGE_SOURCES.isEmpty()) {
            for(Field f : DamageSource.class.getFields()) {
                if(f.getType() == DamageSource.class) {
                    try {
                        DamageSource ds = (DamageSource) f.get(null);
                        DAMAGE_SOURCES.put(ds.u(), ds);
                    } catch(Exception ex) {
                    }
                }
            }
        }
        return DAMAGE_SOURCES.get(name);
    }

    public static DamageSource sting(LivingEntity liv) {
        return DamageSource.b(map(liv));
    }

    public static DamageSource mobAttack(LivingEntity liv) {
        return DamageSource.mobAttack(map(liv));
    }

    public static DamageSource mobIndirect(Entity ent, LivingEntity liv) {
        return DamageSource.a(map(ent), map(liv));
    }

    public static DamageSource playerAttack(Player p) {
        return DamageSource.playerAttack(map(p));
    }

    public static DamageSource arrow(Arrow arrow, Entity ent) {
        return DamageSource.arrow(map(arrow), map(ent));
    }

    public static DamageSource trident(Entity ent, Entity ent2) {
        return DamageSource.a(map(ent), map(ent2));
    }

    public static DamageSource firework(Firework firework, Entity ent) {
        return DamageSource.a(map(firework), map(ent));
    }

    public static DamageSource fireball(LargeFireball fireball, Entity ent) {
        return DamageSource.fireball(map(fireball), map(ent));
    }

    public static DamageSource witherSkull(WitherSkull witherSkull, Entity ent) {
        return DamageSource.a(map(witherSkull), map(ent));
    }

    public static DamageSource projectile(Entity ent, Entity ent2) {
        return DamageSource.projectile(map(ent), map(ent2));
    }

    public static DamageSource indirectMagic(Entity ent, Entity ent2) {
        return DamageSource.c(map(ent), map(ent2));
    }

    public static DamageSource thorns(Entity ent) {
        return DamageSource.a(map(ent));
    }

    public static DamageSource explosion(LivingEntity liv) {
        return DamageSource.d(map(liv));
    }

    public static DamageSource netherBed() {
        return DamageSource.a();
    }

    public static void setMessageOfTheDay(String msg) {
        ((CraftServer) Bukkit.getServer()).getServer().setMotd(msg);
    }

    public static String toString(ItemStack stack) {
        NBTTagCompound c = new NBTTagCompound();
        map(stack).save(c);
        return c.toString();
    }

    public static ItemStack parseItemStack(String stack) throws Exception {
        NBTTagCompound c = MojangsonParser.parse(stack);
        return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(c));
    }

    public static String toString(Entity ent) {
        NBTTagCompound c = new NBTTagCompound();
        map(ent).d(c);
        return c.toString();
    }

    public static Entity parseEntity(String stack, Location l) throws Exception {
        NBTTagCompound c = MojangsonParser.parse(stack);
        var nmsWorld = map(l.getWorld());
        net.minecraft.world.entity.Entity ent =
                net.minecraft.world.entity.EntityTypes.a(c, nmsWorld, e -> {
                    e.a_(UUID.randomUUID());
                    e.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                    return nmsWorld.addEntity(e) ? e : null;
                });
        return map(ent);
    }

    private static BlockPosition convert(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static void copyTileEntity(Location from, Location to) {
        var nmsFromWorld = map(from.getWorld());
        var nmsToWorld = map(to.getWorld());

        var fromEntity = nmsFromWorld.getTileEntity(convert(from));
        var toEntity = nmsToWorld.getTileEntity(convert(to));
        KajetansPlugin.log(String.valueOf(fromEntity));
        KajetansPlugin.log(String.valueOf(toEntity));
        if(fromEntity != null && toEntity != null && fromEntity.getClass() == toEntity.getClass()) {
            NBTTagCompound nbtTagCompound = fromEntity.save(new NBTTagCompound());
            toEntity.load(nbtTagCompound);
        }
    }
}
