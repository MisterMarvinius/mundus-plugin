/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Location;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;

public class ItemEntityCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("item.entity.get", (sc, in) -> ((ItemEntity) in[0].get(sc)).getItem());
        sm.registerConsumer("item.entity.set", (sc, in) -> {
            ((ItemEntity) in[0].get(sc)).setItem((ItemStack) in[1].get(sc));
        });
        sm.registerFunction("item.entity.new", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ItemEntity item = new ItemEntity(EntityType.ITEM, l.getWorld());
            item.setPosition(l.getX(), l.getY(), l.getZ());
            ItemStack stack = (ItemStack) in[1].get(sc);
            item.setItem(stack);
            item.lifespan = stack.getEntityLifespan(l.getWorld());
            return item;
        });
        sm.registerConsumer("item.entity.spawn", (sc, in) -> {
            ItemEntity ent = (ItemEntity) in[0].get(sc);
            ent.world.addEntity(ent);
        });
        sm.registerConsumer("item.entity.setlifespan", (sc, in) -> {
            ((ItemEntity) in[0].get(sc)).lifespan = in[1].getInt(sc);
        });
    }
}
*/
