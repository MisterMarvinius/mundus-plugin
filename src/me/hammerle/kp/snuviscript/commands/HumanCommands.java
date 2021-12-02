/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.entities.EntityHuman;
import me.km.entities.ModEntities;
import me.km.utils.Location;
import net.minecraft.world.World;

public class HumanCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("human.spawn", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            World w = l.getWorld();
            EntityHuman h = ModEntities.HUMAN.create(w);
            h.setPosition(l.getX(), l.getY(), l.getZ());
            w.addEntity(h);
            return h;
        });
        sm.registerConsumer("human.setstatue", (sc, in) -> {
            ((EntityHuman) in[0].get(sc)).setStatue(in[1].getBoolean(sc));
        });
        sm.registerConsumer("human.setskin", (sc, in) -> {
            ((EntityHuman) in[0].get(sc)).setSkinName(in[1].getString(sc));
        });
        sm.registerConsumer("human.setscale", (sc, in) -> {
            ((EntityHuman) in[0].get(sc)).setScale(in[1].getFloat(sc));
        });
        sm.registerConsumer("human.setslim", (sc, in) -> {
            ((EntityHuman) in[0].get(sc)).setSlim(in[1].getBoolean(sc));
        });
    }
}
*/
