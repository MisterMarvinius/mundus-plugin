/*package me.km.snuviscript.commands;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Multimap;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.km.utils.ItemStackUtils;
import me.km.utils.Location;
import me.km.utils.Mapper;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemCommands {
    @SuppressWarnings("unchecked")
    public static void registerFunctions(ScriptManager sm) {
        sm.registerConsumer("item.drop", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            World w = l.getWorld();
            BlockPos pos = l.getBlockPos();
            ItemStack stack = ((ItemStack) in[1].get(sc)).copy();
            int amount = stack.getCount();
            while(amount > stack.getMaxStackSize()) {
                stack.setCount(stack.getMaxStackSize());
                amount -= stack.getMaxStackSize();
                Block.spawnAsEntity(w, pos, stack.copy());
            }
            if(amount > 0) {
                stack.setCount(amount);
                Block.spawnAsEntity(w, pos, stack);
            }
        });
        sm.registerFunction("item.gettag", (sc, in) -> ItemTags.getCollection()
                .get(new ResourceLocation(in[0].getString(sc))));
        sm.registerFunction("item.hastag", (sc, in) -> ((Tag<Item>) in[0].get(sc))
                .contains(((ItemStack) in[1].get(sc)).getItem()));
        sm.registerFunction("item.get", (sc, in) -> ((ItemStack) in[0].get(sc)).getItem());
        sm.registerFunction("item.gettype",
                (sc, in) -> ((ItemStack) in[0].get(sc)).getItem().getRegistryName().toString());
        sm.registerFunction("item.getmaxamount",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getMaxStackSize());
        sm.registerFunction("item.getamount",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getCount());
        sm.registerConsumer("item.setamount",
                (sc, in) -> ((ItemStack) in[0].get(sc)).setCount(in[1].getInt(sc)));
        sm.registerFunction("item.getfulltext",
                (sc, in) -> ((ItemStack) in[0].get(sc)).getTextComponent());
        sm.registerFunction("item.hasname",
                (sc, in) -> ((ItemStack) in[0].get(sc)).hasDisplayName());
        sm.registerFunction("item.getname",
                (sc, in) -> ((ItemStack) in[0].get(sc)).getDisplayName().getString());
        sm.registerConsumer("item.setname", (sc, in) -> {
            ((ItemStack) in[0].get(sc))
                    .setDisplayName(new StringTextComponent(SnuviUtils.connect(sc, in, 1)));
        });
        sm.registerFunction("item.getlore",
                (sc, in) -> ItemStackUtils.getLore((ItemStack) in[0].get(sc)));
        sm.registerConsumer("item.setlore", (sc, in) -> {
            ItemStackUtils.setLore((ItemStack) in[0].get(sc), (List<Object>) in[1].get(sc));
        });
        sm.registerConsumer("item.addlore", (sc, in) -> {
            ItemStackUtils.addLore((ItemStack) in[0].get(sc), SnuviUtils.connect(sc, in, 2),
                    in[1].getInt(sc));
        });
        sm.registerConsumer("item.setcooldown", (sc, in) -> {
            ((PlayerEntity) in[0].get(sc)).getCooldownTracker()
                    .setCooldown(((ItemStack) in[1].get(sc)).getItem(), in[2].getInt(sc));
        });
        sm.registerConsumer("item.addpotion", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            List<EffectInstance> list = PotionUtils.getEffectsFromStack(stack);
            list.add(new EffectInstance(Mapper.getPotion(in[1].getString(sc)), in[2].getInt(sc),
                    in[3].getInt(sc)));
            PotionUtils.appendEffects(stack, list);
        });
        sm.registerFunction("item.hide", (sc, in) -> {
            int flag = 0;
            switch(in[1].getString(sc)) {
                case "enchantments":
                    flag = 1;
                    break;
                case "attributes":
                    flag = 2;
                    break;
                case "unbreakable":
                    flag = 4;
                    break;
                case "destroys":
                    flag = 8;
                    break;
                case "placed_on":
                    flag = 16;
                    break;
                case "potion_effects":
                    flag = 32;
                    break;
            }
            if(flag == 0) {
                return false;
            }
            ItemStack stack = (ItemStack) in[0].get(sc);
            CompoundNBT com = stack.getTag() != null ? stack.getTag() : new CompoundNBT();
            com.putInt("HideFlags", com.getInt("HideFlags") | flag);
            stack.setTag(com);
            return true;
        });
        sm.registerConsumer("item.addattribute", (sc, in) -> {
            Attribute a =
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(in[1].getString(sc)));
            ((ItemStack) in[0].get(sc)).addAttributeModifier(a,
                    new AttributeModifier("modifier", in[3].getDouble(sc),
                            AttributeModifier.Operation.values()[in[4].getInt(sc)]),
                    (EquipmentSlotType) in[2].get(sc));
        });
        sm.registerConsumer("item.clearattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            if(stack.hasTag()) {
                stack.getTag().remove("AttributeModifiers");
            }
        });
        sm.registerFunction("item.hasattributes", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            return stack.hasTag() && stack.getTag().contains("AttributeModifiers", 9);
        });
        sm.registerConsumer("item.adddefaulttags", (sc, in) -> {
            ItemStack stack = (ItemStack) in[0].get(sc);
            for(EquipmentSlotType slot : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> attributes =
                        stack.getAttributeModifiers(slot);
                for(Map.Entry<Attribute, AttributeModifier> e : attributes.entries()) {
                    stack.addAttributeModifier(e.getKey(), e.getValue(), slot);
                }
            }
        });
        sm.registerFunction("item.clone", (sc, in) -> ((ItemStack) in[0].get(sc)).copy());
        sm.registerFunction("item.getmaxdamage",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getMaxDamage());
        sm.registerFunction("item.isdamageable",
                (sc, in) -> ((ItemStack) in[0].get(sc)).isDamageable());
        sm.registerFunction("item.getdamage",
                (sc, in) -> (double) ((ItemStack) in[0].get(sc)).getDamage());
        sm.registerConsumer("item.setdamage", (sc, in) -> {
            ((ItemStack) in[0].get(sc)).setDamage(in[1].getInt(sc));
        });
    }
}
*/
