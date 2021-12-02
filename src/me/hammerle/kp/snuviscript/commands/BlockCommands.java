/*package me.km.snuviscript.commands;

import com.mojang.brigadier.StringReader;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.SnuviUtils;
import me.km.inventory.InventoryUtils;
import me.km.utils.Location;
import me.km.utils.Mapper;
import me.km.utils.ReflectionUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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

    private static final Offset[] OFFSETS = new Offset[] {
            // new Offset(-1, -1, -1),
            new Offset(0, -1, -1),
            // new Offset(1, -1, -1),
            new Offset(-1, 0, -1), new Offset(0, 0, -1), new Offset(1, 0, -1),
            // new Offset(-1, 1, -1),
            new Offset(0, 1, -1),
            // new Offset(1, 1, -1),

            new Offset(-1, -1, 0), new Offset(0, -1, 0), new Offset(1, -1, 0), new Offset(-1, 0, 0),
            new Offset(0, 0, 0), new Offset(1, 0, 0), new Offset(-1, 1, 0), new Offset(0, 1, 0),
            new Offset(1, 1, 0),
            // new Offset(-1, -1, 1),
            new Offset(0, -1, 1),
            // new Offset(1, -1, 1),
            new Offset(-1, 0, 1), new Offset(0, 0, 1), new Offset(1, 0, 1),
            // new Offset(-1, 1, 1),
            new Offset(0, 1, 1), // new Offset(1, 1, 1),
    };

    @SuppressWarnings("deprecation")
    private static void breakBlock(ServerWorld w, Entity e, BlockPos pos) {
        BlockState state = w.getBlockState(pos);
        if(state.isAir(w, pos)) {
            return;
        }
        FluidState fState = w.getFluidState(pos);
        if(!(state.getBlock() instanceof AbstractFireBlock)) {
            w.playEvent(2001, pos, Block.getStateId(state));
        }
        TileEntity te = state.hasTileEntity() ? w.getTileEntity(pos) : null;
        ItemStack stack = ItemStack.EMPTY;
        if(e instanceof LivingEntity) {
            stack = ((LivingEntity) e).getHeldItemMainhand();
        }
        int bonusLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
        int silklevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack);
        state.getBlock().dropXpOnBlockBreak(w, pos,
                state.getExpDrop(w, pos, bonusLevel, silklevel));

        Block.spawnDrops(state, w, pos, te, e, stack);
        w.setBlockState(pos, fState.getBlockState(), 3, 512);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("block.gettag", (sc, in) -> BlockTags.getCollection()
                .get(new ResourceLocation(in[0].getString(sc))));
        sm.registerFunction("block.hastag",
                (sc, in) -> ((Tag<Block>) in[0].get(sc)).contains((Block) in[1].get(sc)));
        sm.registerFunction("block.gettype", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getBlockState(l.getBlockPos()).getBlock().getRegistryName()
                    .toString();
        });
        sm.registerFunction("block.isair", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().isAirBlock(l.getBlockPos());
        });
        sm.registerFunction("block.countair", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            IWorld w = l.getWorld();
            BlockPos oldPos = l.getBlockPos();
            BlockPos.Mutable pos =
                    new BlockPos.Mutable(oldPos.getX(), oldPos.getY(), oldPos.getZ());
            int ox = pos.getX();
            int oy = pos.getY();
            int oz = pos.getZ();
            double counter = 0;
            for(Offset off : OFFSETS) {
                pos.setPos(ox + off.x, oy + off.y, oz + off.z);
                if(w.isAirBlock(pos)) {
                    counter++;
                }
            }
            return counter;
        });
        sm.registerFunction("block.get", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getBlockState(l.getBlockPos()).getBlock();
        });
        sm.registerFunction("block.getstate", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getBlockState(l.getBlockPos());
        });
        sm.registerFunction("block.getproperties", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getWorld().getBlockState(l.getBlockPos()).getBlock().getStateContainer()
                    .getProperties();
        });
        sm.registerFunction("block.getproperty",
                (sc, in) -> Mapper.getProperty(in[0].getString(sc)));
        sm.registerFunction("block.property.getvalue", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            Property<?> prop = (Property) in[1].get(sc);
            BlockState state = l.getWorld().getBlockState(l.getBlockPos());
            if(state.hasProperty(prop)) {
                Object o = l.getWorld().getBlockState(l.getBlockPos()).get(prop);
                if(o instanceof Number) {
                    return ((Number) o).doubleValue();
                } else if(o instanceof Boolean) {
                    return o;
                }
                return o.toString();
            }
            return null;
        });
        sm.registerConsumer("block.property.setint", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            World w = l.getWorld();
            BlockPos pos = l.getBlockPos();
            IntegerProperty prop = (IntegerProperty) in[1].get(sc);
            BlockState state = w.getBlockState(pos);
            w.setBlockState(pos, state.with(prop, in[2].getInt(sc)), 18);
        });
        sm.registerConsumer("block.property.setbool", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            World w = l.getWorld();
            BlockPos pos = l.getBlockPos();
            BooleanProperty prop = (BooleanProperty) in[1].get(sc);
            BlockState state = w.getBlockState(pos);
            w.setBlockState(pos, state.with(prop, in[2].getBoolean(sc)), 18);
        });
        sm.registerConsumer("block.property.setenum", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            World w = l.getWorld();
            BlockPos pos = l.getBlockPos();
            EnumProperty prop = (EnumProperty) in[1].get(sc);
            Enum e = (Enum) prop.parseValue(in[2].getString(sc)).get();
            BlockState state = w.getBlockState(pos);
            w.setBlockState(pos, state.with(prop, e), 18);
        });
        sm.registerConsumer("block.clone", (sc, in) -> {
            Location l0 = (Location) in[0].get(sc);
            Location l1 = (Location) in[1].get(sc);

            IWorld w0 = l0.getWorld();
            BlockPos pos0 = l0.getBlockPos();
            BlockState state = w0.getBlockState(pos0);
            TileEntity tileEnt0 = w0.getTileEntity(pos0);

            IWorld w1 = l1.getWorld();
            BlockPos pos1 = l1.getBlockPos();
            w1.setBlockState(pos1, state, 2);
            TileEntity tileEnt1 = w1.getTileEntity(pos1);
            if(tileEnt0 != null && tileEnt1 != null) {
                CompoundNBT nbt = tileEnt0.write(new CompoundNBT());
                nbt.putInt("x", pos1.getX());
                nbt.putInt("y", pos1.getY());
                nbt.putInt("z", pos1.getZ());
                tileEnt1.read(w1.getBlockState(pos1), nbt);
                tileEnt1.markDirty();
            }
        });
        sm.registerConsumer("block.break", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            breakBlock((ServerWorld) l.getWorld(), (Entity) in[1].get(sc), l.getBlockPos());
        });
        sm.registerConsumer("block.set", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            BlockStateParser parser =
                    new BlockStateParser(new StringReader(in[1].getString(sc)), true);
            BlockState state = parser.parse(true).getState();
            int flag = 2;
            if(in.length >= 3 && in[2].getBoolean(sc)) {
                flag |= 16;
            }
            l.getWorld().setBlockState(l.getBlockPos(), state, flag);
        });
        sm.registerFunction("block.newstate", (sc, in) -> {
            try {
                BlockStateParser parser =
                        new BlockStateParser(new StringReader(in[0].getString(sc)), true);
                return parser.parse(true).getState();
            } catch(Exception e) {
                return null;
            }
        });
        sm.registerConsumer("block.setstate", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            int flag = 2;
            if(in.length >= 3 && in[2].getBoolean(sc)) {
                flag |= 16;
            }
            l.getWorld().setBlockState(l.getBlockPos(), (BlockState) in[1].get(sc), flag);
        });
        sm.registerFunction("block.setsign", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            TileEntity te = l.getWorld().getTileEntity(l.getBlockPos());
            if(te == null || !(te instanceof SignTileEntity)) {
                return false;
            }
            SignTileEntity sign = (SignTileEntity) te;
            sign.setText(in[1].getInt(sc), new StringTextComponent(SnuviUtils.connect(sc, in, 2)));
            SUpdateTileEntityPacket packet = sign.getUpdatePacket();
            l.getWorld().getPlayers()
                    .forEach(p -> ((ServerPlayerEntity) p).connection.sendPacket(packet));
            return true;
        });
        sm.registerFunction("block.getsign", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            TileEntity te = l.getWorld().getTileEntity(l.getBlockPos());
            if(te == null || !(te instanceof SignTileEntity)) {
                return null;
            }
            return ReflectionUtils.getSignText((SignTileEntity) te, in[1].getInt(sc)).getString();
        });
        sm.registerConsumer("block.settrapdoorstatus", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            BlockPos pos = l.getBlockPos();
            BlockState state = l.getWorld().getBlockState(pos);
            World w = l.getWorld();
            state = state.with(TrapDoorBlock.OPEN, in[1].getBoolean(sc));
            w.setBlockState(pos, state, 2);
            if(state.get(TrapDoorBlock.WATERLOGGED)) {
                w.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER,
                        Fluids.WATER.getTickRate(w));
            }
            Material m = state.getMaterial();
            if(state.get(TrapDoorBlock.OPEN)) {
                int i = m == Material.IRON ? 1037 : 1007;
                w.playEvent(null, i, pos, 0);
            } else {
                int j = m == Material.IRON ? 1036 : 1013;
                w.playEvent(null, j, pos, 0);
            }
        });
        sm.registerFunction("block.gettrapdoorstatus", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getBlockState().get(TrapDoorBlock.OPEN);
        });
        sm.registerConsumer("block.setdoorstatus", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            BlockPos pos = l.getBlockPos();
            BlockState state = l.getWorld().getBlockState(pos);
            ((DoorBlock) state.getBlock()).openDoor((World) l.getWorld(), state, pos,
                    in[1].getBoolean(sc));
        });
        sm.registerFunction("block.getdoorstatus", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return l.getBlockState().get(DoorBlock.OPEN);
        });
        sm.registerFunction("block.issolid", (sc, in) -> {
            return CommandUtils.getBlockState((Location) in[0].get(sc)).isSolid();
        });
        sm.registerFunction("block.tostack", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return new ItemStack(l.getBlockState().getBlock().asItem());
        });
        sm.registerFunction("block.getitemamount", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            TileEntity te = l.getWorld().getTileEntity(l.getBlockPos());
            if(te == null || !(te instanceof ChestTileEntity)) {
                return 0.0d;
            }
            return (double) InventoryUtils.searchInventoryFor((ChestTileEntity) te,
                    (ItemStack) in[2].get(sc), in[1].getBoolean(sc));
        });
        sm.registerFunction("block.getsecchest", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            BlockPos pos = l.getBlockPos();
            BlockState state = l.getWorld().getBlockState(pos);
            ChestType chesttype = state.get(ChestBlock.TYPE);
            if(chesttype == ChestType.SINGLE) {
                return null;
            }
            Direction dir = ChestBlock.getDirectionToAttached(state);
            return l.copyAdd(dir.getXOffset(), dir.getYOffset(), dir.getZOffset());
        });
        sm.registerFunction("block.additem", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ItemStack stack = ((ItemStack) in[1].get(sc));
            TileEntity te = l.getWorld().getTileEntity(l.getBlockPos());
            if(te == null || !(te instanceof ChestTileEntity)) {
                return stack;
            }
            stack.setCount(InventoryUtils.addToInventory((ChestTileEntity) te, stack));
            return stack;
        });
        sm.registerFunction("block.subitem", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            ItemStack stack = ((ItemStack) in[1].get(sc));
            TileEntity te = l.getWorld().getTileEntity(l.getBlockPos());
            if(te == null || !(te instanceof ChestTileEntity)) {
                return stack;
            }
            stack.setCount(InventoryUtils.removeFromInventory((ChestTileEntity) te, stack));
            return stack;
        });
        sm.registerConsumer("block.setspawnertype", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            MobSpawnerTileEntity spawner =
                    (MobSpawnerTileEntity) l.getWorld().getTileEntity(l.getBlockPos());
            spawner.getSpawnerBaseLogic()
                    .setEntityType(EntityType.byKey(in[1].getString(sc)).get());
        });
        sm.registerFunction("block.getinv", (sc, in) -> {
            Location l = (Location) in[0].get(sc);
            return (IInventory) l.getWorld().getTileEntity(l.getBlockPos());
        });
    }
}
*/
