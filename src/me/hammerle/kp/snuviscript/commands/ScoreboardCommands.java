/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.km.overrides.ModEntityPlayerMP;
import me.km.permissions.Permissions;
import static me.km.snuviscript.commands.CommandUtils.doForGroup;
import me.km.snuviscript.Scripts;
import net.minecraft.server.MinecraftServer;

public class ScoreboardCommands {
    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms, MinecraftServer server) {
        sm.registerConsumer("sb.add", (sc, in) -> {
            int id = in[1].getInt(sc);
            String message = SnuviUtils.connect(sc, in, 2);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ((ModEntityPlayerMP) p).getScoreboard().addText(id, message));
        });
        sm.registerConsumer("sb.remove", (sc, in) -> {
            int id = in[1].getInt(sc);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ((ModEntityPlayerMP) p).getScoreboard().removeText(id));
        });
        sm.registerConsumer("sb.reset", (sc, in) -> {
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ((ModEntityPlayerMP) p).getScoreboard().clear((ModEntityPlayerMP) p));
        });
    }
}
*/
