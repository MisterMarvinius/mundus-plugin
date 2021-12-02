/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.ReflectionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.RuleKey;
import net.minecraft.world.World;

public class GameRuleCommands {
    private static GameRules.RuleKey<?> ruleKey = null;

    public static void registerFunctions(ScriptManager sm, MinecraftServer server) {
        sm.registerFunction("gamerule.getkey", (sc, in) -> {
            String name = in[0].getString(sc);
            ruleKey = null;
            GameRules.visitAll(new GameRules.IRuleEntryVisitor() {
                @Override
                public <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> key,
                        GameRules.RuleType<T> type) {
                    if(key.getName().equals(name)) {
                        ruleKey = key;
                    }
                }
            });
            return ruleKey;
        });
        sm.registerFunction("gamerule.getvalue", (sc, in) -> {
            return ((World) in[0].get(sc)).getGameRules().get((RuleKey<?>) in[1].get(sc));
        });
        sm.registerFunction("gamerule.isbool", (sc, in) -> {
            return in[0].get(sc) instanceof GameRules.BooleanValue;
        });
        sm.registerFunction("gamerule.isint", (sc, in) -> {
            return in[0].get(sc) instanceof GameRules.IntegerValue;
        });
        sm.registerFunction("gamerule.getbool", (sc, in) -> {
            return ((GameRules.BooleanValue) in[0].get(sc)).get();
        });
        sm.registerFunction("gamerule.getint", (sc, in) -> {
            return (double) ((GameRules.IntegerValue) in[0].get(sc)).get();
        });
        sm.registerConsumer("gamerule.setbool", (sc, in) -> {
            ((GameRules.BooleanValue) in[0].get(sc)).set(in[1].getBoolean(sc), server);
        });
        sm.registerConsumer("gamerule.setint", (sc, in) -> {
            ReflectionUtils.setIntegerValue((GameRules.IntegerValue) in[0].get(sc),
                    in[1].getInt(sc));
        });
    }
}
*/
