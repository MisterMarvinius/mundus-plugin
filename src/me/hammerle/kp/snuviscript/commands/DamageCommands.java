package me.hammerle.kp.snuviscript.commands;

import java.lang.reflect.Field;
import java.util.HashMap;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import me.hammerle.kp.KajetansPlugin;

public class DamageCommands {
    private final static HashMap<String, DamageType> DAMAGE_TYPES = new HashMap<>();

    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("damage.get", (sc, in) -> {
            DamageType dt = parseDamageType(in[0].getString(sc));
            return DamageSource.builder(dt).build();
        });
        KajetansPlugin.scriptManager.registerFunction("damage.gettype", (sc, in) -> {
            DamageSource d = (DamageSource) in[0].get(sc);
            return d.getDamageType().getTranslationKey();
        });
        KajetansPlugin.scriptManager.registerFunction("damage.getimmediatesource", (sc, in) -> {
            DamageSource d = (DamageSource) in[0].get(sc);
            return d.getDirectEntity();
        });
        KajetansPlugin.scriptManager.registerFunction("damage.gettruesource", (sc, in) -> {
            DamageSource d = (DamageSource) in[0].get(sc);
            return d.getCausingEntity();
        });
        KajetansPlugin.scriptManager.registerFunction("damage.isindirect", (sc, in) -> {
            DamageSource d = (DamageSource) in[0].get(sc);
            return d.isIndirect();
        });
        KajetansPlugin.scriptManager.registerFunction("damage.isdifficultyscaled", (sc, in) -> {
            DamageSource d = (DamageSource) in[0].get(sc);
            return d.scalesWithDifficulty();
        });
    }

    public static DamageType parseDamageType(String name) {
        if(DAMAGE_TYPES.isEmpty()) {
            for(Field f : DamageType.class.getFields()) {
                if(f.getType() == DamageType.class) {
                    try {
                        DamageType dt = (DamageType) f.get(null);
                        DAMAGE_TYPES.put(dt.getTranslationKey(), dt);
                    } catch(Exception ex) {
                    }
                }
            }
        }
        return DAMAGE_TYPES.get(name);
    }
}
