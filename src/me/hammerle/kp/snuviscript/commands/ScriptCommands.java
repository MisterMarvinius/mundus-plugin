/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.Script;
import me.hammerle.snuviscript.code.ScriptManager;
import me.km.snuviscript.Scripts;
import net.minecraft.server.MinecraftServer;

public class ScriptCommands {
    private static void nothing(Script sc) {
    }

    public static void registerFunctions(ScriptManager sm, Scripts scripts, MinecraftServer server) {
        sm.registerConsumer("script.callevent", (sc, in) -> {
            String name = in[0].getString(sc);
            if(in.length >= 2) {
                sm.callEvent(name, (Script) in[1].get(sc), ScriptCommands::nothing, ScriptCommands::nothing);
            } else {
                sm.callEvent(name, ScriptCommands::nothing, ScriptCommands::nothing);
            }
        });
        sm.registerFunction("script.start", (sc, in) -> {
            String[] names = new String[in.length];
            for(int i = 0; i < in.length; i++) {
                names[i] = in[i].getString(sc);
            }
            return scripts.startScript(null, names);
        });
        sm.registerFunction("script.startnamed", (sc, in) -> {
            String[] names = new String[in.length - 1];
            for(int i = 0; i < names.length; i++) {
                names[i] = in[i + 1].getString(sc);
            }
            return scripts.startScript(in[0].getString(sc), names);
        });
    }
}
*/
