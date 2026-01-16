package me.hammerle.mp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.snuviscript.CommandManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.permissions.Permissible;

public class PermissionCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("perm.clear", (sc, in) -> {
            CommandManager.clearPermissions((Player) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("perm.add", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            LuckPerms luckPerms = MundusPlugin.getLuckPerms();
            if(luckPerms == null) {
                return;
            }
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(p);
            String permission = in[0].getString(sc);
            user.transientData().remove(PermissionNode.builder(permission).value(false).build());
            user.transientData().add(PermissionNode.builder(permission).value(true).build());
            p.recalculatePermissions();
        });
        MundusPlugin.scriptManager.registerConsumer("perm.remove", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            LuckPerms luckPerms = MundusPlugin.getLuckPerms();
            if(luckPerms == null) {
                return;
            }
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(p);
            String permission = in[0].getString(sc);
            user.transientData().remove(PermissionNode.builder(permission).value(true).build());
            user.transientData().add(PermissionNode.builder(permission).value(false).build());
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
