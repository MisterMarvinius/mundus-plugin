package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.Metadatable;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.snuviscript.config.SnuviConfig;
import net.kyori.adventure.text.Component;

public class Commands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("setmotd", (sc, in) -> {
            NMS.setMessageOfTheDay(in[0].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("msg", (sc, in) -> {
            CommandUtils.sendMessageToGroup(in[0].get(sc), sc, (Component) in[1].get(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("isplayer", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof Player && !((Player)o).hasMetadata("NPC");
        });
        KajetansPlugin.scriptManager.registerFunction("isliving", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof LivingEntity;
        });
        KajetansPlugin.scriptManager.registerFunction("iscitizen", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && ((Metadatable)o).hasMetadata("NPC");
        });
        KajetansPlugin.scriptManager.registerConsumer("config.saveasync", (sc, in) -> {
            SnuviConfig config = (SnuviConfig) in[0].get(sc);
            KajetansPlugin.scheduleAsyncTask(() -> config.save(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("stop", (sc, in) -> {
            Bukkit.shutdown();
        });
    }
}
