/*package me.km.permissions;

import com.google.common.collect.Maps;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import me.hammerle.snuviscript.code.ISnuviScheduler;
import me.km.events.CommandEvent;
import me.km.snuviscript.ScriptEvents;
import me.km.snuviscript.Scripts;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SCommandListPacket;

public class ModCommandManager extends Commands {
    private final HashSet<String> otherCommands = new HashSet<>();

    private final HashMap<String, Command> commands = new HashMap<>();
    private final Permissions perms;
    private final ScriptEvents events;
    private final Scripts scripts;

    private final ArrayList<CommandNode<?>> customNodes = new ArrayList<>();
    private final HashSet<String> ignoredCommands = new HashSet<>();

    public ModCommandManager(Permissions perms, ScriptEvents events, Scripts scripts,
            ISnuviScheduler scheduler) {
        super(EnvironmentType.DEDICATED);
        getDispatcher().getRoot().getChildren().forEach(c -> {
            otherCommands.add(c.getName());
        });
        scheduler.scheduleTask("ModCommandManager", () -> {
            getDispatcher().getRoot().getChildren().forEach(c -> {
                if(otherCommands.add(c.getName())) {
                    perms.addOtherGroupPermission(c.getName());
                }
            });
        });
        this.perms = perms;
        this.events = events;
        this.scripts = scripts;

        // forge command which fails ...
        ignoredCommands.add("config");
    }

    public void clearCustomNodes() {
        customNodes.clear();
    }

    public void addIgnoredCommands(String command) {
        ignoredCommands.add(command);
    }

    public void clearIgnoredCommands() {
        ignoredCommands.clear();
        // forge command which fails ...
        ignoredCommands.add("config");
    }

    public void addCustomNode(CommandNode<?> node) {
        customNodes.add(node);
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
        for(String alias : command.getAliases()) {
            commands.put(alias, command);
        }
    }

    private String getCommandName(String rawCommand) {
        if(rawCommand.isEmpty()) {
            return "";
        }
        int index = rawCommand.indexOf(' ');
        return rawCommand.substring(rawCommand.charAt(0) == '/' ? 1 : 0,
                index == -1 ? rawCommand.length() : index);
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

    private ICommandSource getSource(CommandSource cs) {
        if(cs.getEntity() != null) {
            return cs.getEntity();
        }
        return cs.getServer();
    }

    private String lowerRawCommand(String raw) {
        int index = raw.indexOf(" ");
        if(index == -1) {
            return raw.toLowerCase();
        }
        return raw.substring(0, index).toLowerCase() + raw.substring(index);
    }

    @Override
    public int handleCommand(CommandSource cs, String rawCommand) {
        rawCommand = lowerRawCommand(rawCommand);
        String commandName = getCommandName(rawCommand);

        Command command = commands.get(commandName);
        if(command != null) {
            if(perms.has(cs, command.getName())) {
                command.execute(getSource(cs), getArguments(rawCommand));
                return 1;
            }
            events.onMissingPermission(getSource(cs), command.getName());
            return 0;
        }

        if(scripts.isRegisteredScriptCommand(commandName)) {
            Entity ent = cs.getEntity();
            if(ent != null && ent instanceof PlayerEntity) {
                events.onCustomCommand((PlayerEntity) ent, commandName, getArguments(rawCommand));
                return 1;
            }
            events.onCustomCommand(null, commandName, getArguments(rawCommand));
            return 0;
        }

        if(otherCommands.contains(commandName)) {
            if(perms.has(cs, commandName)) {
                Entity ent = cs.getEntity();
                if(ent != null && ent instanceof PlayerEntity) {
                    CommandEvent e = new CommandEvent((PlayerEntity) ent, commandName);
                    events.onCommand(e);
                    if(e.isCanceled()) {
                        return 0;
                    }
                }
                return super.handleCommand(cs, rawCommand);
            }
            events.onMissingPermission(getSource(cs), commandName);
            return 0;
        }

        events.onMissingCommand(getSource(cs), commandName);
        return 0;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void send(ServerPlayerEntity player) {
        Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map = Maps.newHashMap();
        RootCommandNode<ISuggestionProvider> rootNode = new RootCommandNode<>();
        map.put(getDispatcher().getRoot(), rootNode);
        CommandSource cs = player.getCommandSource();
        commandSourceNodesToSuggestionNodes(true, getDispatcher().getRoot(), rootNode, cs, map);
        for(CommandNode node : customNodes) {
            commandSourceNodesToSuggestionNodes(node, rootNode, cs, map);
        }
        player.connection.sendPacket(new SCommandListPacket(rootNode));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void commandSourceNodesToSuggestionNodes(boolean first, CommandNode<CommandSource> node,
            CommandNode<ISuggestionProvider> suggestion, CommandSource source,
            Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map) {
        for(CommandNode<CommandSource> childNode : node.getChildren()) {
            if(first && ignoredCommands.contains(childNode.getName())) {
                continue;
            }
            if((first && perms.has(source, childNode.getName())
                    || (!first && childNode.canUse(source)))) {
                ArgumentBuilder<ISuggestionProvider, ?> arg =
                        (ArgumentBuilder) childNode.createBuilder();
                arg.requires(a -> true);
                if(arg.getCommand() != null) {
                    arg.executes(a -> 0);
                }

                if(arg instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<ISuggestionProvider, ?> required =
                            (RequiredArgumentBuilder) arg;
                    if(required.getSuggestionsProvider() != null) {
                        required.suggests(
                                SuggestionProviders.ensureKnown(required.getSuggestionsProvider()));
                    }
                }

                if(arg.getRedirect() != null) {
                    arg.redirect(map.get(arg.getRedirect()));
                }

                CommandNode<ISuggestionProvider> commandNode = arg.build();
                map.put(childNode, commandNode);
                suggestion.addChild(commandNode);
                if(!childNode.getChildren().isEmpty()) {
                    this.commandSourceNodesToSuggestionNodes(false, childNode, commandNode, source,
                            map);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void commandSourceNodesToSuggestionNodes(CommandNode<CommandSource> node,
            CommandNode<ISuggestionProvider> parentNode, CommandSource cs,
            Map<CommandNode<CommandSource>, CommandNode<ISuggestionProvider>> map) {
        if(!node.canUse(cs)) {
            return;
        }
        ArgumentBuilder<ISuggestionProvider, ?> arg = (ArgumentBuilder) node.createBuilder();
        arg.requires(a -> true);
        if(arg.getCommand() != null) {
            arg.executes(a -> 0);
        }
        if(arg instanceof RequiredArgumentBuilder) {
            RequiredArgumentBuilder<ISuggestionProvider, ?> required =
                    (RequiredArgumentBuilder) arg;
            if(required.getSuggestionsProvider() != null) {
                required.suggests(
                        SuggestionProviders.ensureKnown(required.getSuggestionsProvider()));
            }
        }
        if(arg.getRedirect() != null) {
            arg.redirect(map.get(arg.getRedirect()));
        }
        CommandNode<ISuggestionProvider> commandNode = arg.build();
        map.put(node, commandNode);
        parentNode.addChild(commandNode);
        for(CommandNode<CommandSource> childNode : node.getChildren()) {
            commandSourceNodesToSuggestionNodes(childNode, commandNode, cs, map);
        }
    }
}*/
