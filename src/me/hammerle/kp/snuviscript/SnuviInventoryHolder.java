package me.hammerle.kp.snuviscript;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;

public class SnuviInventoryHolder implements InventoryHolder {
    public enum SnuviSlotType {
        BLOCKED, NORMAL, CLICK_EVENT_1, CLICK_EVENT_2
    }

    private static int ids = 0;

    private final SnuviSlotType[] types;
    private Inventory inventory;
    private int id = ids++;

    public static Inventory create(String info, Component title) {
        int allSlots = info.length();
        allSlots = Math.min(9 * 6, allSlots);
        int slots = allSlots == 0 ? 9 : allSlots + 8 - ((allSlots - 1) % 9);
        SnuviSlotType[] types = new SnuviSlotType[slots];
        SnuviSlotType[] values = SnuviSlotType.values();
        for(int i = 0; i < allSlots; i++) {
            int id = Character.getNumericValue(info.charAt(i));
            if(id > 3 || id < 0) {
                id = 0;
            }
            types[i] = values[id];
        }
        for(int i = allSlots; i < slots; i++) {
            types[i] = SnuviSlotType.BLOCKED;
        }
        SnuviInventoryHolder holder = new SnuviInventoryHolder(types);
        Inventory inv = Bukkit.createInventory(holder, slots, title);
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        stack.editMeta(meta -> meta.displayName(Component.text("")));
        for(int i = 0; i < slots; i++) {
            if(types[i] != SnuviSlotType.NORMAL) {
                inv.setItem(i, stack);
            }
        }
        holder.inventory = inv;
        return inv;
    }

    private SnuviInventoryHolder(SnuviSlotType[] types) {
        this.types = types;
    }

    public SnuviSlotType getSlotType(int i) {
        return types[i];
    }

    public int getId() {
        return id;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
