package me.hammerle.kp;

import java.util.UUID;
import org.bukkit.craftbukkit.v1_20_R3.entity.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;

public class NMS {
    public static void init() {
        net.minecraft.world.item.ItemStack.maxStackSizeHook = (stack, vanilla) -> {
            return CustomItems.getMaxStackSize(stack.getBukkitStack(), vanilla);
        };
        ItemStack.maxStackSizeHook = (stack, vanilla) -> {
            return CustomItems.getMaxStackSize(stack, vanilla);
        };
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

    public static WorldServer map(World e) {
        return ((CraftWorld) e).getHandle();
    }

    public static IBlockData map(Block b) {
        return ((CraftBlock) b).getNMS();
    }

    public static String toString(ItemStack stack) {
        NBTTagCompound c = new NBTTagCompound();
        map(stack).b(c);
        return c.toString();
    }

    private static NBTTagCompound parse(String s) throws Exception {
        return MojangsonParser.a(s);
    }

    public static ItemStack parseItemStack(String stack) {
        try {
            return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(parse(stack)));
        } catch(Exception ex) {
            return null;
        }
    }

    public static String toString(Entity ent) {
        NBTTagCompound c = new NBTTagCompound();
        map(ent).d(c);
        return c.toString();
    }

    public static Entity parseEntity(String stack, Location l) {
        try {
            NBTTagCompound c = parse(stack);
            var nmsWorld = map(l.getWorld());
            net.minecraft.world.entity.Entity ent =
                    net.minecraft.world.entity.EntityTypes.a(c, nmsWorld, e -> {
                        e.a_(UUID.randomUUID());
                        e.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                        return nmsWorld.b(e) ? e : null;
                    });
            return map(ent);
        } catch(Exception ex) {
            return null;
        }
    }

    private static BlockPosition convert(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static void copyTileEntity(Location from, Location to) {
        var nmsFromWorld = map(from.getWorld());
        var nmsToWorld = map(to.getWorld());

        BlockPosition posTo = convert(to);
        TileEntity fromEntity = nmsFromWorld.c_(convert(from));
        TileEntity toEntity = nmsToWorld.c_(posTo);
        if(fromEntity != null && toEntity != null && fromEntity.getClass() == toEntity.getClass()) {
            NBTTagCompound nbtTagCompound = fromEntity.o();
            toEntity.a(nbtTagCompound);
            Block b = to.getBlock();
            nmsToWorld.a(posTo, map(b), map(b), 3);
        }
    }

    public static NBTTagCompound getBlockEntity(String s) {
        try {
            return parse(s);
        } catch(Exception ex) {
            return null;
        }
    }

    public static NBTTagCompound getEntity(Block b) {
        Location l = b.getLocation();
        var nmsWorld = map(l.getWorld());
        TileEntity te = nmsWorld.c_(convert(l));
        if(te == null) {
            return null;
        }
        return te.o();
    }

    public static void setEntity(Block b, NBTTagCompound nbt) {
        if(nbt == null) {
            return;
        }
        Location l = b.getLocation();
        var nmsWorld = map(l.getWorld());
        BlockPosition pos = convert(l);
        TileEntity te = nmsWorld.c_(pos);
        if(te != null) {
            te.a(nbt);
            nmsWorld.a(pos, map(b), map(b), 3);
        }
    }

    public static NBTTagCompound toNBT(Object o) {
        return (NBTTagCompound) o;
    }

    public static void resetSleepTimer(Player p) {
        // this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        map(p).a(StatisticList.i.b(StatisticList.n));
    }
}
