package me.hammerle.mp.snuviscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import me.hammerle.mp.MundusPlugin;
import com.mojang.brigadier.tree.CommandNode;

public class CommandManager {
    private final static UUID MARVINIUS = UUID.fromString("e41b5335-3c74-46e9-a6c5-dafc6334a477");
    private final static UUID SIRTERENCE7 = UUID.fromString("6cc9f8c7-9dfd-44f4-a3f2-af30054411a8");
    private final static HashMap<String, MundusCommand> COMMANDS = new HashMap<>();
    private final static HashSet<String> SNUVI_COMMANDS = new HashSet<>();
    private final static HashMap<String, CommandNode<?>> CUSTOM_NODES = new HashMap<>();

    public static void clearCustomNodes() {
        CUSTOM_NODES.clear();
    }

    public static void addCustomNode(CommandNode<?> node) {
        CUSTOM_NODES.put(node.getName(), node);
    }

    public static void add(MundusCommand command) {
        COMMANDS.put(command.getName(), command);
        for(String alias : command.getAliases()) {
            COMMANDS.put(alias, command);
        }
    }

    private static String getCommandName(String rawCommand) {
        if(rawCommand.isEmpty()) {
            return "";
        }
        int index = rawCommand.indexOf(' ');
        return rawCommand.substring(rawCommand.charAt(0) == '/' ? 1 : 0,
                index == -1 ? rawCommand.length() : index).toLowerCase();
    }

    private static String[] getArguments(String rawCommand) {
        int old = rawCommand.indexOf(' ') + 1;
        if(old == 0) {
            return new String[0];
        }
        int pos = old;
        ArrayList<String> list = new ArrayList<>();
        while(pos < rawCommand.length()) {
            char c = rawCommand.charAt(pos);
            switch(c) {
                case ' ':
                    if(pos - old > 0) {
                        list.add(rawCommand.substring(old, pos));
                    }
                    old = pos + 1;
                    break;
                case '"':
                    if(pos - old > 0) {
                        list.add(rawCommand.substring(old, pos));
                    }
                    old = pos + 1;
                    pos = old;
                    while(pos < rawCommand.length() && rawCommand.charAt(pos) != '"') {
                        pos++;
                    }
                    list.add(rawCommand.substring(old, pos));
                    old = pos + 1;
                    break;
            }
            pos++;
        }
        if(pos - old > 0) {
            list.add(rawCommand.substring(old, pos));
        }
        return list.toArray(new String[list.size()]);
    }

    private static boolean checkPerm(CommandSender cs, String perm) {
        if(cs.hasPermission(perm)) {
            return true;
        }
        int point = perm.indexOf(".");
        if(point != -1) {
            String all = perm.substring(0, point) + ".*";
            return cs.hasPermission(all);
        }
        return false;
    }

    public static boolean execute(CommandSender cs, String rawCommand) {
        String commandName = getCommandName(rawCommand);

        MundusCommand command = COMMANDS.get(commandName);
        if(command != null) {
            if(cs.hasPermission(command.getName())) {
                command.execute(cs, getArguments(rawCommand));
                return true;
            }
            ScriptEvents.onMissingPermission(cs, command.getName(), command.getName());
            return true;
        }

        if(hasCustom(commandName)) {
            ScriptEvents.onCustomCommand(cs, commandName, getArguments(rawCommand));
            return true;
        }

        Command bCommand = Bukkit.getServer().getCommandMap().getCommand(commandName);
        if(bCommand == null) {
            ScriptEvents.onMissingCommand(cs, commandName);
            return true;
        }

        String perm = bCommand.getPermission();
        if(perm == null || perm.isEmpty()) {
            perm = "missing." + bCommand.getName();
        }

        if(!checkPerm(cs, perm)) {
            ScriptEvents.onMissingPermission(cs, commandName, perm);
            return true;
        }
        if(cs instanceof Player && ScriptEvents.onCommand((Player) cs, commandName, bCommand)) {
            return true;
        }
        return false;
    }

    public static void addCustom(String command) {
        SNUVI_COMMANDS.add(command);
    }

    public static void removeCustom(String command) {
        SNUVI_COMMANDS.remove(command);
    }

    public static boolean hasCustom(String command) {
        return SNUVI_COMMANDS.contains(command);
    }

    public static void clearCustom() {
        SNUVI_COMMANDS.clear();
    }

    public static void clearPermissions(Player p) {
        for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
            if(info.getAttachment() != null) {
                info.getAttachment().remove();
            }
        }
        for(PermissionAttachmentInfo info : p.getEffectivePermissions()) {
            if(info.getAttachment() == null) {
                p.addAttachment(MundusPlugin.instance, info.getPermission(), false);
            }
        }
        if(p.getUniqueId().equals(MARVINIUS) || p.getUniqueId().equals(SIRTERENCE7)) {
            PermissionAttachment perm = p.addAttachment(MundusPlugin.instance, "script", true);
            perm.setPermission("script.debug", true);
            perm.setPermission("script.error", true);
        }
        p.recalculatePermissions();
    }
}
