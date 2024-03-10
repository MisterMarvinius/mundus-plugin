package me.hammerle.kp.snuviscript.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.hammerle.kp.KajetansPlugin;

public class Command2Commands {
    public static void registerFunctions() {
        //command creation
        KajetansPlugin.scriptManager.registerFunction("command2.newhelp", (sc, in) -> {
            final String perm = in[1].getString(sc);
            return new CommandAPICommand(in[0].getString(sc)).withPermission(perm);
        });
        KajetansPlugin.scriptManager.registerConsumer("command2.addhelpargument", (sc, in) -> {
            CommandAPICommand cmd = (CommandAPICommand) in[0].get(sc);
            Argument<?> arg = (Argument<?>) in[1].get(sc);
            cmd.withArguments(arg);
        });
        KajetansPlugin.scriptManager.registerConsumer("command2.registerhelp", (sc, in) -> {
            CommandAPICommand cmd = (CommandAPICommand) in[0].get(sc);
            cmd.executes((sender, args) -> {
            });
            cmd.register();
        });
        KajetansPlugin.scriptManager.registerConsumer("command2.unregister", (sc, in) -> {
            String cmd = in[0].getString(sc);
            CommandAPI.unregister(cmd, true);
            CommandAPIBukkit.unregister(cmd, true, true);
        });

        //argument creation
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpstring", (sc, in) -> {
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
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpliteral", (sc, in) -> {
            String literal = in[0].getString(sc);
            Argument<?> arg = new StringArgument(literal)
                    .replaceSuggestions(ArgumentSuggestions.strings(literal));
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpbool", (sc, in) -> {
            String name = in[0].getString(sc);
            Argument<?> arg = new BooleanArgument(name);
            if(in.length >= 2) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpfloat", (sc, in) -> {
            float min = in[1].getFloat(sc);
            float max = in[2].getFloat(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new FloatArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpdouble", (sc, in) -> {
            double min = in[1].getDouble(sc);
            double max = in[2].getDouble(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new DoubleArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpint", (sc, in) -> {
            int min = in[1].getInt(sc);
            int max = in[2].getInt(sc);
            String name = in[0].getString(sc);
            Argument<?> arg = new IntegerArgument(name, min, max);
            if(in.length >= 4) {
                final String perm = in[1].getString(sc);
                arg.withPermission(perm);
            }
            return arg;
        });
        KajetansPlugin.scriptManager.registerFunction("command2.newhelpspecial", (sc, in) -> {
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
