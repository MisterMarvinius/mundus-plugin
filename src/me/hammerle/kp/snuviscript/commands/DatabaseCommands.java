package me.hammerle.kp.snuviscript.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.hammerle.kp.Database;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.snuviscript.exceptions.StackTrace;

public class DatabaseCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("databank.prepare", (sc, in) -> {
            Database.SafeStatement p = Database.prepare(in[0].getString(sc));
            if(in.length <= 1 || in[1].getBoolean(sc)) {
                sc.addCloseable(p);
            }
            return p;
        });
        KajetansPlugin.scriptManager.registerConsumer("databank.setint", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setInt(in[1].getInt(sc), in[2].getInt(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("databank.setlong", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setLong(in[1].getInt(sc), in[2].getLong(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("databank.setdouble", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setDouble(in[1].getInt(sc),
                    in[2].getDouble(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("databank.setstring", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setString(in[1].getInt(sc),
                    in[2].getString(sc));
        });
        KajetansPlugin.scriptManager.registerConsumer("databank.setbool", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setBoolean(in[1].getInt(sc),
                    in[2].getBoolean(sc));
        });
        KajetansPlugin.scriptManager.registerFunction("databank.getint",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getInt(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("databank.getlong",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getLong(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("databank.getdouble",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getDouble(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("databank.getstring",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getString(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("databank.getbool",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getBoolean(in[1].getInt(sc)));
        KajetansPlugin.scriptManager.registerFunction("databank.execute",
                (sc, in) -> ((Database.SafeStatement) in[0].get(sc)).executeQuery());
        KajetansPlugin.scriptManager.registerConsumer("databank.workerexecute", (sc, in) -> {
            final Database.SafeStatement p = (Database.SafeStatement) in[0].get(sc);
            StackTrace lines = sc.getStackTrace();
            String function = "databank.workerexecute";
            KajetansPlugin.scheduleAsyncTask(() -> {
                try {
                    p.execute();
                } catch(SQLException ex) {
                    KajetansPlugin.scheduleTask(() -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
                sc.removeCloseable(p);
                try {
                    p.close();
                } catch(SQLException ex) {
                    KajetansPlugin.scheduleTask(() -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
            });
        });
        KajetansPlugin.scriptManager.registerFunction("databank.next",
                (sc, in) -> ((ResultSet) in[0].get(sc)).next());
        KajetansPlugin.scriptManager.registerConsumer("databank.close", (sc, in) -> {
            AutoCloseable auto = (AutoCloseable) in[0].get(sc);
            auto.close();
            sc.removeCloseable(auto);
        });
    }
}
