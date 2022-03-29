package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;

public class ErrorCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("error.clear", (sc, in) -> {
            KajetansPlugin.logger.getErrorHistory().clear();
        });
        KajetansPlugin.scriptManager.registerFunction("error.getsize", (sc, in) -> {
            return (double) KajetansPlugin.logger.getErrorHistory().getLength();
        });
        KajetansPlugin.scriptManager.registerFunction("error.getindex", (sc, in) -> {
            return KajetansPlugin.logger.getErrorHistory().get(in[0].getInt(sc)).message;
        });
        KajetansPlugin.scriptManager.registerFunction("error.getindextime", (sc, in) -> {
            return (double) KajetansPlugin.logger.getErrorHistory().get(in[0].getInt(sc)).timestamp;
        });
        KajetansPlugin.scriptManager.registerConsumer("error.setconsoleprint", (sc, in) -> {
            KajetansPlugin.logger.setConsoleErrorLogging(in[0].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("debug.clear", (sc, in) -> {
            KajetansPlugin.logger.getDebugHistory().clear();
        });
        KajetansPlugin.scriptManager.registerFunction("debug.getsize", (sc, in) -> {
            return (double) KajetansPlugin.logger.getDebugHistory().getLength();
        });
        KajetansPlugin.scriptManager.registerFunction("debug.getindex", (sc, in) -> {
            return KajetansPlugin.logger.getDebugHistory().get(in[0].getInt(sc)).message;
        });
        KajetansPlugin.scriptManager.registerFunction("debug.getindextime", (sc, in) -> {
            return (double) KajetansPlugin.logger.getDebugHistory().get(in[0].getInt(sc)).timestamp;
        });
        KajetansPlugin.scriptManager.registerConsumer("debug.setconsoleprint", (sc, in) -> {
            KajetansPlugin.logger.setConsoleDebugLogging(in[0].getBoolean(sc));
        });
    }
}
