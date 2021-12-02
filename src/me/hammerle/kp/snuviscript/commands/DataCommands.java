/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.overrides.ModEntityPlayerMP;
import me.km.scheduler.SnuviScheduler;

public class DataCommands {
    public static void registerFunctions(ScriptManager sm, SnuviScheduler scheduler) {
        sm.registerConsumer("data.set", (sc, in) -> {
            ((ModEntityPlayerMP) in[0].get(sc)).setVar(in[1].getString(sc), in[2].get(sc));
        });
        sm.registerConsumer("data.settimer", (sc, in) -> {
            ((ModEntityPlayerMP) in[0].get(sc)).setTimer(in[1].getString(sc), in[2].getInt(sc), scheduler);
        });
        sm.registerFunction("data.get", (sc, in) -> {
            return ((ModEntityPlayerMP) in[0].get(sc)).getVar(in[1].getString(sc));
        });
        sm.registerFunction("data.gettimer", (sc, in) -> {
            return (double) ((ModEntityPlayerMP) in[0].get(sc)).getTimer(in[1].getString(sc));
        });
        sm.registerConsumer("data.clear", (sc, in) -> {
            ((ModEntityPlayerMP) in[0].get(sc)).clearData(scheduler);
        });
    }
}
*/
