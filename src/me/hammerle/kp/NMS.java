package me.hammerle.kp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
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
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireballFireball;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.EntityWitherSkull;

public class NMS {
    private final static HashMap<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();

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

    public static net.minecraft.world.level.World map(World e) {
        return ((CraftWorld) e).getHandle();
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

    public static ItemStack parseItemStack(String stack) {
        try {
            NBTTagCompound c = MojangsonParser.parse(stack);
            return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(c));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String toString(Entity ent) {
        NBTTagCompound c = new NBTTagCompound();
        map(ent).d(c);
        return c.toString();
    }

    public static Entity parseEntity(String stack, Location l) {
        try {
            NBTTagCompound c = MojangsonParser.parse(stack);
            var nmsWorld = map(l.getWorld());
            net.minecraft.world.entity.Entity ent =
                    net.minecraft.world.entity.EntityTypes.a(c, nmsWorld, e -> {
                        e.a_(UUID.randomUUID());
                        e.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                        return nmsWorld.addEntity(e) ? e : null;
                    });
            return map(ent);
        } catch(Exception ex) {
        }
        return null;
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
