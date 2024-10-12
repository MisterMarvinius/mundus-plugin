package me.hammerle.mp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.NMS;

public class ReadCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("read.player", (sc, in) -> {
            String name = in[0].getString(sc);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.getName().equals(name)) {
                    return p;
                }
            }
            return null;
        });
        MundusPlugin.scriptManager.registerFunction("read.item", (sc, in) -> {
            String s = in[0].getString(sc);
            if(s.contains("minecraft:air")) {
                return new ItemStack(Material.AIR);
            }
            ReadWriteNBT nbt = NBT.parseNBT(s);
            return NBT.itemStackFromNBT(nbt);
        });
        MundusPlugin.scriptManager.registerFunction("read.spawnmob", (sc, in) -> {
            return NMS.parseEntity(in[1].getString(sc), (Location) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("read.uuid", (sc, in) -> {
            String s = in[0].getString(sc);
            try {
                return UUID.fromString(s);
            } catch(Exception ex) {
                return null;
            }
        });
        MundusPlugin.scriptManager.registerFunction("read.slot", (sc, in) -> {
            String s = in[0].getString(sc);
            try {
                return EquipmentSlot.valueOf(s);
            } catch(Exception ex) {
                return null;
            }
        });
        MundusPlugin.scriptManager.registerFunction("read.blockdata", (sc, in) -> {
            String s = in[0].getString(sc);
            try {
                return Bukkit.createBlockData(s);
            } catch(Exception ex) {
                return null;
            }
        });
    }
}
