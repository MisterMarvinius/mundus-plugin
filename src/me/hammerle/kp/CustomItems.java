package me.hammerle.kp;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import net.kyori.adventure.text.Component;

public class CustomItems {
    private static final UUID CUSTOM_UUID = new UUID(3242, 42343);
    private static final HashMap<Integer, CustomItem> ID_TO_ITEM = new HashMap<>();

    public enum CustomItem {
        GOLD_COIN(2, "Gold Coin", 64),
        SILVER_COIN(4, "Silver Coin", 64),
        COPPER_COIN(6, "Copper Coin", 64),
        ARROW_DOWN(10, "Down Arrow", 64),
        ARROW_LEFT(12, "Left Arrow", 64),
        ARROW_RIGHT(14, "Right Arrow", 64),
        ARROW_UP(16, "Up Arrow", 64),
        BAT_WING(18, "Bat Wing", 64),
        BEAR_POLAR_FUR(20, "Polar Bear Fur", 64),
        CHECK_GREEN(22, "Green Check", 64),
        CHECK_RED(24, "Red Check", 64),
        CLOTH(26, "Cloth", 64),
        COARSE_CLOTH(28, "Coarse Cloth", 64),
        DIGIT_0(30, "Digit 0", 64),
        DIGIT_1(32, "Digit 1", 64),
        DIGIT_2(34, "Digit 2", 64),
        DIGIT_3(36, "Digit 3", 64),
        DIGIT_4(38, "Digit 4", 64),
        DIGIT_5(40, "Digit 5", 64),
        DIGIT_6(42, "Digit 6", 64),
        DIGIT_7(44, "Digit 7", 64),
        DIGIT_8(46, "Digit 8", 64),
        DIGIT_9(48, "Digit 9", 64),
        PLUS_GREEN(50, "Green Plus", 64),
        PLUS_RED(52, "Red Plus", 64),
        REFRESH_GREEN(54, "Green Refresh", 64),
        REFRESH_RED(56, "Red Refresh", 64),
        RETURN_GREEN(58, "Green Return", 64),
        RETURN_RED(60, "Red Return", 64),
        RUBY(62, "Ruby", 64),
        AMBER(64, "Amber", 64),
        SAPPHIRE(66, "Sapphire", 64),
        COPPER_KEY(68, "Copper Key", 64),
        IRON_KEY(70, "Iron Key", 64),
        GOLDEN_KEY(72, "Golden Key", 64),
        CROSS_GREEN(74, "Green Cross", 64),
        CROSS_RED(76, "Red Cross", 64),
        GEAR(80, "Gear", 64),
        MINUS_GREEN(82, "Green Minus", 64),
        MINUS_RED(84, "Red Minus", 64),
        MUSHROOM_STICK_COOKED(86, "Raw Mushroom Stick", 64),
        MUSHROOM_STICK_RAW(88, "Cooked Mushroom Stick", 64),
        WOLF_FUR(94, "Wolf Fur", 64),
        SCROLL(96, "Scroll", 64);

        private final int id;
        private final String name;
        private final int maxStackSize;

        private CustomItem(int id, String name, int maxStackSize) {
            this.id = id;
            this.name = name;
            this.maxStackSize = maxStackSize;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getMaxStackSize() {
            return maxStackSize;
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
        if(getCustomItem(stack) != null) {
            return;
        }
        Damageable meta = (Damageable) stack.getItemMeta();
        if((meta.getDamage() & 1) == 0) {
            meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            meta.setDamage(meta.getDamage() - 1);
        }
        stack.setItemMeta(meta);
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
        if(stack.getType() != Material.NETHERITE_HOE) {
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
        d.displayName(Component.text("§f" + item.getName()));
        d.setUnbreakable(true);
        d.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(CUSTOM_UUID,
                "custom", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        d.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(d);
        return stack;
    }

    public static int getMaxStackSize(ItemStack stack, int vanillaAmount) {
        CustomItem item = getCustomItem(stack);
        return item == null ? vanillaAmount : item.getMaxStackSize();
    }
}
