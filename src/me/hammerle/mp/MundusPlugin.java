package me.hammerle.mp;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import me.hammerle.mp.snuviscript.SnuviLogger;
import me.hammerle.mp.snuviscript.commands.*;
import me.hammerle.mp.plots.WorldPlotMap;
import me.hammerle.mp.snuviscript.CommandScript;
import me.hammerle.mp.snuviscript.CommandTest;
import me.hammerle.mp.snuviscript.CommandManager;
import me.hammerle.mp.snuviscript.MoveEvents;
import me.hammerle.mp.snuviscript.ScriptEvents;
import me.hammerle.mp.snuviscript.ScriptTypeRegistration;
import me.hammerle.snuviscript.config.SnuviConfig;
import me.hammerle.snuviscript.code.ISnuviScheduler;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.Script;

public class MundusPlugin extends JavaPlugin implements ISnuviScheduler {
    private static PrivateKey privateKey = null;
    private static volatile boolean runningVotifier = true;
    private static ServerSocket votifierSocket = null;
    private static Thread votifierAccept = null;

    public static MundusPlugin instance;
    public static SnuviLogger logger;
    public static ScriptManager scriptManager;
    public static Thread isAliveThread = null;

    private boolean enabled = false;
    private boolean debug = false;

    public MundusPlugin() throws ClassNotFoundException {
        instance = this;
    }

    public static void test() {
        System.out.println("HOHO");
    }

    public static void log(String msg) {
        instance.getLogger().info(msg);
    }

    public static void warn(String msg) {
        instance.getLogger().warning(msg);
    }

    @Override
    public void scheduleTask(String name, Runnable r, long delay) {
        if(enabled) {
            Bukkit.getScheduler().runTaskLater(instance, r, delay);
        }
    }

    public static void scheduleTask(Runnable r, long delay) {
        instance.scheduleTask(null, r, delay);
    }

    public static void scheduleTask(Runnable r) {
        scheduleTask(r, 1);
    }

    public static void scheduleRepeatingTask(Runnable r, long ticks, long repeatTicks) {
        if(instance.enabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, r, ticks, repeatTicks);
        }
    }

    public static void scheduleAsyncTask(Runnable r) {
        if(instance.enabled) {
            Bukkit.getScheduler().runTaskAsynchronously(instance, r);
        }
    }

    public static Script startLocalScript(String name, String... names) {
        return startScript("scripts/", name, names);
    }

    public static Script startGlobalScript(String name, String... names) {
        return startScript("../scripts/", name, names);
    }

    public static Script startScript(String path, String name, String... names) {
        Arrays.setAll(names, i -> path + names[i] + ".snuvi");
        return scriptManager.startScript(sc -> MoveEvents.remove(sc),
                name == null ? names[0] : name, names);
    }

    public static boolean isInDebug() {
        return instance.debug;
    }

    private static void registerFunctions() {
        CommandCommands.registerFunctions();
        TableCommands.registerFunctions();
        TitleCommands.registerFunctions();
        PlayerCommands.registerFunctions();
        WorldCommands.registerFunctions();
        GameRuleCommands.registerFunctions();
        ItemCommands.registerFunctions();
        LocationCommands.registerFunctions();
        BlockCommands.registerFunctions();
        EventCommands.registerFunctions();
        DamageCommands.registerFunctions();
        EntityCommands.registerFunctions();
        LivingCommands.registerFunctions();
        ArmorStandCommands.registerFunctions();
        CitizenCommands.registerFunctions();
        DatabaseCommands.registerFunctions();
        PlotCommands.registerFunctions();
        WorldGuardCommands.registerFunctions();
        ScriptCommands.registerFunctions();
        ScoreboardCommands.registerFunctions();
        ParticleCommands.registerFunctions();
        SoundCommands.registerFunctions();
        InventoryCommands.registerFunctions();
        DataCommands.registerFunctions();
        ReadCommands.registerFunctions();
        TextCommands.registerFunctions();
        DisplayCommands.registerFunctions();
        BanCommands.registerFunctions();
        ErrorCommands.registerFunctions();
        EnchantmentCommands.registerFunctions();
        ItemEntityCommands.registerFunctions();
        BossBarCommands.registerFunctions();
        Commands.registerFunctions();
        LuckPermsCommands.registerFunctions();
        SkillsCommands.registerFunctions();
    }

    @Override
    public void onLoad() {
        WorldGuardCommands.registerFlagsOnLoad();
    }

    @Override
    public void onEnable() {
        enabled = true;
        log("§40.0.1");
        logger = new SnuviLogger();

        SnuviConfig conf = new SnuviConfig("", "config");
        if(conf.exists()) {
            conf.load(null);
        }

        debug = conf.getBoolean(null, "debug", false);
        if(debug) {
            warn("Starting server in debug mode");
        }

        scriptManager = new ScriptManager(logger, this);
        ScriptTypeRegistration.registerMinecraftTypes();

        CommandManager.add(new CommandScript());
        CommandManager.add(new CommandTest());

        if(!Database.connect(conf.getString(null, "user", "root"),
                conf.getString(null, "password", ""))) {
            warn("Starting server without database");
        }

        scheduleTask(() -> WorldPlotMap.read());

        getServer().getPluginManager().registerEvents(new Events(), instance);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        registerFunctions();
        scheduleTask(() -> startLocalScript(null, "startscript"));
        startVotifier(conf.getString(null, "pkey", ""));

        if(isAliveThread == null) {
            isAliveThread = new Thread(() -> {
                int deadLoop = 0;
                while(true) {
                    try {
                        Thread.sleep(1000);
                    } catch(Exception ex) {
                    }
                    if(Bukkit.isStopping()) {
                        warn("stopping ... (" + deadLoop + ")");
                        deadLoop++;
                        if(deadLoop > 120) {
                            warn("Server seems dead on stop");
                            Runtime.getRuntime().halt(1);
                            return;
                        }
                    }
                }
            });
            isAliveThread.start();
        }
    }

    @Override
    public void onDisable() {
        warn("DISABLE");
        enabled = false;
        startLocalScript("endscript");
        for(BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
            if(task instanceof Runnable) {
                ((Runnable) task).run();
            }
        }
        Database.close();
        try {
            if(votifierSocket != null) {
                votifierSocket.close();
                votifierAccept.join(1000);
            }
        } catch(Exception ex) {
            warn(ex.getMessage());
        }
    }

    private static PrivateKey getPrivateKey(String base64) {
        try {
            PKCS8EncodedKeySpec keySpec =
                    new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64.getBytes()));;
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch(Exception e) {
            warn(e.getMessage());
        }
        return null;
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    private static void startVotifier(String pKey) {
        privateKey = getPrivateKey(pKey);
        if(privateKey == null) {
            return;
        }
        try {
            votifierSocket = new ServerSocket(12345);
            votifierAccept = new Thread(() -> {
                while(runningVotifier) {
                    try {
                        Socket client = votifierSocket.accept();
                        InputStream in = client.getInputStream();
                        byte[] data = new byte[256];
                        for(int i = 0; i < 256; i++) {
                            data[i] = (byte) in.read();
                        }
                        String[] s = decrypt(data, privateKey).split("\n");
                        scheduleTask(() -> {
                            ScriptEvents.onVote(s);
                        });
                    } catch(Exception ex) {
                        if(!votifierSocket.isClosed()) {
                            log(ex.getMessage());
                        }
                    }
                }
            });
            votifierAccept.start();
            log("votifier started");
        } catch(Exception ex) {
            warn("cannot start votifier socket");
            warn(ex.getMessage());
        }
    }
}
