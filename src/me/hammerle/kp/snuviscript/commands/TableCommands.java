package me.hammerle.kp.snuviscript.commands;

import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.utils.Table;

public class TableCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("table.new", (sc, in) -> {
            int[] widths = new int[in.length - 1];
            for(int i = 0; i < widths.length; i++) {
                widths[i] = in[i + 1].getInt(sc);
            }
            return new Table(in[0].getString(sc), widths);
        });
        KajetansPlugin.scriptManager.registerFunction("table.getstart", (sc, in) -> {
            return ((Table) in[0].get(sc)).getStart();
        });
        KajetansPlugin.scriptManager.registerFunction("table.getmiddle", (sc, in) -> {
            return ((Table) in[0].get(sc)).getMiddle();
        });
        KajetansPlugin.scriptManager.registerFunction("table.getend", (sc, in) -> {
            return ((Table) in[0].get(sc)).getEnd();
        });
        KajetansPlugin.scriptManager.registerFunction("table.get", (sc, in) -> {
            String[] columns = new String[in.length - 1];
            for(int i = 0; i < columns.length; i++) {
                columns[i] = in[i + 1].getString(sc);
            }
            return ((Table) in[0].get(sc)).get(columns);
        });
        KajetansPlugin.scriptManager.registerConsumer("table.setsize", (sc, in) -> {
            Table.addSizeMapping(in[0].getString(sc).charAt(0), in[1].getInt(sc));
        });
    }
}
