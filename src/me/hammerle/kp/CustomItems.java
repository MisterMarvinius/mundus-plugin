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
        GOLD_COIN(2, "Gold Coin", 64), SILVER_COIN(4, "Silver Coin", 64), COPPER_COIN(6,
                "Copper Coin", 64);

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
