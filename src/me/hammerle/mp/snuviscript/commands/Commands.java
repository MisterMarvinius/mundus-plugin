package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.snuviscript.config.SnuviConfig;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;

public class Commands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("setmotd", (sc, in) -> {
            org.bukkit.Bukkit.motd(Component.text(in[0].getString(sc)));
        });
        MundusPlugin.scriptManager.registerFunction("getmotd", (sc, in) -> {
            return org.bukkit.Bukkit.motd();
        });
        MundusPlugin.scriptManager.registerConsumer("msg", (sc, in) -> {
            CommandUtils.sendMessageToGroup(in[0].get(sc), sc, (Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerFunction("isplayer", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof Player && !((Player) o).hasMetadata("NPC");
        });
        MundusPlugin.scriptManager.registerFunction("isliving", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof LivingEntity;
        });
        MundusPlugin.scriptManager.registerFunction("iscitizen", (sc, in) -> {
            Object o = in[0].get(sc);
            if(o == null) {
                return false;
            }
            if(o instanceof NPC) {
                return true;
            }
            return o instanceof Entity && ((Entity) o).hasMetadata("NPC");
        });
        MundusPlugin.scriptManager.registerConsumer("config.saveasync", (sc, in) -> {
            SnuviConfig config = (SnuviConfig) in[0].get(sc);
            MundusPlugin.scheduleAsyncTask(() -> config.save(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("stop", (sc, in) -> {
            Bukkit.shutdown();
        });
    }
}
