package me.hammerle.kp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionCommands {
    private final static UUID MARVINIUS = UUID.fromString("e41b5335-3c74-46e9-a6c5-dafc6334a477");
    private final static UUID KAJETANJOHANNES =
            UUID.fromString("51e240f9-ab10-4ea6-8a5d-779319f51257");

    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("perm.clear", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
                if(info.getAttachment() != null) {
                    info.getAttachment().remove();
                }
            }
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
                if(info.getAttachment() == null) {
                    p.addAttachment(KajetansPlugin.instance, info.getPermission(), false);
                }
            }
            if(p.getUniqueId().equals(MARVINIUS) || p.getUniqueId().equals(KAJETANJOHANNES)) {
                p.addAttachment(KajetansPlugin.instance, "script", true);
            }
        });
        KajetansPlugin.scriptManager.registerConsumer("perm.add", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
                PermissionAttachment perm = info.getAttachment();
                if(perm != null && perm.getPlugin() == KajetansPlugin.instance) {
                    perm.setPermission(in[1].getString(sc), true);
                    return;
                }
            }
            p.addAttachment(KajetansPlugin.instance, in[1].getString(sc), true);
        });
        KajetansPlugin.scriptManager.registerConsumer("perm.refresh", (sc, in) -> {
            Player p = (Player) in[0].get(sc);
            p.recalculatePermissions();
        });
        KajetansPlugin.scriptManager.registerFunction("perm.has",
                (sc, in) -> ((Permissible) in[0].get(sc)).hasPermission(in[1].getString(sc)));
    }
}
