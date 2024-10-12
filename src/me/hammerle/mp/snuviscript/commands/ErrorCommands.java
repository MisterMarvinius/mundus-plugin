package me.hammerle.mp.snuviscript.commands;

import me.hammerle.mp.MundusPlugin;

public class ErrorCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("error.clear", (sc, in) -> {
            MundusPlugin.logger.getErrorHistory().clear();
        });
        MundusPlugin.scriptManager.registerFunction("error.getsize", (sc, in) -> {
            return (double) MundusPlugin.logger.getErrorHistory().getLength();
        });
        MundusPlugin.scriptManager.registerFunction("error.getindex", (sc, in) -> {
            return MundusPlugin.logger.getErrorHistory().get(in[0].getInt(sc)).message;
        });
        MundusPlugin.scriptManager.registerFunction("error.getindextime", (sc, in) -> {
            return (double) MundusPlugin.logger.getErrorHistory().get(in[0].getInt(sc)).timestamp;
        });
        MundusPlugin.scriptManager.registerConsumer("error.setconsoleprint", (sc, in) -> {
            MundusPlugin.logger.setConsoleErrorLogging(in[0].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("debug.clear", (sc, in) -> {
            MundusPlugin.logger.getDebugHistory().clear();
        });
        MundusPlugin.scriptManager.registerFunction("debug.getsize", (sc, in) -> {
            return (double) MundusPlugin.logger.getDebugHistory().getLength();
        });
        MundusPlugin.scriptManager.registerFunction("debug.getindex", (sc, in) -> {
            return MundusPlugin.logger.getDebugHistory().get(in[0].getInt(sc)).message;
        });
        MundusPlugin.scriptManager.registerFunction("debug.getindextime", (sc, in) -> {
            return (double) MundusPlugin.logger.getDebugHistory().get(in[0].getInt(sc)).timestamp;
        });
        MundusPlugin.scriptManager.registerConsumer("debug.setconsoleprint", (sc, in) -> {
            MundusPlugin.logger.setConsoleDebugLogging(in[0].getBoolean(sc));
        });
    }
}
