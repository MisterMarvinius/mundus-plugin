package me.hammerle.kp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.Location;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.snuviscript.MoveEvents;

public class EventCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("event.addmovedata", (sc, in) -> {
            UUID uuid = in.length >= 5 ? CommandUtils.getUUID(in[4].get(sc)) : null;
            MoveEvents.Data pmd = new MoveEvents.Data(sc, (Location) in[0].get(sc),
                    (Location) in[1].get(sc), in[2].getInt(sc), in[3].getInt(sc), uuid);
            return (double) MoveEvents.add(pmd);
        });
        KajetansPlugin.scriptManager.registerConsumer("event.removemovedata",
                (sc, in) -> MoveEvents.remove(in[0].getInt(sc)));
    }
}
