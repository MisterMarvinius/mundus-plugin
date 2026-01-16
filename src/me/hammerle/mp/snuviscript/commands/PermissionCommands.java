package me.hammerle.mp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.snuviscript.CommandManager;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerConsumer("perm.clear", (sc, in) -> {
            CommandManager.clearPermissions((Player) in[0].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("perm.add", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
                PermissionAttachment perm = info.getAttachment();
                if(perm != null && perm.getPlugin() == MundusPlugin.instance) {
                    perm.setPermission(in[0].getString(sc), true);
                    return;
                }
            }
            p.addAttachment(MundusPlugin.instance, in[0].getString(sc), true);
        });
        MundusPlugin.scriptManager.registerConsumer("perm.remove", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            p.addAttachment(MundusPlugin.instance, in[0].getString(sc), false);
        });
        MundusPlugin.scriptManager.registerConsumer("perm.update", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.recalculatePermissions();
        });
        //MundusPlugin.scriptManager.registerFunction("perm.has",
        //        (sc, in) -> ((Permissible) in[1].get(sc)).hasPermission(in[0].getString(sc)));
    }
}
