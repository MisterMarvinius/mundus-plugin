package me.hammerle.mp;

import org.bukkit.craftbukkit.entity.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class NMS {

    public static Entity map(net.minecraft.world.entity.Entity nmsEntity) {
        return nmsEntity.getBukkitEntity();
    }

    public static LivingEntity map(org.bukkit.entity.LivingEntity bukkitLiving) {
        return ((CraftLivingEntity) bukkitLiving).getHandle();
    }

    public static ServerLevel map(World bukkitWorld) {
        return ((CraftWorld) bukkitWorld).getHandle();
    }
}
