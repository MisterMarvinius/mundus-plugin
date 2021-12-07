package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;

public class BlockCommands {
    private static class Offset {
        private final int x;
        private final int y;
        private final int z;

        public Offset(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static final Offset[] OFFSETS = new Offset[] {new Offset(0, -1, -1),
            new Offset(-1, 0, -1), new Offset(0, 0, -1), new Offset(1, 0, -1), new Offset(0, 1, -1),
            new Offset(-1, -1, 0), new Offset(0, -1, 0), new Offset(1, -1, 0), new Offset(-1, 0, 0),
            new Offset(0, 0, 0), new Offset(1, 0, 0), new Offset(-1, 1, 0), new Offset(0, 1, 0),
            new Offset(1, 1, 0), new Offset(0, -1, 1), new Offset(-1, 0, 1), new Offset(0, 0, 1),
            new Offset(1, 0, 1), new Offset(0, 1, 1)};

    @SuppressWarnings("unchecked")
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("block.gettag",
                (sc, in) -> Bukkit.getTag(Tag.REGISTRY_BLOCKS,
                        NamespacedKey.fromString(in[0].getString(sc)), Material.class));
        KajetansPlugin.scriptManager.registerFunction("block.hastag", (sc,
                in) -> ((Tag<Material>) in[1].get(sc)).isTagged(((Block) in[0].get(sc)).getType()));
        KajetansPlugin.scriptManager.registerFunction("block.gettype",
                (sc, in) -> ((Block) in[0].get(sc)).getType());
        KajetansPlugin.scriptManager.registerFunction("block.getlocation",
                (sc, in) -> ((Block) in[0].get(sc)).getLocation());
        KajetansPlugin.scriptManager.registerFunction("block.isair",
                (sc, in) -> ((Location) in[0].get(sc)).getBlock().getType() == Material.AIR);
        KajetansPlugin.scriptManager.registerFunction("block.countair", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Location o = l.clone();
            double counter = 0;
            for(Offset off : OFFSETS) {
                o.add(off.x, off.y, off.z);
                if(o.getBlock().getType() == Material.AIR) {
                    counter++;
                }
            }
            return counter;
        });
        KajetansPlugin.scriptManager.registerFunction("block.get",
                (sc, in) -> ((Location) in[0].get(sc)).getBlock());
        KajetansPlugin.scriptManager.registerFunction("block.getdata",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData());
        KajetansPlugin.scriptManager.registerConsumer("block.clone", (sc, in) -> {
            Location from = (Location) in[0].get(sc);
            Location to = (Location) in[1].get(sc);
            to.getBlock().setBlockData(from.getBlock().getBlockData());
            NMS.copyTileEntity(from, to);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.break", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            LivingEntity liv = (LivingEntity) in[1].get(sc);
            l.getBlock().breakNaturally(liv.getEquipment().getItemInMainHand(), true);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.set", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            l.getWorld().setBlockData(l, (BlockData) in[0].get(sc));
        });
    }
}
