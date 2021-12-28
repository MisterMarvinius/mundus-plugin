package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.snuviscript.CommandManager;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEnchantment;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMobEffect;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.item.ArgumentItemStack;

public class CommandCommands {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerConsumer("command.addignored",
                (sc, in) -> CommandManager.addIgnored(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("command.clearignored",
                (sc, in) -> CommandManager.clearIgnored());
        KajetansPlugin.scriptManager.registerConsumer("command.addnoperm",
                (sc, in) -> CommandManager.addNoPerm(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("command.clearnoperm",
                (sc, in) -> CommandManager.clearNoPerm());
        KajetansPlugin.scriptManager.registerFunction("command.newhelp", (sc, in) -> {
            final String perm = in[1].getString(sc);
            return CommandDispatcher.a(in[0].getString(sc))
                    .requires(p -> p.getBukkitSender().hasPermission(perm));
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpliteral", (sc, in) -> {
            LiteralArgumentBuilder<CommandListenerWrapper> arg =
                    CommandDispatcher.a(in[0].getString(sc));
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpbool", (sc, in) -> {
            RequiredArgumentBuilder<CommandListenerWrapper, Boolean> arg =
                    CommandDispatcher.a(in[0].getString(sc), BoolArgumentType.bool());
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpdouble", (sc, in) -> {
            double min = in[1].getDouble(sc);
            double max = in[2].getDouble(sc);
            RequiredArgumentBuilder<CommandListenerWrapper, Double> arg = CommandDispatcher
                    .a(in[0].getString(sc), DoubleArgumentType.doubleArg(min, max));
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpfloat", (sc, in) -> {
            float min = in[1].getFloat(sc);
            float max = in[2].getFloat(sc);
            RequiredArgumentBuilder<CommandListenerWrapper, Float> arg =
                    CommandDispatcher.a(in[0].getString(sc), FloatArgumentType.floatArg(min, max));
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpint", (sc, in) -> {
            int min = in[1].getInt(sc);
            int max = in[2].getInt(sc);
            RequiredArgumentBuilder<CommandListenerWrapper, Integer> arg =
                    CommandDispatcher.a(in[0].getString(sc), IntegerArgumentType.integer(min, max));
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelplong", (sc, in) -> {
            long min = in[1].getLong(sc);
            long max = in[2].getLong(sc);
            RequiredArgumentBuilder<CommandListenerWrapper, Long> arg =
                    CommandDispatcher.a(in[0].getString(sc), LongArgumentType.longArg(min, max));
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpstring", (sc, in) -> {
            RequiredArgumentBuilder<CommandListenerWrapper, String> arg;
            if(in[1].getBoolean(sc)) {
                arg = CommandDispatcher.a(in[0].getString(sc), StringArgumentType.greedyString());
            } else {
                arg = CommandDispatcher.a(in[0].getString(sc), StringArgumentType.string());
            }
            if(in.length >= 3) {
                final String perm = in[2].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command.newhelpspecial", (sc, in) -> {
            RequiredArgumentBuilder<CommandListenerWrapper, ArgumentType<?>> arg = null;
            String name = in[0].getString(sc);
            switch(name) {
                case "Item":
                    arg = CommandDispatcher.a(in[1].getString(sc),
                            (ArgumentType) ArgumentItemStack.a());
                    break;
                case "Block":
                    arg = CommandDispatcher.a(in[1].getString(sc), (ArgumentType) ArgumentTile.a());
                    break;
                case "Potion":
                    arg = CommandDispatcher.a(in[1].getString(sc),
                            (ArgumentType) ArgumentMobEffect.a());
                    break;
                case "Enchantment":
                    arg = CommandDispatcher.a(in[1].getString(sc),
                            (ArgumentType) ArgumentEnchantment.a());
                    break;
                case "Player":
                    arg = CommandDispatcher.a(in[1].getString(sc),
                            (ArgumentType) ArgumentEntity.c());
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("'%s' is not a valid special help", name));
            }
            if(in.length >= 3) {
                final String perm = in[2].getString(sc);
                arg.requires(p -> p.getBukkitSender().hasPermission(perm));
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerConsumer("command.sendhelp", (sc, in) -> {
            if(in.length > 0) {
                CommandManager.send((Player) in[0].get(sc));
                return;
            }
            Bukkit.getServer().getOnlinePlayers().forEach(p -> CommandManager.send(p));
        });
        KajetansPlugin.scriptManager.registerConsumer("command.addhelpalias", (sc, in) -> {
            ((ArgumentBuilder) in[0].get(sc)).redirect(((ArgumentBuilder) in[1].get(sc)).build());
        });
        KajetansPlugin.scriptManager.registerConsumer("command.addhelpchild", (sc, in) -> {
            ((ArgumentBuilder) in[0].get(sc)).then(((ArgumentBuilder) in[1].get(sc)).build());
        });
        KajetansPlugin.scriptManager.registerConsumer("command.addhelp", (sc, in) -> {
            CommandManager.addCustomNode(
                    ((LiteralArgumentBuilder<CommandListenerWrapper>) in[0].get(sc)).build());
        });
        KajetansPlugin.scriptManager.registerConsumer("command.clearhelp",
                (sc, in) -> CommandManager.clearCustomNodes());
        KajetansPlugin.scriptManager.registerConsumer("command.add",
                (sc, in) -> CommandManager.addCustom(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("command.remove",
                (sc, in) -> CommandManager.removeCustom(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("command.exists",
                (sc, in) -> CommandManager.hasCustom(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerConsumer("command.clear",
                (sc, in) -> CommandManager.clearCustom());
    }
}
