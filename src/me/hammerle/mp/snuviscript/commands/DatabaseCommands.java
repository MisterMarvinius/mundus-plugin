package me.hammerle.mp.snuviscript.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.hammerle.mp.Database;
import me.hammerle.mp.MundusPlugin;
import me.hammerle.snuviscript.exceptions.StackTrace;

public class DatabaseCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("databank.prepare", (sc, in) -> {
            Database.SafeStatement p = Database.prepare(in[0].getString(sc));
            if(in.length <= 1 || in[1].getBoolean(sc)) {
                sc.addCloseable(p);
            }
            return p;
        });
        MundusPlugin.scriptManager.registerConsumer("databank.setint", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setInt(in[1].getInt(sc), in[2].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("databank.setlong", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setLong(in[1].getInt(sc), in[2].getLong(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("databank.setdouble", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setDouble(in[1].getInt(sc),
                    in[2].getDouble(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("databank.setstring", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setString(in[1].getInt(sc),
                    in[2].getString(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("databank.setbool", (sc, in) -> {
            ((Database.SafeStatement) in[0].get(sc)).setBoolean(in[1].getInt(sc),
                    in[2].getBoolean(sc));
        });
        MundusPlugin.scriptManager.registerFunction("databank.getint",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getInt(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("databank.getlong",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getLong(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("databank.getdouble",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getDouble(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("databank.getstring",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getString(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("databank.getbool",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getBoolean(in[1].getInt(sc)));
        MundusPlugin.scriptManager.registerFunction("databank.execute",
                (sc, in) -> ((Database.SafeStatement) in[0].get(sc)).executeQuery());
        MundusPlugin.scriptManager.registerConsumer("databank.workerexecute", (sc, in) -> {
            final Database.SafeStatement p = (Database.SafeStatement) in[0].get(sc);
            StackTrace lines = sc.getStackTrace();
            String function = "databank.workerexecute";
            MundusPlugin.scheduleAsyncTask(() -> {
                try {
                    p.execute();
                } catch(SQLException ex) {
                    MundusPlugin.scheduleTask(() -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
                sc.removeCloseable(p);
                try {
                    p.close();
                } catch(SQLException ex) {
                    MundusPlugin.scheduleTask(() -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
            });
        });
        MundusPlugin.scriptManager.registerFunction("databank.next",
                (sc, in) -> ((ResultSet) in[0].get(sc)).next());
        MundusPlugin.scriptManager.registerConsumer("databank.close", (sc, in) -> {
            AutoCloseable auto = (AutoCloseable) in[0].get(sc);
            auto.close();
            sc.removeCloseable(auto);
        });
    }
}
