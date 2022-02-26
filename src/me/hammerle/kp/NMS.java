package me.hammerle.kp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import org.bukkit.craftbukkit.v1_18_R1.entity.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.snuviscript.ScriptEvents;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireballFireball;
import net.minecraft.world.entity.projectile.EntityFireworks;
import net.minecraft.world.entity.projectile.EntityWitherSkull;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.entity.ai.attributes.AttributeDefaults;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;

public class NMS {
    private final static HashMap<String, DamageSource> DAMAGE_SOURCES = new HashMap<>();

    private static EntityTypes<RawHuman.WrapperHuman> HUMAN_TYPE;
    public static String humanPrefix = "A";
    public static String humanSuffix = "B";

    @SuppressWarnings("unchecked")
    public static void init() {
        try {
            Method m = EntityTypes.class.getDeclaredMethod("a", String.class,
                    EntityTypes.Builder.class);
            m.setAccessible(true);
            HUMAN_TYPE =
                    (EntityTypes<RawHuman.WrapperHuman>) m.invoke(null, "human", EntityTypes.Builder
                            .a(RawHuman.WrapperHuman::new, EnumCreatureType.a).a(0.6f, 1.95f));

            final Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

            Field attributesMapField = AttributeDefaults.class.getDeclaredField("b");
            attributesMapField.setAccessible(true);

            Object base = unsafe.staticFieldBase(attributesMapField);
            long offset = unsafe.staticFieldOffset(attributesMapField);

            Map<EntityTypes<? extends EntityLiving>, AttributeProvider> attributesMap =
                    (Map<EntityTypes<? extends EntityLiving>, AttributeProvider>) attributesMapField
                            .get(null);
            attributesMap = new HashMap<>(attributesMap);
            attributesMap.put(HUMAN_TYPE,
                    EntityMonster.fD().a(GenericAttributes.a, 20.0).a(GenericAttributes.f, 1)
                            .a(GenericAttributes.d, 0.23).a(GenericAttributes.h).a());
            unsafe.putObject(base, offset, attributesMap);

            AttributeDefaults.a();
        } catch(Exception ex) {
            ex.printStackTrace();
            KajetansPlugin.warn(ex.getMessage());
        }

        net.minecraft.world.item.ItemStack.maxStackSizeHook = (stack, vanilla) -> {
            return CustomItems.getMaxStackSize(stack.getBukkitStack(), vanilla);
        };
    }

    private static CraftServer getCraftServer() {
        return (CraftServer) Bukkit.getServer();
    }

    public static interface Human extends Creature {
        public void setSkin(String texture, String signature);

        public void setName(String name);

        public void setSkin(PlayerProfile profile);

        public void canMove(boolean b);

        public void setAI(int type);

        public int moveTo(double x, double y, double z);

        public int getAI();

        public String getName();
    }

    private static String cutName(String name) {
        if(name.length() > 16) {
            return name.substring(0, 16);
        }
        return name;
    }

    private static class RawHuman extends CraftCreature implements Human {
        private static class WrapperHuman extends EntityCreature {
            private static class Goal {
                private static int ids = 0;
                private int id = ids++;
                private double x;
                private double y;
                private double z;
                private int ticks = 20 * 30;

                public Goal(double x, double y, double z) {
                    this.x = x;
                    this.y = y;
                    this.z = z;
                }
            };

            private EntityPlayer player = null;
            private boolean canMove = false;
            private int ai = 0;
            private LinkedList<Goal> goals = new LinkedList<>();
            private EntityLiving lastAttacker = null;
            private int attackTimer = 0;

            public WrapperHuman(EntityTypes<? extends WrapperHuman> type,
                    net.minecraft.world.level.World world) {
                super(type, world);
                setPlayer("Default", world);
            }

            private void setPlayer(String name, net.minecraft.world.level.World world) {
                EntityPlayer newPlayer =
                        new EntityPlayer(getCraftServer().getServer(), (WorldServer) world,
                                new GameProfile(cm(), cutName(name)));
                if(player != null) {
                    PropertyMap newProps = newPlayer.fp().getProperties();
                    for(var entry : player.fp().getProperties().entries()) {
                        newProps.put(entry.getKey(), entry.getValue());
                    }
                }
                newPlayer.e(ae());
                player = newPlayer;
            }

            private void tickGoals() {
                if(goals.isEmpty()) {
                    return;
                }
                Goal g = goals.getFirst();
                double diffX = dc() - g.x;
                double diffY = de() - g.y;
                double diffZ = di() - g.z;
                double distance = diffX * diffX + diffY * diffY + diffZ * diffZ;
                if(distance < 1.0f) {
                    goals.removeFirst();
                    Human h = getWrappedEntity();
                    ScriptEvents.onHumanGoalReach(h, new Location(h.getWorld(), g.x, g.y, g.z),
                            g.id);
                    return;
                }
                g.ticks--;
                if(g.ticks <= 0) {
                    goals.removeFirst();
                    Human h = getWrappedEntity();
                    ScriptEvents.onHumanGoalTimeout(h, new Location(h.getWorld(), g.x, g.y, g.z),
                            g.id);
                    return;
                }
                stopGoals();
                D().a(g.x, g.y, g.z, 1.0);
            }

            @Override
            public void k() { // tick
                tickGoals();
                super.k();
                o(ce()); // setYRot(getYHeadRot());

                if(attackTimer > 0) {
                    attackTimer--;
                    if(attackTimer == 0) {
                        lastAttacker = null;
                    }
                }
            }

            @Override
            public boolean a(DamageSource ds, float amount) { // hurt
                if(ds.l() instanceof EntityLiving) {
                    lastAttacker = (EntityLiving) ds.l();
                    attackTimer = 20 * 60;
                } else if(ds.k() instanceof EntityLiving) {
                    lastAttacker = (EntityLiving) ds.k();
                    attackTimer = 20 * 60;
                }
                if(ScriptEvents.onHumanHurt(ds, getWrappedEntity(), amount, ai == 0)) {
                    return false;
                }
                return super.a(ds, amount);
            }

            public RawHuman getWrappedEntity() {
                return new RawHuman(this);
            }

            @Override
            public CraftEntity getBukkitEntity() {
                return getWrappedEntity();
            }

            public EntityPlayer update(PacketPlayOutSpawnEntityLiving p) {
                setTabName();
                player.e(p.b()); // set id, get id
                player.a(p.e(), p.f(), p.g(), 0.0f, 0.0f);
                return player;
            }

            private void activateSkinOverlays() {
                player.ai().b(EntityHuman.bQ, (byte) 0xFF);
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            private void copyMeta() {
                var playerData = player.ai();
                for(var item : ai().c()) {
                    DataWatcherObject o = item.a();
                    Object po = playerData.a(o);
                    if(po.getClass() == item.b().getClass()) {
                        playerData.b(o, item.b());
                    }
                }
            }

            public PacketPlayOutEntityMetadata update(PacketPlayOutEntityMetadata p) {
                copyMeta();
                activateSkinOverlays();
                return new PacketPlayOutEntityMetadata(p.c(), player.ai(), true);
            }

            @Override
            public void b(NBTTagCompound nbt) { // addAdditionalSaveData
                super.b(nbt);

                GameProfile gp = player.fp();
                nbt.a("HumanName", gp.getName());

                Collection<Property> c = gp.getProperties().get("textures");
                for(Property p : c) {
                    if(p.getName().equals("textures")) {
                        nbt.a("HumanTexture", p.getValue());
                        nbt.a("HumanSignature", p.getSignature());
                        break;
                    }
                }

                nbt.a("HumanCanMove", canMove);
                nbt.a("HumanAI", ai);
            }

            @Override
            public void a(NBTTagCompound nbt) { // readAdditionalSaveData
                super.a(nbt);
                if(nbt.b("HumanName", 8)) {
                    String name = cutName(nbt.l("HumanName"));
                    setPlayer(name, player.t);
                }
                if(nbt.b("HumanTexture", 8) && nbt.b("HumanSignature", 8)) {
                    String texture = nbt.l("HumanTexture");
                    String signature = nbt.l("HumanSignature");
                    setSkinWithoutPacket(texture, signature);
                }
                if(nbt.b("HumanCanMove", 1)) {
                    canMove = nbt.q("HumanCanMove");
                }
                if(nbt.b("HumanAI", 3)) {
                    ai = nbt.h("HumanAI");
                    setAI(ai);
                }
            }

            private void updatePosition() {
                Location l = getBukkitEntity().getLocation();
                player.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            }

            public void setSkinWithoutPacket(String texture, String signature) {
                GameProfile gp = player.fp();
                gp.getProperties().clear();
                gp.getProperties().put("textures", new Property("textures", texture, signature));
            }

            private void setTabName() {
                player.listName =
                        new ChatComponentText(humanPrefix + player.fp().getName() + humanSuffix);
            }

            private void sync() {
                updatePosition();
                setTabName();
                PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(
                        PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, player);
                PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);
                copyMeta();
                activateSkinOverlays();
                PacketPlayOutEntityMetadata meta =
                        new PacketPlayOutEntityMetadata(player.ae(), player.ai(), true);

                ArrayList<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list =
                        new ArrayList<>();
                for(EnumItemSlot slot : EnumItemSlot.values()) {
                    list.add(new Pair<>(slot, this.b(slot)));
                }
                PacketPlayOutEntityEquipment equip =
                        new PacketPlayOutEntityEquipment(player.ae(), list);

                var channel = ((WorldServer) t).k();
                channel.a(this, info);
                channel.a(this, spawn);
                channel.a(this, meta);
                channel.a(this, equip);
            }

            public void setSkin(String texture, String signature) {
                setSkinWithoutPacket(texture, signature);
                sync();
            }

            public void setName(String name) {
                setPlayer(name, player.t);
                sync();
            }

            public void setSkin(PlayerProfile profile) {
                GameProfile gp = player.fp();
                gp.getProperties().clear();
                for(ProfileProperty prop : profile.getProperties()) {
                    gp.getProperties().put(prop.getName(),
                            new Property(prop.getName(), prop.getValue(), prop.getSignature()));
                }
                sync();
            }

            @Override
            protected void u() { // registerGoals
                setAI(ai);
            }

            private void stopGoals(PathfinderGoalSelector p) {
                var iter = p.c().iterator();
                while(iter.hasNext()) {
                    var next = iter.next();
                    if(next.g()) { // isRunning
                        next.d(); // stop
                    }
                }
            }

            private void stopGoals() {
                stopGoals(bR);
                stopGoals(bS);
            }

            private void removeGoals(PathfinderGoalSelector p) {
                var iter = p.c().iterator();
                while(iter.hasNext()) {
                    var next = iter.next();
                    if(next.g()) { // isRunning
                        next.d(); // stop
                    }
                    iter.remove();
                }
            }

            public boolean angryAt(EntityLiving liv) {
                return lastAttacker == liv;
            }

            public void setAI(int type) {
                ai = type;
                removeGoals(bR);
                removeGoals(bS);
                switch(type) {
                    case 1:
                        bR.a(4, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0f));
                        bR.a(3, new PathfinderGoalRandomLookaround(this));
                        bR.a(0, new PathfinderGoalMeleeAttack(this, 1.0, false));
                        bR.a(2, new PathfinderGoalRandomStrollLand(this, 1.0));
                        bS.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class,
                                true));
                        break;
                    case 2:
                        bR.a(1, new PathfinderGoalMeleeAttack(this, 1.2, true));
                        bR.a(2, new PathfinderGoalMoveTowardsTarget(this, 1.2, 32.0f));
                        bR.a(4, new PathfinderGoalRandomStrollLand(this, 1.0));
                        bR.a(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0f));
                        bR.a(6, new PathfinderGoalRandomLookaround(this));
                        bS.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
                        bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class,
                                10, true, false, this::angryAt));
                        bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this,
                                EntityInsentient.class, 5, false, false, (liv) -> {
                                    return liv instanceof IMonster
                                            && !(liv instanceof EntityCreeper);
                                }));
                        break;
                    default:
                        bR.a(0, new PathfinderGoalFloat(this));
                        bR.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0f));
                        bR.a(2, new PathfinderGoalRandomLookaround(this));
                }
            }

            @Override
            public void g(Vec3D velocity) { // setDeltaMovement
                if(canMove) {
                    super.g(velocity);
                    return;
                }
                super.g(new Vec3D(0.0, velocity.c, 0.0));
            }

            @Override
            public void n(double x, double y, double z) { // setDeltaMovement
                if(canMove) {
                    super.n(x, y, z);
                    return;
                }
                super.n(0.0, y, 0.0);
            }

            public void canMove(boolean b) {
                canMove = b;
            }

            public int moveTo(double x, double y, double z) {
                Goal g = new Goal(x, y, z);
                goals.add(g);
                return g.id;
            }
        }

        public final WrapperHuman human;

        private RawHuman(WrapperHuman human) {
            super(getCraftServer(), human);
            this.human = human;
            setRemoveWhenFarAway(false);
        }

        private RawHuman(WrapperHuman human, Location l, String name) {
            this(human);
            human.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
            human.setPlayer(name, human.t);
            human.W().b(human);
        }

        public RawHuman(Location l, String name) {
            this(HUMAN_TYPE.a(map(l.getWorld())), l, name);
        }

        @Override
        public void setSkin(String texture, String signature) {
            human.setSkin(texture, signature);
        }

        @Override
        public void setName(String name) {
            human.setName(name);
        }

        @Override
        public void setSkin(PlayerProfile profile) {
            human.setSkin(profile);
        }

        @Override
        public void canMove(boolean b) {
            human.canMove(b);
        }

        @Override
        public void setAI(int type) {
            human.setAI(type);
        }

        @Override
        public int moveTo(double x, double y, double z) {
            return human.moveTo(x, y, z);
        }

        @Override
        public int getAI() {
            return human.ai;
        }

        @Override
        public String getName() {
            return human.player.fp().getName();
        }
    }

    public static Human createHuman(String name, Location l) {
        return new RawHuman(l, name);
    }

    private static class PluginConnection extends PlayerConnection {
        public PluginConnection(PlayerConnection c) {
            super(getCraftServer().getServer(), c.a, c.b);
        }

        @SuppressWarnings("deprecation")
        private RawHuman.WrapperHuman getById(int id) {
            net.minecraft.world.entity.Entity ent = b.x().b(id);
            if(ent instanceof RawHuman.WrapperHuman) {
                return (RawHuman.WrapperHuman) ent;
            }
            return null;
        }

        private boolean handle(PacketPlayOutSpawnEntityLiving p) {
            if(p.d() != IRegistry.Z.a(HUMAN_TYPE)) {
                return false;
            }
            UUID uuid = p.c();
            Entity ent = Bukkit.getEntity(uuid);
            if(!(ent instanceof RawHuman)) {
                return false;
            }
            RawHuman raw = (RawHuman) ent;
            EntityPlayer player = raw.human.update(p);
            PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, player);
            PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(player);
            super.a(info);
            super.a(spawn);
            return true;
        }

        private boolean handle(PacketPlayOutEntityMetadata p) {
            RawHuman.WrapperHuman h = getById(p.c());
            if(h == null) {
                return false;
            }
            super.a(h.update(p));
            return true;
        }

        private void handle(PacketPlayOutEntityDestroy p) {
            for(int id : p.b()) {
                RawHuman.WrapperHuman human = getById(id);
                if(human != null) {
                    PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(
                            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, human.player);
                    super.a(remove);
                }
            }
        }

        @Override
        public void a(Packet<?> p) {
            if(p instanceof PacketPlayOutSpawnEntityLiving
                    && handle((PacketPlayOutSpawnEntityLiving) p)) {
                return;
            } else if(p instanceof PacketPlayOutEntityMetadata
                    && handle((PacketPlayOutEntityMetadata) p)) {
                return;
            } else if(p instanceof PacketPlayOutEntityDestroy) {
                handle((PacketPlayOutEntityDestroy) p);
            }
            super.a(p);
        }
    }

    public static void patch(Player p) {
        map(p).b = new PluginConnection(map(p).b);
    }

    public static DamageSource getCurrentDamageSource() {
        return CraftEventFactory.currentDamageCause;
    }

    public static Entity getImmediateSource(DamageSource ds) {
        if(ds.k() == null) {
            return null;
        }
        return map(ds.k());
    }

    public static Entity getTrueSource(DamageSource ds) {
        if(ds.l() == null) {
            return null;
        }
        return map(ds.l());
    }

    public static EntityPlayer map(Player p) {
        return ((CraftPlayer) p).getHandle();
    }

    public static net.minecraft.world.item.ItemStack map(ItemStack stack) {
        return CraftItemStack.asNMSCopy(stack);
    }

    public static net.minecraft.world.entity.Entity map(Entity e) {
        return ((CraftEntity) e).getHandle();
    }

    public static Entity map(net.minecraft.world.entity.Entity e) {
        return e.getBukkitEntity();
    }

    public static EntityLiving map(LivingEntity e) {
        return ((CraftLivingEntity) e).getHandle();
    }

    public static EntityArrow map(Arrow e) {
        return ((CraftArrow) e).getHandle();
    }

    public static EntityFireworks map(Firework e) {
        return ((CraftFirework) e).getHandle();
    }

    public static EntityFireballFireball map(LargeFireball e) {
        return ((CraftLargeFireball) e).getHandle();
    }

    public static EntityWitherSkull map(WitherSkull e) {
        return ((CraftWitherSkull) e).getHandle();
    }

    public static EntityItem map(Item e) {
        return (EntityItem) ((CraftItem) e).getHandle();
    }

    public static WorldServer map(World e) {
        return ((CraftWorld) e).getHandle();
    }

    public static GameProfile map(PlayerProfile profile) {
        return ((CraftPlayerProfile) profile).getGameProfile();
    }

    public static IBlockData map(Block b) {
        return ((CraftBlock) b).getNMS();
    }

    public static DamageSource toDamageSource(Object o) {
        return (DamageSource) o;
    }

    public static DamageSource parseDamageSource(String name) {
        if(DAMAGE_SOURCES.isEmpty()) {
            for(Field f : DamageSource.class.getFields()) {
                if(f.getType() == DamageSource.class) {
                    try {
                        DamageSource ds = (DamageSource) f.get(null);
                        DAMAGE_SOURCES.put(ds.u(), ds);
                    } catch(Exception ex) {
                    }
                }
            }
        }
        return DAMAGE_SOURCES.get(name);
    }

    public static DamageSource sting(LivingEntity liv) {
        return DamageSource.b(map(liv));
    }

    public static DamageSource mobAttack(LivingEntity liv) {
        return DamageSource.c(map(liv));
    }

    public static DamageSource mobIndirect(Entity ent, LivingEntity liv) {
        return DamageSource.a(map(ent), map(liv));
    }

    public static DamageSource playerAttack(Player p) {
        return DamageSource.a(map(p));
    }

    public static DamageSource arrow(Arrow arrow, Entity ent) {
        return DamageSource.a(map(arrow), map(ent));
    }

    public static DamageSource trident(Entity ent, Entity ent2) {
        return DamageSource.a(map(ent), map(ent2));
    }

    public static DamageSource firework(Firework firework, Entity ent) {
        return DamageSource.a(map(firework), map(ent));
    }

    public static DamageSource fireball(LargeFireball fireball, Entity ent) {
        return DamageSource.a(map(fireball), map(ent));
    }

    public static DamageSource witherSkull(WitherSkull witherSkull, Entity ent) {
        return DamageSource.a(map(witherSkull), map(ent));
    }

    public static DamageSource projectile(Entity ent, Entity ent2) {
        return DamageSource.a(map(ent), map(ent2));
    }

    public static DamageSource indirectMagic(Entity ent, Entity ent2) {
        return DamageSource.c(map(ent), map(ent2));
    }

    public static DamageSource thorns(Entity ent) {
        return DamageSource.a(map(ent));
    }

    public static DamageSource explosion(LivingEntity liv) {
        return DamageSource.d(map(liv));
    }

    public static DamageSource explosionBed() {
        return DamageSource.a();
    }

    public static void setMessageOfTheDay(String msg) {
        ((CraftServer) Bukkit.getServer()).getServer().e(msg);
    }

    public static String toString(ItemStack stack) {
        NBTTagCompound c = new NBTTagCompound();
        map(stack).b(c);
        return c.toString();
    }

    private static NBTTagCompound parse(String s) throws Exception {
        return MojangsonParser.a(s);
    }

    public static ItemStack parseItemStack(String stack) {
        try {
            return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(parse(stack)));
        } catch(Exception ex) {
            return null;
        }
    }

    public static String toString(Entity ent) {
        NBTTagCompound c = new NBTTagCompound();
        map(ent).d(c);
        return c.toString();
    }

    public static Entity parseEntity(String stack, Location l) {
        try {
            NBTTagCompound c = parse(stack);
            var nmsWorld = map(l.getWorld());
            net.minecraft.world.entity.Entity ent =
                    net.minecraft.world.entity.EntityTypes.a(c, nmsWorld, e -> {
                        e.a_(UUID.randomUUID());
                        e.a(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                        return nmsWorld.b(e) ? e : null;
                    });
            return map(ent);
        } catch(Exception ex) {
            return null;
        }
    }

    private static BlockPosition convert(Location l) {
        return new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public static void copyTileEntity(Location from, Location to) {
        var nmsFromWorld = map(from.getWorld());
        var nmsToWorld = map(to.getWorld());

        BlockPosition posTo = convert(to);
        TileEntity fromEntity = nmsFromWorld.c_(convert(from));
        TileEntity toEntity = nmsToWorld.c_(posTo);
        if(fromEntity != null && toEntity != null && fromEntity.getClass() == toEntity.getClass()) {
            NBTTagCompound nbtTagCompound = fromEntity.m();
            toEntity.a(nbtTagCompound);
            Block b = to.getBlock();
            nmsToWorld.a(posTo, map(b), map(b), 3);
        }
    }

    public static NBTTagCompound getBlockEntity(String s) {
        try {
            return parse(s);
        } catch(Exception ex) {
            return null;
        }
    }

    public static NBTTagCompound getEntity(Block b) {
        Location l = b.getLocation();
        var nmsWorld = map(l.getWorld());
        TileEntity te = nmsWorld.c_(convert(l));
        if(te == null) {
            return null;
        }
        return te.o();
    }

    public static void setEntity(Block b, NBTTagCompound nbt) {
        if(nbt == null) {
            return;
        }
        Location l = b.getLocation();
        var nmsWorld = map(l.getWorld());
        BlockPosition pos = convert(l);
        TileEntity te = nmsWorld.c_(pos);
        if(te != null) {
            te.a(nbt);
            nmsWorld.a(pos, map(b), map(b), 3);
        }
    }

    public static NBTTagCompound toNBT(Object o) {
        return (NBTTagCompound) o;
    }

    public static void resetSleepTimer(Player p) {
        // this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        map(p).a(StatisticList.i.b(StatisticList.n));
    }
}
