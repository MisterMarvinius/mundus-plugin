package me.hammerle.kp.snuviscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.ICompletionProvider;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.network.protocol.game.PacketPlayOutCommands;
import net.minecraft.server.level.EntityPlayer;
import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

public class CommandManager {
    private final static UUID MARVINIUS = UUID.fromString("e41b5335-3c74-46e9-a6c5-dafc6334a477");
    private final static UUID KAJETANJOHANNES =
            UUID.fromString("51e240f9-ab10-4ea6-8a5d-779319f51257");
    private final static HashMap<String, KajetanCommand> COMMANDS = new HashMap<>();
    private final static HashSet<String> SNUVI_COMMANDS = new HashSet<>();
    private final static ArrayList<CommandNode<?>> CUSTOM_NODES = new ArrayList<>();
    private final static HashSet<String> IGNORED_COMMANDS = new HashSet<>();

    public static void clearCustomNodes() {
        CUSTOM_NODES.clear();
    }

    public static void addIgnored(String command) {
        IGNORED_COMMANDS.add(command);
    }

    public static void clearIgnored() {
        IGNORED_COMMANDS.clear();
    }

    public static void addCustomNode(CommandNode<?> node) {
        CUSTOM_NODES.add(node);
    }

    public static void add(KajetanCommand command) {
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

    public static void execute(CommandSender cs, String rawCommand) {
        String commandName = getCommandName(rawCommand);

        KajetanCommand command = COMMANDS.get(commandName);
        if(command != null) {
            if(cs.hasPermission(command.getName())) {
                command.execute(cs, getArguments(rawCommand));
                return;
            }
            ScriptEvents.onMissingPermission(cs, command.getName());
            return;
        }

        if(hasCustom(commandName)) {
            ScriptEvents.onCustomCommand(cs, commandName, getArguments(rawCommand));
            return;
        }

        if(!cs.hasPermission(commandName)) {
            ScriptEvents.onMissingPermission(cs, commandName);
            return;
        } else if(cs instanceof Player && ScriptEvents.onCommand((Player) cs, commandName)) {
            return;
        }
        Command bCommand = Bukkit.getServer().getCommandMap().getCommand(commandName);
        if(bCommand == null) {
            ScriptEvents.onMissingCommand(cs, commandName);
            return;
        }
        String perm = bCommand.getPermission();
        PermissionAttachment pa = cs.addAttachment(KajetansPlugin.instance, perm, true);
        try {
            bCommand.execute(cs, commandName, getArguments(rawCommand));
        } catch(Throwable ex) {
            KajetansPlugin.warn(ex.getMessage());
        }
        cs.removeAttachment(pa);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void send(Player player) {
        EntityPlayer p = NMS.map(player);

        Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map =
                Maps.newIdentityHashMap();
        RootCommandNode<CommandListenerWrapper> vanilla =
                p.c.vanillaCommandDispatcher.a().getRoot();
        RootCommandNode<ICompletionProvider> rootNode = new RootCommandNode<>();
        map.put(vanilla, rootNode);
        CommandListenerWrapper cs = p.cQ();
        commandSourceNodesToSuggestionNodes(true, vanilla, rootNode, cs, map);
        for(CommandNode node : CUSTOM_NODES) {
            commandSourceNodesToSuggestionNodes(node, rootNode, cs, map);
        }
        p.b.a(new PacketPlayOutCommands(rootNode));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void commandSourceNodesToSuggestionNodes(boolean first,
            CommandNode<CommandListenerWrapper> node, CommandNode<ICompletionProvider> suggestion,
            CommandListenerWrapper source,
            Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map) {
        for(CommandNode<CommandListenerWrapper> childNode : node.getChildren()) {
            if(first && IGNORED_COMMANDS.contains(childNode.getName())) {
                continue;
            }
            if((first && source.getBukkitSender().hasPermission(childNode.getName())
                    || (!first && childNode.canUse(source)))) {
                ArgumentBuilder<ICompletionProvider, ?> arg =
                        (ArgumentBuilder) childNode.createBuilder();
                arg.requires(a -> true);
                if(arg.getCommand() != null) {
                    arg.executes(a -> 0);
                }

                if(arg instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<ICompletionProvider, ?> required =
                            (RequiredArgumentBuilder) arg;
                    if(required.getSuggestionsProvider() != null) {
                        required.suggests(CompletionProviders.b(required.getSuggestionsProvider()));
                    }
                }

                if(arg.getRedirect() != null) {
                    arg.redirect(map.get(arg.getRedirect()));
                }

                CommandNode<ICompletionProvider> commandNode = arg.build();
                map.put(childNode, commandNode);
                suggestion.addChild(commandNode);
                if(!childNode.getChildren().isEmpty()) {
                    commandSourceNodesToSuggestionNodes(false, childNode, commandNode, source, map);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void commandSourceNodesToSuggestionNodes(
            CommandNode<CommandListenerWrapper> node, CommandNode<ICompletionProvider> parentNode,
            CommandListenerWrapper cs,
            Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map) {
        if(!node.canUse(cs)) {
            return;
        }
        ArgumentBuilder<ICompletionProvider, ?> arg = (ArgumentBuilder) node.createBuilder();
        arg.requires(a -> true);
        if(arg.getCommand() != null) {
            arg.executes(a -> 0);
        }
        if(arg instanceof RequiredArgumentBuilder) {
            RequiredArgumentBuilder<ICompletionProvider, ?> required =
                    (RequiredArgumentBuilder) arg;
            if(required.getSuggestionsProvider() != null) {
                required.suggests(CompletionProviders.b(required.getSuggestionsProvider()));
            }
        }
        if(arg.getRedirect() != null) {
            arg.redirect(map.get(arg.getRedirect()));
        }
        CommandNode<ICompletionProvider> commandNode = arg.build();
        map.put(node, commandNode);
        parentNode.addChild(commandNode);
        for(CommandNode<CommandListenerWrapper> childNode : node.getChildren()) {
            commandSourceNodesToSuggestionNodes(childNode, commandNode, cs, map);
        }
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
                p.addAttachment(KajetansPlugin.instance, info.getPermission(), false);
            }
        }
        if(p.getUniqueId().equals(MARVINIUS) || p.getUniqueId().equals(KAJETANJOHANNES)) {
            PermissionAttachment perm = p.addAttachment(KajetansPlugin.instance, "script", true);
            perm.setPermission("script.debug", true);
            perm.setPermission("script.error", true);
        }
        p.recalculatePermissions();
    }
}
