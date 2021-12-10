package me.hammerle.kp.snuviscript.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.LivingEntity;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import net.kyori.adventure.text.Component;

public class BlockCommands {
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
        KajetansPlugin.scriptManager.registerFunction("block.get",
                (sc, in) -> ((Location) in[0].get(sc)).getBlock());
        KajetansPlugin.scriptManager.registerFunction("block.getdata",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData());
        KajetansPlugin.scriptManager.registerConsumer("block.clone", (sc, in) -> {
            Block from = (Block) in[0].get(sc);
            Location to = (Location) in[1].get(sc);
            boolean applyPhysics = in.length > 2 ? in[2].getBoolean(sc) : false;
            to.getBlock().setBlockData(from.getBlockData(), applyPhysics);
            NMS.copyTileEntity(from.getLocation(), to);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.break", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            if(in.length > 1) {
                LivingEntity liv = (LivingEntity) in[1].get(sc);
                b.breakNaturally(liv.getEquipment().getItemInMainHand(), true);
                return;
            }
            b.breakNaturally(true);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.setdata", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            boolean applyPhysics = in.length > 2 ? in[2].getBoolean(sc) : false;
            b.setBlockData((BlockData) in[1].get(sc), applyPhysics);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.setmaterial", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            boolean applyPhysics = in.length > 2 ? in[2].getBoolean(sc) : false;
            b.setType((Material) in[1].get(sc), applyPhysics);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getentity",
                (sc, in) -> NMS.getEntity((Block) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerConsumer("block.setentity",
                (sc, in) -> NMS.setEntity((Block) in[0].get(sc), NMS.toNBT(in[1].get(sc))));
        KajetansPlugin.scriptManager.registerConsumer("block.setsign", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Sign sign = (Sign) b.getState();
            sign.line(in[1].getInt(sc), (Component) in[2].get(sc));
            sign.update(true, false);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getsign", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Sign sign = (Sign) b.getState();
            return sign.line(in[1].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("block.getinventory", (sc, in) -> {
            BlockState state = ((Block) in[0].get(sc)).getState();
            if(state instanceof Container) {
                return ((Container) state).getInventory();
            }
            return null;
        });
        KajetansPlugin.scriptManager.registerConsumer("block.setopen", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Openable o = (Openable) b.getBlockData();
            o.setOpen(in[1].getBoolean(sc));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.isopen", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Openable o = (Openable) b.getBlockData();
            return o.isOpen();
        });
    }
}
