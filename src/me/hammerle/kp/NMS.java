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
import org.bukkit.craftbukkit.v1_18_R1.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.snuviscript.ScriptEvents;
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
import net.minecraft.world.level.block.entity.TileEntity;
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
            HUMAN_TYPE =
                    (EntityTypes<RawHuman.WrapperHuman>) m.invoke(null, "human", EntityTypes.Builder
                            .a(RawHuman.WrapperHuman::new, EnumCreatureType.a).a(0.6f, 1.95f));

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
                    EntityMonster.fD().a(GenericAttributes.a, 20.0).a(GenericAttributes.f, 1)
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

        public void setName(String name);
    }

    private static class RawHuman extends CraftCreature implements Human {
        private static class WrapperHuman extends EntityCreature {
            private EntityPlayer player;

            public WrapperHuman(EntityTypes<? extends WrapperHuman> type,
                    net.minecraft.world.level.World world) {
                super(type, world);
                setPlayer("Default", world);
            }

            private void setPlayer(String name, net.minecraft.world.level.World world) {
                player = new EntityPlayer(getCraftServer().getServer(), (WorldServer) world,
                        new GameProfile(cm(), name));
                player.e(getBukkitEntity().getEntityId());
            }

            @Override
            public boolean a(DamageSource ds, float amount) {
                if(ScriptEvents.onHumanHurt(ds, getWrappedEntity(), amount)) {
                    return false;
                }
                return super.a(ds, amount);
            }

            public RawHuman getWrappedEntity() {
                return new RawHuman(this);
            }

            @Override
            public CraftEntity getBukkitEntity() {
                return getWrappedEntity();
            }

            public EntityPlayer update(PacketPlayOutSpawnEntityLiving p) {
                player.e(p.b()); // set id, get id
                player.a(p.e(), p.f(), p.g(), 0.0f, 0.0f);
                return player;
            }

            @Override
            public void b(NBTTagCompound nbt) {
                super.b(nbt);

                GameProfile gp = player.fp();
                nbt.a("HumanName", gp.getName());

                Collection<Property> c = gp.getProperties().get("textures");
                for(Property p : c) {
                    if(p.getName().equals("textures")) {
                        nbt.a("HumanTexture", p.getValue());
                        nbt.a("HumanSignature", p.getSignature());
                        break;
                    }
                }
            }

            @Override
            public void a(NBTTagCompound nbt) {
                super.a(nbt);
                if(nbt.b("HumanName", 8)) {
                    String name = nbt.l("HumanName");
                    setPlayer(name, player.t);
                }
                if(nbt.b("HumanTexture", 8) && nbt.b("HumanSignature", 8)) {
                    String texture = nbt.l("HumanTexture");
                    String signature = nbt.l("HumanSignature");
                    setSkinWithoutPacket(texture, signature);
                }
            }

            private void updatePosition() {
                Location l = getBukkitEntity().getLocation();
                player.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            }

            public void setSkinWithoutPacket(String texture, String signature) {
                GameProfile gp = player.fp();
                gp.getProperties().clear();
                gp.getProperties().put("textures", new Property("textures", texture, signature));
            }

            private void sync() {
                updatePosition();
                PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, player);
                PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);

                Location center = getBukkitEntity().getLocation();
                for(EntityPlayer p : ((WorldServer) this.W()).z()) {
                    double distance = center.distanceSquared(p.getBukkitEntity().getLocation());
                    if(distance < 100.0) {
                        p.b.a(info);
                        p.b.a(spawn);
                    }
                }
            }

            public void setSkin(String texture, String signature) {
                setSkinWithoutPacket(texture, signature);
                sync();
            }

            public void setName(String name) {
                setPlayer(name, player.t);
                sync();
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
            human.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            human.W().b(human);
        }

        public RawHuman(Location l, PlayerProfile profile) {
            this(HUMAN_TYPE.a(map(l.getWorld())), l, profile);
        }

        public void setSkin(String texture, String signature) {
            human.setSkin(texture, signature);
        }

        public void setName(String name) {
            human.setName(name);
        }
    }

    public static Human createHuman(String name, Location l) {
        return new RawHuman(l, Bukkit.createProfile(UUID.randomUUID(), name));
    }

    private static class PluginConnection extends PlayerConnection {
        public PluginConnection(PlayerConnection c) {
            super(getCraftServer().getServer(), c.a, c.b);
        }

        @SuppressWarnings("deprecation")
        private RawHuman.WrapperHuman getById(int id) {
            net.minecraft.world.entity.Entity ent = b.x().b(id);
            if(ent instanceof RawHuman.WrapperHuman) {
                return (RawHuman.WrapperHuman) ent;
            }
            return null;
        }

        private boolean handle(PacketPlayOutSpawnEntityLiving p) {
            if(p.d() != IRegistry.Z.a(HUMAN_TYPE)) {
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
            super.a(info);
            super.a(spawn);
            return true;
        }

        private boolean handle(PacketPlayOutEntityMetadata p) {
            return getById(p.c()) != null;
        }

        private void handle(PacketPlayOutEntityDestroy p) {
            for(int id : p.b()) {
                RawHuman.WrapperHuman human = getById(id);
                if(human != null) {
                    PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, human.player);
                    super.a(remove);
                }
            }
        }

        @Override
        public void a(Packet<?> p) {
            if(p instanceof PacketPlayOutSpawnEntityLiving
                    && handle((PacketPlayOutSpawnEntityLiving) p)) {
                return;
            } else if(p instanceof PacketPlayOutEntityMetadata
                    && handle((PacketPlayOutEntityMetadata) p)) {
                return;
            } else if(p instanceof PacketPlayOutEntityDestroy) {
                handle((PacketPlayOutEntityDestroy) p);
            }
            super.a(p);
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
        if(ds.l() == null) {
            return null;
        }
        return map(ds.l());
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
        return DamageSource.c(map(liv));
    }

    public static DamageSource mobIndirect(Entity ent, LivingEntity liv) {
        return DamageSource.a(map(ent), map(liv));
    }

    public static DamageSource playerAttack(Player p) {
        return DamageSource.a(map(p));
    }

    public static DamageSource arrow(Arrow arrow, Entity ent) {
        return DamageSource.a(map(arrow), map(ent));
    }

    public static DamageSource trident(Entity ent, Entity ent2) {
        return DamageSource.a(map(ent), map(ent2));
    }

    public static DamageSource firework(Firework firework, Entity ent) {
        return DamageSource.a(map(firework), map(ent));
    }

    public static DamageSource fireball(LargeFireball fireball, Entity ent) {
        return DamageSource.a(map(fireball), map(ent));
    }

    public static DamageSource witherSkull(WitherSkull witherSkull, Entity ent) {
        return DamageSource.a(map(witherSkull), map(ent));
    }

    public static DamageSource projectile(Entity ent, Entity ent2) {
        return DamageSource.a(map(ent), map(ent2));
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
        ((CraftServer) Bukkit.getServer()).getServer().e(msg);
    }

    public static String toString(ItemStack stack) {
        NBTTagCompound c = new NBTTagCompound();
        map(stack).b(c);
        return c.toString();
    }

    public static ItemStack parseItemStack(String stack) throws Exception {
        NBTTagCompound c = MojangsonParser.a(stack);
        return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(c));
    }

    public static String toString(Entity ent) {
        NBTTagCompound c = new NBTTagCompound();
        map(ent).d(c);
        return c.toString();
    }

    public static Entity parseEntity(String stack, Location l) throws Exception {
        NBTTagCompound c = MojangsonParser.a(stack);
        var nmsWorld = map(l.getWorld());
        net.minecraft.world.entity.Entity ent =
                net.minecraft.world.entity.EntityTypes.a(c, nmsWorld, e -> {
                    e.a_(UUID.randomUUID());
                    e.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                    return nmsWorld.b(e) ? e : null;
                });
        return map(ent);
    }

    private static BlockPosition convert(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static void copyTileEntity(Location from, Location to) {
        var nmsFromWorld = map(from.getWorld());
        var nmsToWorld = map(to.getWorld());

        TileEntity fromEntity = nmsFromWorld.c_(convert(from));
        TileEntity toEntity = nmsToWorld.c_(convert(to));
        if(fromEntity != null && toEntity != null && fromEntity.getClass() == toEntity.getClass()) {
            NBTTagCompound nbtTagCompound = fromEntity.m();
            toEntity.a(nbtTagCompound);
        }
    }
}
