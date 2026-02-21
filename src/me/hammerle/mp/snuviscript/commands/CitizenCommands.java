package me.hammerle.mp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import me.hammerle.mp.MundusPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;

public class CitizenCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("citizen.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            NPC npc =
                    CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, in[1].getString(sc));
            npc.spawn(l);
            return npc;
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("citizen.despawn", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.despawn();
        });
        MundusPlugin.scriptManager.registerConsumer("citizen.destroy", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.destroy();
        });
        MundusPlugin.scriptManager.registerConsumer("citizen.setskin", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            SkinTrait skin = npc.getOrAddTrait(SkinTrait.class);
            skin.setSkinPersistent(npc.getName(), in[2].getString(sc), in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("citizen.setname", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.setName(in[1].getString(sc));
        });
        MundusPlugin.scriptManager.registerFunction("citizen.getname", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            return npc.getName();
        }, "object");
        MundusPlugin.scriptManager.registerConsumer("citizen.shownameplate", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, in[1].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("citizen.setequip", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class)
                    .set(EquipmentSlot.valueOf(in[1].getString(sc)), (ItemStack) in[2].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("citizen.lookclose", (sc, in) -> {
            LookClose lookClose = new LookClose();
            lookClose.lookClose(true);
            NPC npc = (NPC) in[0].get(sc);
            npc.addTrait(lookClose);
        });

    }
}
