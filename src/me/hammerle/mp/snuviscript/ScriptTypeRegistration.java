package me.hammerle.mp.snuviscript;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.utils.Table;
import me.hammerle.snuviscript.inputprovider.ScriptTypeRegistry;
import net.kyori.adventure.text.Component;

public final class ScriptTypeRegistration {
    private ScriptTypeRegistration() {}

    public static void registerMinecraftTypes() {
        registerType("entity", Entity.class::isInstance);
        registerType("living", LivingEntity.class::isInstance, "entity");
        registerType("player", Player.class::isInstance, "living", "entity");
        registerType("itemstack", ItemStack.class::isInstance);
        registerType("itementity", Item.class::isInstance, "entity");
        registerType("block", Block.class::isInstance);
        registerType("material", Material.class::isInstance);
        registerType("location", Location.class::isInstance);
        registerType("slot", EquipmentSlot.class::isInstance);
        registerType("damagesource", DamageSource.class::isInstance);
        registerType("display", Display.class::isInstance);
        registerType("textdisplay", TextDisplay.class::isInstance, "display");
        registerType("blockdisplay", BlockDisplay.class::isInstance, "display");
        registerType("itemdisplay", ItemDisplay.class::isInstance, "display");
        registerType("enchantment", Enchantment.class::isInstance);
        registerType("text", Component.class::isInstance);
        registerType("table", Table.class::isInstance);
        registerType("inventory", Inventory.class::isInstance);
        registerType("world", World.class::isInstance);
        registerType("particle", Particle.class::isInstance);
        registerType("sound", Sound.class::isInstance);
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
