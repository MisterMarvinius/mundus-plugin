package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.KajetansPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;

public class CitizenCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("citizen.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            NPC npc =
                    CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, in[1].getString(sc));
            npc.spawn(l);
            return npc;
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.despawn", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.despawn();
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.destroy", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.destroy();
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.setskin", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            SkinTrait skin = npc.getOrAddTrait(SkinTrait.class);
            skin.setSkinPersistent(npc.getName(), in[2].getString(sc), in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.setname", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.setName(in[1].getString(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("citizen.getname", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            return npc.getName();
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.shownameplate", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, in[1].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.setequip", (sc, in) -> {
            NPC npc = (NPC) in[0].get(sc);
            npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class)
                    .set(EquipmentSlot.valueOf(in[1].getString(sc)), (ItemStack) in[2].get(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("citizen.lookclose", (sc, in) -> {
            LookClose lookClose = new LookClose();
            lookClose.lookClose(true);
            NPC npc = (NPC) in[0].get(sc);
            npc.addTrait(lookClose);
        });

    }
}
