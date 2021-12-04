package me.hammerle.kp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;

public class ReadCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("read.player", (sc, in) -> {
            String name = in[0].getString(sc);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.getName().equals(name)) {
                    return p;
                }
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerFunction("read.item",
                (sc, in) -> NMS.parseItemStack(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("read.spawnmob", (sc, in) -> {
            return NMS.parseEntity(in[1].getString(sc), (Location) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("read.uuid",
                (sc, in) -> UUID.fromString(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("read.slot",
                (sc, in) -> EquipmentSlot.valueOf(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("read.blockdata",
                (sc, in) -> Bukkit.createBlockData(in[0].getString(sc)));
    }
}
