/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.hammerle.snuviscript.config.SnuviConfig;
import me.hammerle.snuviscript.exceptions.StackTrace;
import me.km.permissions.ModCommandManager;
import me.km.permissions.Permissions;
import me.km.scheduler.SnuviScheduler;
import me.km.snuviscript.Scripts;
import static me.km.snuviscript.commands.CommandUtils.concat;
import static me.km.snuviscript.commands.CommandUtils.sendMessageToGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

public class Commands {
    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms,
            SnuviScheduler scheduler, MinecraftServer server, ModCommandManager commands) {
        sm.registerConsumer("setmotd", (sc, in) -> {
            server.getServerStatusResponse()
                    .setServerDescription(new StringTextComponent(in[0].getString(sc)));
        });
        sm.registerConsumer("msg", (sc, in) -> {
            sendMessageToGroup(server, scripts, perms, in[0].get(sc), sc, concat(sc, 1, "", in));
        });
        sm.registerFunction("removeformat",
                (sc, in) -> SnuviUtils.connect(sc, in, 0).replaceAll("§.", ""));
        sm.registerConsumer("command", (sc, in) -> {
            final String s = SnuviUtils.connect(sc, in, 0);
            StackTrace trace = sc.getStackTrace();
            scheduler.scheduleTask("command", () -> {
                try {
                    commands.handleCommand(server.getCommandSource(), s);
                } catch(Exception ex) {
                    sc.getScriptManager().getLogger().print(null, ex, "command", sc.getName(), sc,
                            trace);
                }
            });
        });
        sm.registerFunction("isplayer", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof PlayerEntity;
        });
        sm.registerFunction("isliving", (sc, in) -> {
            Object o = in[0].get(sc);
            return o != null && o instanceof LivingEntity;
        });
        sm.registerConsumer("config.saveasync", (sc, in) -> {
            SnuviConfig config = (SnuviConfig) in[0].get(sc);
            scheduler.scheduleAsyncTask(() -> config.save(sc));
        });
        sm.registerFunction("text.concat2", (sc, in) -> concat(sc, 0, "", in));
        sm.registerAlias("text.concat2", "concat2");
    }
}
*/
