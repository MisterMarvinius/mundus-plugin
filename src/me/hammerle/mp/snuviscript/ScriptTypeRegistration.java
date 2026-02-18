package me.hammerle.mp.snuviscript;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.snuviscript.inputprovider.ScriptTypeRegistry;

public final class ScriptTypeRegistration {
    private ScriptTypeRegistration() {}

    public static void registerMinecraftTypes() {
        registerType("entity", Entity.class::isInstance);
        registerType("living", LivingEntity.class::isInstance, "entity");
        registerType("player", Player.class::isInstance, "living", "entity");
    }

    private static void registerType(String name, Predicate<Object> check, String... parents) {
        try {
            Method method = ScriptTypeRegistry.class.getMethod("registerType", String.class,
                    Predicate.class, String[].class);
            method.invoke(null, name, check, parents);
            return;
        } catch(NoSuchMethodException ex) {
            // fallback below for older core API
        } catch(Exception ex) {
            throw new IllegalStateException("unable to register script type '" + name + "'", ex);
        }

        ScriptTypeRegistry.registerType(name, check);
        if(parents.length > 0) {
            MundusPlugin.warn("ScriptTypeRegistry has no parent-type API; registered '" + name
                    + "' without hierarchy.");
        }
    }
}
