package me.hammerle.kp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftFirework;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLargeFireball;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftWitherSkull;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.inventory.ItemStack;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireballFireball;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.EntityWitherSkull;

public class NMS {
    private final static HashMap<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();

    private static CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public static interface Human extends Player {
        public void setSkin(String texture, String signature);
    }

    private static class RawHuman extends CraftPlayer implements Human {
        private static class DummyNetworkManager extends NetworkManager {
            public DummyNetworkManager() {
                super(EnumProtocolDirection.b);
            }
        }

        private static class WrapperHuman extends EntityPlayer {
            public WrapperHuman(World w, PlayerProfile profile) {
                super(getCraftServer().getServer(), map(w), map(profile));
                b = new PlayerConnection(getCraftServer().getServer(), new DummyNetworkManager(),
                        this);
            }

            @Override
            public void tick() {
                super.tick();
                Player p = getBukkitEntity();
                if(p.isDead()) {
                    p.remove();
                }
                KajetansPlugin.log("TICK");
            }

            @Override
            public boolean damageEntity(DamageSource ds, float amount) {
                if(ds.ignoresInvulnerability()) {
                    super.damageEntity(ds, amount);
                }
                return false;
            }
        }

        private WrapperHuman human;

        private RawHuman(WrapperHuman human, float yaw) {
            super(getCraftServer(), human);
            this.human = human;
            human.getWorld().addEntity(human);

            PacketPlayOutPlayerInfo playerInfoAdd = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, human);
            PacketPlayOutNamedEntitySpawn namedEntitySpawn =
                    new PacketPlayOutNamedEntitySpawn(human);
            PacketPlayOutEntityHeadRotation headRotation =
                    new PacketPlayOutEntityHeadRotation(human, (byte) ((yaw * 256f) / 360f));
            PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, human);

            for(Player player : Bukkit.getOnlinePlayers()) {
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                connection.sendPacket(playerInfoAdd);
                connection.sendPacket(namedEntitySpawn);
                connection.sendPacket(headRotation);
                connection.sendPacket(playerInfoRemove);
            }
        }

        public RawHuman(Location l, PlayerProfile profile) {
            this(new WrapperHuman(l.getWorld(), profile), l.getYaw());
            teleport(l);
        }

        public void setSkin(String texture, String signature) {
            GameProfile gp = human.getProfile();
            gp.getProperties().clear();
            texture =
                    "ewogICJ0aW1lc3RhbXAiIDogMTU4OTA1MzMyMjY3NiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4OTBjN2U1OGYyM2U5N2ZmNGRkYWMwNDhiYmZhMDJkNjYxMTEwMTNkMmYxNzdjNWY4ZjYyYThiMWIxYWZkZCIKICAgIH0KICB9Cn0=";
            signature =
                    "Skp8QvxYFa4YUhw+XHgicva/8j3gnCcN9u0EDLpkSlVCWuYqUeETbgA3LAit4ftjDjNpUA40UPTvlebBsnjcUvEkiu765BvZE61yms2IcNeK7vDoLoeNfx+UqTAquMI6uOzBBNZi6yBeMSghRX2hwVGsiKuzoFb67o1HfFcPLfCOVR3QRd2D84VfduhQmc+MVSFrUhZFGluzOvslUzR8/tbi2ZarkURlLOlgT0UoT1yEX/pHM7GogtnQiJL7xOqfEnU0Ex+OKZYkjawatbD/L5bjbL1pV2QeuxZLrnvRoQFTVAnONhvPfd9f8WF0kdR8DaDe4Knq+SPJ357HOun9ZRci3RobXcyQaRsw5JezSrgUbBccoUr7SiSgdM4VBhtzGGZ8TYUBz5pocHYCaOALG71bZZ4aVjQKfw5Rtalj+q2Wqbub20IQd/7/z9NUvPB0d7zHBLqr8a1UtZoSKLbaVJZJaYqt0ygxff68MKKQlE0L4fupBHEIXdNgza8tp472rsB+o45IZ/xmFltH1jhRsYvV973ki0l4S6U/O6gWu699sUyHn4a3DnVNN0GIyNAIP9KpHhvQzvxPxJq0Z2gXw2rzRGDxt+fe8gYZJ+UF4t/i39IP9RBgryocdu0L0lzeQA0b7vrr1khvAHyBVuZJ0t2S/RHTnlcAcAxoDENP1Gk=";
            gp.getProperties().put("textures", new Property("textures", texture, signature));

            for(Player pl : Bukkit.getOnlinePlayers()) {
                pl.hidePlayer(KajetansPlugin.instance, this);
                pl.showPlayer(KajetansPlugin.instance, this);
            }
        }
    }

    public static Human createHuman(String name, Location l) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        Human human = new RawHuman(l, profile);
        return human;
    }

    public static DamageSource getCurrentDamageSource() {
        return CraftEventFactory.currentDamageCause;
    }

    public static Entity getImmediateSource(DamageSource ds) {
        return map(ds.k());
    }

    public static Entity getTrueSource(DamageSource ds) {
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
