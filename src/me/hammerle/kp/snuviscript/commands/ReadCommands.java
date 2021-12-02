package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.hammerle.kp.KajetansPlugin;

public class ReadCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("read.player", (sc, in) -> {
            String name = in[0].getString(sc);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.getName().equals(name)) {
                    return p;
                }
            }
            return null;
        });

        /*sm.registerFunction("read.location", (sc, in) -> new Location(server, in[0].getString(sc)));
        sm.registerFunction("read.item", (sc, in) -> {
        try {
            String s = in[0].getString(sc);
            if(s.startsWith("{")) {
                String left = SnuviUtils.connect(sc, in, 1);
                CompoundNBT c = JsonToNBT.getTagFromJson(s + left);
                return ItemStack.read(c);
            }
            Item item = Mapper.getItem(s);
            int amount = in.length >= 2 ? in[1].getInt(sc) : 1;
            ItemStack stack = new ItemStack(item, amount);
            if(in.length >= 3) {
                stack.setDisplayName(new StringTextComponent(in[2].getString(sc)));
            }
            if(in.length >= 4) {
                for(int i = 3; i < in.length; i++) {
                    ItemStackUtils.addLore(stack, in[i].getString(sc));
                }
            }
            return stack;
        } catch(Exception ex) {
            return null;
        }
        });
        sm.registerFunction("read.spawnmob", (sc, in) -> {
        Location l = (Location) in[0].get(sc);
        ServerWorld sw = (ServerWorld) l.getWorld();
        CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(SnuviUtils.connect(sc, in, 1));
        Entity entity = EntityType.loadEntityAndExecute(compoundnbt, sw, ent -> {
            ent.addTag("mod_spawned");
            ent.setLocationAndAngles(l.getX(), l.getY(), l.getZ(), ent.rotationYaw,
                    ent.rotationPitch);
            return sw.summonEntity(ent) ? ent : null;
        });
        return entity;
        });
        sm.registerFunction("read.uuid", (sc, in) -> UUID.fromString(in[0].getString(sc)));
        sm.registerFunction("read.slot", (sc, in) -> {
        String name = in[0].getString(sc);
        for(EquipmentSlotType slot : EquipmentSlotType.values()) {
            if(slot.getName().equals(name)) {
                return slot;
            }
        }
        return null;
        });*/
    }
}
