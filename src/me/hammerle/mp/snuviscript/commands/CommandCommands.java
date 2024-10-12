package me.hammerle.mp.snuviscript.commands;

import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.snuviscript.CommandManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.ParticleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.PotionEffectArgument;
import dev.jorel.commandapi.arguments.SoundArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.minecraft.commands.CommandDispatcher;
import net.minecraft.data.registries.VanillaRegistries;

public class CommandCommands {
    public static void registerFunctions() {
        CommandDispatcher.a(VanillaRegistries.a());

        MundusPlugin.scriptManager.registerConsumer("command.add",
                (sc, in) -> CommandManager.addCustom(in[0].getString(sc)));
        MundusPlugin.scriptManager.registerConsumer("command.remove",
                (sc, in) -> CommandManager.removeCustom(in[0].getString(sc)));
        MundusPlugin.scriptManager.registerFunction("command.exists",
                (sc, in) -> CommandManager.hasCustom(in[0].getString(sc)));
        MundusPlugin.scriptManager.registerConsumer("command.clear",
                (sc, in) -> CommandManager.clearCustom());
        //commandhelp creation
        MundusPlugin.scriptManager.registerFunction("command.newhelp", (sc, in) -> {
            final String perm = in[1].getString(sc);
            return new CommandAPICommand(in[0].getString(sc)).withPermission(perm);
        });
        MundusPlugin.scriptManager.registerConsumer("command.addhelpargument", (sc, in) -> {
            CommandAPICommand cmd = (CommandAPICommand) in[0].get(sc);
            Argument<?> arg = (Argument<?>) in[1].get(sc);
            cmd.withArguments(arg);
        });
        MundusPlugin.scriptManager.registerConsumer("command.registerhelp", (sc, in) -> {
            CommandAPICommand cmd = (CommandAPICommand) in[0].get(sc);
            cmd.executes((sender, args) -> {
            });
            cmd.register();
        });
        MundusPlugin.scriptManager.registerConsumer("command.unregister", (sc, in) -> {
            String cmd = in[0].getString(sc);
            CommandAPI.unregister(cmd, true);
            CommandAPIBukkit.unregister(cmd, true, true);
        });
        //commandhelp argument creation
        MundusPlugin.scriptManager.registerFunction("command.newhelpstring", (sc, in) -> {
            Argument<?> arg = null;
            String name = in[0].getString(sc);
            if(in[1].getBoolean(sc)) {
                arg = new GreedyStringArgument(name);
            } else {
                arg = new StringArgument(name);
            }
            if(in.length >= 3) {
                final String perm = in[2].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpliteral", (sc, in) -> {
            String literal = in[0].getString(sc);
            Argument<?> arg = new LiteralArgument(literal)
                    .replaceSuggestions(ArgumentSuggestions.strings(literal));
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpbool", (sc, in) -> {
            String name = in[0].getString(sc);
            Argument<?> arg = new BooleanArgument(name);
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpfloat", (sc, in) -> {
            float min = in[1].getFloat(sc);
            float max = in[2].getFloat(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new FloatArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpdouble", (sc, in) -> {
            double min = in[1].getDouble(sc);
            double max = in[2].getDouble(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new DoubleArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpint", (sc, in) -> {
            int min = in[1].getInt(sc);
            int max = in[2].getInt(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new IntegerArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[3].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        MundusPlugin.scriptManager.registerFunction("command.newhelpspecial", (sc, in) -> {
            Argument<?> arg = null;
            String type = in[0].getString(sc);
            String name = in[1].getString(sc);
            switch(type) {
                case "Item":
                    arg = new ItemStackArgument(name);
                    break;
                case "Player":
                    arg = new PlayerArgument(name);
                    break;
                case "Enchantment":
                    arg = new EnchantmentArgument(name);
                    break;
                case "Potion":
                    arg = new PotionEffectArgument(name);
                    break;
                case "Particle":
                    arg = new ParticleArgument(name);
                    break;
                case "Sound":
                    arg = new SoundArgument(name);
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("'%s' is not a valid special help", type));
            }
            if(in.length >= 3) {
                final String perm = in[2].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
    }
}
