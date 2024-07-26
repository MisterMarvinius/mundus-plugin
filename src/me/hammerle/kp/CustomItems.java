package me.hammerle.kp;

import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CustomItems {
    private static final HashMap<Integer, CustomItem> ID_TO_ITEM = new HashMap<>();

    public enum CustomItem {
        GOLD_COIN(2, "Gold Coin"),
        SILVER_COIN(4, "Silver Coin"),
        COPPER_COIN(6, "Copper Coin"),
        ARROW_DOWN(10, "Down Arrow"),
        ARROW_LEFT(12, "Left Arrow"),
        ARROW_RIGHT(14, "Right Arrow"),
        ARROW_UP(16, "Up Arrow"),
        BAT_WING(18, "Bat Wing"),
        BEAR_POLAR_FUR(20, "Polar Bear Fur"),
        CHECK_GREEN(22, "Green Check"),
        CHECK_RED(24, "Red Check"),
        CLOTH(26, "Cloth"),
        COARSE_CLOTH(28, "Coarse Cloth"),
        DIGIT_0(30, "Digit 0"),
        DIGIT_1(32, "Digit 1"),
        DIGIT_2(34, "Digit 2"),
        DIGIT_3(36, "Digit 3"),
        DIGIT_4(38, "Digit 4"),
        DIGIT_5(40, "Digit 5"),
        DIGIT_6(42, "Digit 6"),
        DIGIT_7(44, "Digit 7"),
        DIGIT_8(46, "Digit 8"),
        DIGIT_9(48, "Digit 9"),
        PLUS_GREEN(50, "Green Plus"),
        PLUS_RED(52, "Red Plus"),
        REFRESH_GREEN(54, "Green Refresh"),
        REFRESH_RED(56, "Red Refresh"),
        RETURN_GREEN(58, "Green Return"),
        RETURN_RED(60, "Red Return"),
        RUBY(62, "Ruby"),
        AMBER(64, "Amber"),
        SAPPHIRE(66, "Sapphire"),
        COPPER_KEY(68, "Copper Key"),
        IRON_KEY(70, "Iron Key"),
        GOLDEN_KEY(72, "Golden Key"),
        CROSS_GREEN(74, "Green Cross"),
        CROSS_RED(76, "Red Cross"),
        GEAR(80, "Gear"),
        MINUS_GREEN(82, "Green Minus"),
        MINUS_RED(84, "Red Minus"),
        WOLF_FUR(94, "Wolf Fur"),
        SCROLL(96, "Scroll");

        private final int id;
        private final String name;

        private CustomItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    static {
        for(CustomItem item : CustomItem.values()) {
            ID_TO_ITEM.put(item.getId(), item);
        }
    }

    public static void onPlayerItemDamage(PlayerItemDamageEvent e) {
        ItemStack stack = e.getItem();
        if(stack.getType() != Material.NETHERITE_HOE) {
            return;
        }
        Damageable meta = (Damageable) stack.getItemMeta();
        int addDamage = e.getDamage();
        int newDamage = meta.getDamage() + addDamage;
        if(meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
            newDamage++;
            addDamage++;
        }
        if((newDamage & 1) == 0) {
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            addDamage--;
        } else {
            meta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
        }
        stack.setItemMeta(meta);
        e.setDamage(addDamage);
    }

    public static void onPrepareAnvil(PrepareAnvilEvent e) {
        ItemStack stack = e.getResult();
        if(stack == null || stack.getType() != Material.NETHERITE_HOE) {
            return;
        }
        if(getCustomItem(e.getInventory().getFirstItem()) != null
                || getCustomItem(e.getInventory().getSecondItem()) != null) {
            e.setResult(null);
            return;
        }
        if(getCustomItem(stack) != null) {
            return;
        }
        correctDamage(stack);
        e.setResult(stack);
    }

    public static void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
        ItemStack stack = e.getItem();
        if(stack == null || stack.getType() != Material.NETHERITE_HOE) {
            return;
        }
        if(getCustomItem(stack) != null) {
            e.setCancelled(true);
        }
    }

    public static CustomItem getCustomItem(ItemStack stack) {
        if(stack == null) {
            return null;
        } else if(stack.getType() != Material.NETHERITE_HOE) {
            return null;
        }
        Damageable d = (Damageable) stack.getItemMeta();
        return ID_TO_ITEM.get(d.getDamage());
    }

    public static void onPlayerInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if(b == null || !Tag.DIRT.isTagged(b.getType())) {
            return;
        }
        ItemStack stack = e.getPlayer().getInventory().getItem(e.getHand());
        CustomItem item = getCustomItem(stack);
        if(item != null) {
            e.setCancelled(true);
        }
    }

    public static ItemStack build(CustomItem item, int amount) {
        ItemStack stack = new ItemStack(Material.NETHERITE_HOE, amount);
        Damageable d = (Damageable) stack.getItemMeta();
        d.setDamage(item.getId());
        d.displayName(Component.text(item.getName()).color(TextColor.color(255, 255, 255))
                .decoration(TextDecoration.ITALIC, false));
        d.setUnbreakable(true);
        NamespacedKey key = new NamespacedKey(KajetansPlugin.instance, "custom");
        AttributeModifier attributeModifier = new AttributeModifier(key, 0,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        d.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attributeModifier);
        d.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(d);
        return stack;
    }

    public static void onPrepareItemCraft(PrepareItemCraftEvent e) {
        ItemStack result = e.getInventory().getResult();
        if(result == null || result.getType() != Material.NETHERITE_HOE) {
            return;
        }
        for(ItemStack stack : e.getInventory().getMatrix()) {
            if(getCustomItem(stack) != null) {
                e.getInventory().setResult(null);
                return;
            }
        }

        ItemStack stack = e.getInventory().getResult();
        correctDamage(stack);
        e.getInventory().setResult(stack);
    }

    private static void correctDamage(ItemStack stack) {
        Damageable meta = (Damageable) stack.getItemMeta();
        if((meta.getDamage() & 1) == 0) {
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            meta.setDamage(meta.getDamage() - 1);
        }
        stack.setItemMeta(meta);
    }
}
