package me.hammerle.kp.snuviscript.commands;

import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;
import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import me.hammerle.kp.KajetansPlugin;
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
            Block fromBlock = (Block) in[0].get(sc);
            Location toLoc = (Location) in[1].get(sc);
            boolean applyPhysics = in.length > 2 ? in[2].getBoolean(sc) : false;

            toLoc.getBlock().setType(fromBlock.getType(), applyPhysics);
            toLoc.getBlock().setBlockData(fromBlock.getBlockData(), applyPhysics);

            Block toBlock = toLoc.getBlock();

            NBTBlock fromNBTBlock = new NBTBlock(fromBlock);
            NBTBlock toNBTBlock = new NBTBlock(toBlock);
            NBTCompound fromNBT = fromNBTBlock.getData();
            NBTCompound toNBT = toNBTBlock.getData();
            toNBT.mergeCompound(fromNBT);

            // Handle InventoryHolder (like chests, furnaces, etc.)
            if(fromBlock.getState() instanceof InventoryHolder
                    && toBlock.getState() instanceof InventoryHolder) {
                InventoryHolder fromHolder = (InventoryHolder) fromBlock.getState();
                InventoryHolder toHolder = (InventoryHolder) toBlock.getState();
                toHolder.getInventory().setContents(fromHolder.getInventory().getContents());
            }

            // Update the block state to apply changes
            BlockState state = toBlock.getState();
            state.update(true, applyPhysics);
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
        KajetansPlugin.scriptManager.registerConsumer("block.setsign", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Sign sign = (Sign) b.getState();
            SignSide side = (SignSide) sign.getSide(Side.valueOf(in[1].getString(sc)));
            side.line(in[2].getInt(sc), (Component) in[3].get(sc));
            sign.update(true, false);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getsign", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Sign sign = (Sign) b.getState();
            SignSide side = (SignSide) sign.getSide(Side.valueOf(in[1].getString(sc)));
            return side.line(in[2].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("block.signsetwaxed", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Sign sign = (Sign) b.getState();
            sign.setWaxed(in[1].getBoolean(sc));
            sign.update(true, false);
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
        KajetansPlugin.scriptManager.registerFunction("block.isopenable",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Openable);
        KajetansPlugin.scriptManager.registerConsumer("block.setdoorhinge", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Door o = (Door) b.getBlockData();
            o.setHinge(Door.Hinge.valueOf(in[1].getString(sc)));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getdoorhinge", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Door o = (Door) b.getBlockData();
            return o.getHinge().toString();
        });
        KajetansPlugin.scriptManager.registerFunction("block.isdoor",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Door);
        KajetansPlugin.scriptManager.registerConsumer("block.setbisectedhalf", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Bisected o = (Bisected) b.getBlockData();
            o.setHalf(Bisected.Half.valueOf(in[1].getString(sc)));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getbisectedhalf", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Bisected o = (Bisected) b.getBlockData();
            return o.getHalf().toString();
        });
        KajetansPlugin.scriptManager.registerFunction("block.isbisected",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Bisected);
        KajetansPlugin.scriptManager.registerConsumer("block.setdirectionalface", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Directional o = (Directional) b.getBlockData();
            o.setFacing(BlockFace.valueOf(in[1].getString(sc)));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getdirectionalface", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Directional o = (Directional) b.getBlockData();
            return o.getFacing().toString();
        });
        KajetansPlugin.scriptManager.registerFunction("block.getdirectionalfaces", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Directional o = (Directional) b.getBlockData();
            return o.getFaces().stream().map(f -> f.toString()).collect(Collectors.toList());
        });
        KajetansPlugin.scriptManager.registerFunction("block.isdirectional",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Directional);
        KajetansPlugin.scriptManager.registerConsumer("block.setpersistent", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Leaves o = (Leaves) b.getBlockData();
            o.setPersistent(in[1].getBoolean(sc));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.ispersistent", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Leaves o = (Leaves) b.getBlockData();
            return o.isPersistent();
        });
        KajetansPlugin.scriptManager.registerFunction("block.isleaves",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Leaves);
        KajetansPlugin.scriptManager.registerFunction("block.isbed",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Bed);
        KajetansPlugin.scriptManager.registerConsumer("block.setbedpart", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Bed o = (Bed) b.getBlockData();
            o.setPart(Bed.Part.valueOf(in[1].getString(sc)));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerFunction("block.getbedpart", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Bed o = (Bed) b.getBlockData();
            return o.getPart().toString();
        });
        KajetansPlugin.scriptManager.registerFunction("block.canhavewater",
                (sc, in) -> ((Block) in[0].get(sc)).getBlockData() instanceof Waterlogged);
        KajetansPlugin.scriptManager.registerFunction("block.iswaterlogged", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Waterlogged o = (Waterlogged) b.getBlockData();
            return o.isWaterlogged();
        });
        KajetansPlugin.scriptManager.registerConsumer("block.setwaterlogged", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Waterlogged o = (Waterlogged) b.getBlockData();
            o.setWaterlogged(in[1].getBoolean(sc));
            b.setBlockData(o);
        });
        KajetansPlugin.scriptManager.registerConsumer("block.setwaterlogged", (sc, in) -> {
            Block b = (Block) in[0].get(sc);
            Waterlogged o = (Waterlogged) b.getBlockData();
            o.setWaterlogged(in[1].getBoolean(sc));
            b.setBlockData(o);
        });
    }
}
