package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import me.hammerle.kp.KajetansPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
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
    }
}
