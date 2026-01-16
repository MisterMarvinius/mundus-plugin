package me.hammerle.mp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.LuckPermsBridge;
import me.hammerle.mp.snuviscript.CommandManager;
import org.bukkit.permissions.Permissible;

public class PermissionCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("perm.clear", (sc, in) -> {
            CommandManager.clearPermissions((Player) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("perm.add", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            String permission = in[0].getString(sc);
            LuckPermsBridge.removeTransientPermission(p, permission, false);
            LuckPermsBridge.addTransientPermission(p, permission, true);
            p.recalculatePermissions();
        });
        MundusPlugin.scriptManager.registerConsumer("perm.remove", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            String permission = in[0].getString(sc);
            LuckPermsBridge.removeTransientPermission(p, permission, true);
            LuckPermsBridge.addTransientPermission(p, permission, false);
            p.recalculatePermissions();
        });
        MundusPlugin.scriptManager.registerConsumer("perm.update", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.recalculatePermissions();
        });
        MundusPlugin.scriptManager.registerFunction("perm.has",
                (sc, in) -> ((Permissible) in[1].get(sc)).hasPermission(in[0].getString(sc)));
    }
}
