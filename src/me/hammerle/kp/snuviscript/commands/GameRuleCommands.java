package me.hammerle.kp.snuviscript.commands;

import org.bukkit.GameRule;
import org.bukkit.World;
import me.hammerle.kp.KajetansPlugin;

public class GameRuleCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("gamerule.getkey", (sc, in) -> {
            return GameRule.getByName(in[0].getString(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("gamerule.getvalue", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<?> rule = (GameRule<?>) in[1].get(sc);
            Object o = w.getGameRuleValue(rule);
            if(o instanceof Number) {
                return ((Number) o).doubleValue();
            }
            return o;
        });
        KajetansPlugin.scriptManager.registerConsumer("gamerule.setbool", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<Boolean> rule = (GameRule<Boolean>) in[1].get(sc);
            w.setGameRule(rule, in[2].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("gamerule.setint", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<Integer> rule = (GameRule<Integer>) in[1].get(sc);
            w.setGameRule(rule, in[2].getInt(sc));
        });
    }
}
