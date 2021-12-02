/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.km.networking.ModPacketHandler;
import me.km.permissions.Permissions;
import me.km.snuviscript.Scripts;
import static me.km.snuviscript.commands.CommandUtils.doForGroup;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class StatusCommands {
    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms, MinecraftServer server) {
        sm.registerConsumer("status.add", (sc, in) -> {
            byte index = in[1].getByte(sc);
            String message = SnuviUtils.connect(sc, in, 2);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ModPacketHandler.addStatus((ServerPlayerEntity) p, index, message));
        });
        sm.registerConsumer("status.addtimed", (sc, in) -> {
            byte index = in[1].getByte(sc);
            int time = in[2].getInt(sc);
            String message = SnuviUtils.connect(sc, in, 3);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ModPacketHandler.addTimedStatus((ServerPlayerEntity) p, index, message, time));
        });
        sm.registerConsumer("status.remove", (sc, in) -> {
            byte index = in[1].getByte(sc);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ModPacketHandler.removeStatus((ServerPlayerEntity) p, index));
        });
        sm.registerConsumer("status.reset", (sc, in) -> {
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> ModPacketHandler.clearStatus((ServerPlayerEntity) p));
        });
    }
}
*/
