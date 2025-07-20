package me.hammerle.mp.snuviscript.commands;

import org.bukkit.GameRule;
import org.bukkit.World;
import me.hammerle.mp.MundusPlugin;

public class GameRuleCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("gamerule.getall",
                (sc, in) -> GameRule.values());
        MundusPlugin.scriptManager.registerFunction("gamerule.isbool", (sc, in) -> {
            GameRule<?> rule = (GameRule<?>) in[0].get(sc);
            return rule.getType().equals(Boolean.class);
        });
        MundusPlugin.scriptManager.registerFunction("gamerule.getname", (sc, in) -> {
            GameRule<?> rule = (GameRule<?>) in[0].get(sc);
            return rule.getName();
        });
        MundusPlugin.scriptManager.registerFunction("gamerule.getkey", (sc, in) -> {
            return GameRule.getByName(in[0].getString(sc));
        });
        MundusPlugin.scriptManager.registerFunction("gamerule.getvalue", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<?> rule = (GameRule<?>) in[1].get(sc);
            Object o = w.getGameRuleValue(rule);
            if(o instanceof Number) {
                return ((Number) o).doubleValue();
            }
            return o;
        });
        MundusPlugin.scriptManager.registerConsumer("gamerule.setbool", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<Boolean> rule = (GameRule<Boolean>) in[1].get(sc);
            w.setGameRule(rule, in[2].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("gamerule.setint", (sc, in) -> {
            World w = (World) in[0].get(sc);
            GameRule<Integer> rule = (GameRule<Integer>) in[1].get(sc);
            w.setGameRule(rule, in[2].getInt(sc));
        });
    }
}
