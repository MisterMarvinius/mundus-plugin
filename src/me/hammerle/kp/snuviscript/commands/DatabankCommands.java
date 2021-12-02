/*package me.km.snuviscript.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.hammerle.snuviscript.code.ScriptManager;
import me.hammerle.snuviscript.exceptions.StackTrace;
import me.km.databank.DataBank;
import me.km.scheduler.SnuviScheduler;

public class DatabankCommands {
    public static void registerFunctions(ScriptManager sm, SnuviScheduler scheduler,
            DataBank dataBank) {
        sm.registerFunction("databank.prepare", (sc, in) -> {
            PreparedStatement p = dataBank.prepareUnsafeStatement(in[0].getString(sc));
            if(in.length <= 1 || in[1].getBoolean(sc)) {
                sc.addCloseable(p);
            }
            return p;
        });
        sm.registerConsumer("databank.setint", (sc, in) -> {
            ((PreparedStatement) in[0].get(sc)).setInt(in[1].getInt(sc), in[2].getInt(sc));
        });
        sm.registerConsumer("databank.setlong", (sc, in) -> {
            ((PreparedStatement) in[0].get(sc)).setLong(in[1].getInt(sc), in[2].getLong(sc));
        });
        sm.registerConsumer("databank.setdouble", (sc, in) -> {
            ((PreparedStatement) in[0].get(sc)).setDouble(in[1].getInt(sc), in[2].getDouble(sc));
        });
        sm.registerConsumer("databank.setstring", (sc, in) -> {
            ((PreparedStatement) in[0].get(sc)).setString(in[1].getInt(sc), in[2].getString(sc));
        });
        sm.registerConsumer("databank.setbool", (sc, in) -> {
            ((PreparedStatement) in[0].get(sc)).setBoolean(in[1].getInt(sc), in[2].getBoolean(sc));
        });
        sm.registerFunction("databank.getint",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getInt(in[1].getInt(sc)));
        sm.registerFunction("databank.getlong",
                (sc, in) -> (double) ((ResultSet) in[0].get(sc)).getLong(in[1].getInt(sc)));
        sm.registerFunction("databank.getdouble",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getDouble(in[1].getInt(sc)));
        sm.registerFunction("databank.getstring",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getString(in[1].getInt(sc)));
        sm.registerFunction("databank.getbool",
                (sc, in) -> ((ResultSet) in[0].get(sc)).getBoolean(in[1].getInt(sc)));
        sm.registerFunction("databank.execute",
                (sc, in) -> ((PreparedStatement) in[0].get(sc)).executeQuery());
        sm.registerConsumer("databank.workerexecute", (sc, in) -> {
            final PreparedStatement p = (PreparedStatement) in[0].get(sc);
            StackTrace lines = sc.getStackTrace();
            String function = "databank.workerexecute";
            scheduler.scheduleAsyncTask(() -> {
                try {
                    p.execute();
                } catch(SQLException ex) {
                    scheduler.scheduleTask("worker execute 1", () -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
                sc.removeCloseable(p);
                try {
                    p.close();
                } catch(SQLException ex) {
                    scheduler.scheduleTask("worker execute 2", () -> {
                        sc.getScriptManager().getLogger().print("Worker error", ex, function,
                                sc.getName(), sc, lines);
                    });
                }
            });
        });
        sm.registerFunction("databank.next", (sc, in) -> ((ResultSet) in[0].get(sc)).next());
        sm.registerConsumer("databank.close", (sc, in) -> {
            AutoCloseable auto = (AutoCloseable) in[0].get(sc);
            auto.close();
            sc.removeCloseable(auto);
        });
        sm.registerFunction("worker.haswork", (sc, in) -> scheduler.hasAsyncWork());
    }
}
*/
