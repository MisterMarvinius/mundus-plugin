package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import me.hammerle.kp.NMS.Human;

public class HumanCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("human.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return NMS.createHuman(in[1].getString(sc), l);
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setskin", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setSkin(in[1].getString(sc), in[2].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("human.setname", (sc, in) -> {
            Human h = (Human) in[0].get(sc);
            h.setName(in[1].getString(sc));
        });
    }
}
