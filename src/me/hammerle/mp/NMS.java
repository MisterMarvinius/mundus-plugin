package me.hammerle.mp;

import java.util.UUID;
import org.bukkit.craftbukkit.v1_21_R1.entity.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.entity.*;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityLiving;

public class NMS {

    public static Entity map(net.minecraft.world.entity.Entity e) {
        return e.getBukkitEntity();
    }

    public static EntityLiving map(LivingEntity e) {
        return ((CraftLivingEntity) e).getHandle();
    }

    public static WorldServer map(World e) {
        return ((CraftWorld) e).getHandle();
    }

    private static NBTTagCompound parse(String s) throws Exception {
        return MojangsonParser.a(s);
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
}
