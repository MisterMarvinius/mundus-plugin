package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import me.hammerle.snuviscript.code.Script;

public class ScriptCommands {
    private static void nothing(Script sc) {}

    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("script.callevent", (sc, in) -> {
            String name = in[0].getString(sc);
            if(in.length >= 2) {
                KajetansPlugin.scriptManager.callEvent(name, (Script) in[1].get(sc),
                        ScriptCommands::nothing, ScriptCommands::nothing);
            } else {
                KajetansPlugin.scriptManager.callEvent(name, ScriptCommands::nothing,
                        ScriptCommands::nothing);
            }
        });
        KajetansPlugin.scriptManager.registerFunction("script.start", (sc, in) -> {
            String[] names = new String[in.length];
            for(int i = 0; i < in.length; i++) {
                names[i] = in[i].getString(sc);
            }
            return KajetansPlugin.startScript(null, names);
        });
        KajetansPlugin.scriptManager.registerFunction("script.startnamed", (sc, in) -> {
            String[] names = new String[in.length - 1];
            for(int i = 0; i < names.length; i++) {
                names[i] = in[i + 1].getString(sc);
            }
            return KajetansPlugin.startScript(in[0].getString(sc), names);
        });
    }
}
