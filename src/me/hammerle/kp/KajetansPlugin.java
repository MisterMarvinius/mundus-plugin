package me.hammerle.kp;

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
import me.hammerle.kp.snuviscript.SnuviLogger;
import me.hammerle.kp.snuviscript.commands.*;
import me.hammerle.kp.plots.WorldPlotMap;
import me.hammerle.kp.snuviscript.CommandScript;
import me.hammerle.kp.snuviscript.CommandManager;
import me.hammerle.kp.snuviscript.MoveEvents;
import me.hammerle.kp.snuviscript.ScriptEvents;
import me.hammerle.snuviscript.config.SnuviConfig;
import me.hammerle.snuviscript.code.ISnuviScheduler;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.code.Script;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachment;

public class KajetansPlugin extends JavaPlugin implements ISnuviScheduler {
    private static PrivateKey privateKey = null;
    private static volatile boolean runningVotifier = true;
    private static ServerSocket votifierSocket = null;
    private static Thread votifierAccept = null;

    public static KajetansPlugin instance;
    public static SnuviLogger logger;
    public static ScriptManager scriptManager;

    private boolean enabled = false;
    private boolean debug = false;

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

    public static Script startScript(String name, String... names) {
        Arrays.setAll(names, i -> "scripts/" + names[i] + ".txt");
        return scriptManager.startScript(sc -> MoveEvents.remove(sc),
                name == null ? names[0] : name, names);
    }

    public static boolean isInDebug() {
        return instance.debug;
    }

    private static void registerFunctions() {
        CommandCommands.registerFunctions();
        PermissionCommands.registerFunctions();
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
        HumanCommands.registerFunctions();
        DatabaseCommands.registerFunctions();
        PlotCommands.registerFunctions();
        ScriptCommands.registerFunctions();
        ScoreboardCommands.registerFunctions();
        ParticleCommands.registerFunctions();
        SoundCommands.registerFunctions();
        InventoryCommands.registerFunctions();
        DataCommands.registerFunctions();
        ReadCommands.registerFunctions();
        TextCommands.registerFunctions();
        BanCommands.registerFunctions();
        ShopCommands.registerFunctions();
        ErrorCommands.registerFunctions();
        EnchantmentCommands.registerFunctions();
        ItemEntityCommands.registerFunctions();
        BossBarCommands.registerFunctions();
        Commands.registerFunctions();
    }

    public static PermissionAttachment addPermissions(Permissible p) {
        return p.addAttachment(instance);
    }

    @Override
    public void onEnable() {
        instance = this;
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

        CommandManager.add(new CommandScript());

        if(!Database.connect(conf.getString(null, "user", "root"),
                conf.getString(null, "password", ""))) {
            warn("Starting server without database");
        }

        scheduleTask(() -> WorldPlotMap.read());
        long time = 20 * 60 * 30; // all 30 minutes
        scheduleRepeatingTask(() -> WorldPlotMap.save(), time, time);

        getServer().getPluginManager().registerEvents(new Events(), instance);

        registerFunctions();
        scheduleTask(() -> startScript(null, "startscript"));
        startVotifier(conf.getString(null, "pkey", ""));
    }

    @Override
    public void onDisable() {
        enabled = false;
        startScript("endscript");
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
