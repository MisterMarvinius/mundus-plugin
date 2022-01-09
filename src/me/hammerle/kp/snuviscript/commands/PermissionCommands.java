package me.hammerle.kp.snuviscript.commands;

import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.snuviscript.CommandManager;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("perm.clear", (sc, in) -> {
            CommandManager.clearPermissions((Player) in[0].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("perm.add", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
                PermissionAttachment perm = info.getAttachment();
                if(perm != null && perm.getPlugin() == KajetansPlugin.instance) {
                    perm.setPermission(in[0].getString(sc), true);
                    return;
                }
            }
            p.addAttachment(KajetansPlugin.instance, in[0].getString(sc), true);
        });
        KajetansPlugin.scriptManager.registerConsumer("perm.remove", (sc, in) -> {
            Player p = (Player) in[1].get(sc);
            p.addAttachment(KajetansPlugin.instance, in[0].getString(sc), false);
        });
        KajetansPlugin.scriptManager.registerConsumer("perm.update", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.recalculatePermissions();
        });
        KajetansPlugin.scriptManager.registerFunction("perm.has",
                (sc, in) -> ((Permissible) in[1].get(sc)).hasPermission(in[0].getString(sc)));
    }
}
