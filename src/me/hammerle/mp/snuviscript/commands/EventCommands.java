package me.hammerle.mp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.Location;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.snuviscript.MoveEvents;

public class EventCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("event.addmovedata", (sc, in) -> {
            UUID uuid = in.length >= 5 ? CommandUtils.getUUID(in[4].get(sc)) : null;
            MoveEvents.Data pmd = new MoveEvents.Data(sc, (Location) in[0].get(sc),
                    (Location) in[1].get(sc), in[2].getInt(sc), in[3].getInt(sc), uuid);
            return (double) MoveEvents.add(pmd);
        });
        MundusPlugin.scriptManager.registerConsumer("event.removemovedata",
                (sc, in) -> MoveEvents.remove(in[0].getInt(sc)));
    }
}
