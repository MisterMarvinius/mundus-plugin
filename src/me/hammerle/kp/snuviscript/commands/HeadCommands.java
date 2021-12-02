/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.networking.ModPacketHandler;
import me.km.permissions.Permissions;
import me.km.snuviscript.Scripts;
import static me.km.snuviscript.commands.CommandUtils.doForGroup;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

public class HeadCommands {
    public static void registerFunctions(ScriptManager sm, Scripts scripts, Permissions perms,
            MinecraftServer server) {
        sm.registerConsumer("head.add", (sc, in) -> {
            byte id = in[1].getByte(sc);
            String name = in[2].getString(sc);
            float x = in[3].getFloat(sc);
            float y = in[4].getFloat(sc);
            float width = in[5].getFloat(sc);
            float height = in[6].getFloat(sc);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> {
                ModPacketHandler.sendToHead((ServerPlayerEntity) p, (byte) 1, id, name, x, y, width,
                        height);
            });
        });
        sm.registerConsumer("head.remove", (sc, in) -> {
            byte id = in[1].getByte(sc);
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> {
                ModPacketHandler.sendToHead((ServerPlayerEntity) p, (byte) 2, id, "", 0.0f, 0.0f,
                        0.0f, 0.0f);
            });
        });
        sm.registerConsumer("head.reset", (sc, in) -> {
            doForGroup(server, scripts, perms, in[0].get(sc), sc, p -> {
                ModPacketHandler.sendToHead((ServerPlayerEntity) p, (byte) 3, (byte) -1, "", 0.0f,
                        0.0f, 0.0f, 0.0f);
            });
        });
    }
}
*/
