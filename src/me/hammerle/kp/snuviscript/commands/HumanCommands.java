package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.kp.NMS.Human;

public class HumanCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("human.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return NMS.createHuman("Franz", l);
        });
        KajetansPlugin.scriptManager.registerConsumer("human.set", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setSkin(in[1].getString(sc), in[2].getString(sc));
        });
        /*KajetansPlugin.scriptManager.registerConsumer("human.setstatue", (sc, in) -> {
        ((EntityHuman) in[0].get(sc)).setStatue(in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setskin", (sc, in) -> {
        ((EntityHuman) in[0].get(sc)).setSkinName(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setscale", (sc, in) -> {
        ((EntityHuman) in[0].get(sc)).setScale(in[1].getFloat(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setslim", (sc, in) -> {
        ((EntityHuman) in[0].get(sc)).setSlim(in[1].getBoolean(sc));
        });*/
    }
}
