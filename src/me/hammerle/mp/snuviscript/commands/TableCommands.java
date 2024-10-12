package me.hammerle.mp.snuviscript.commands;

import me.hammerle.mp.MundusPlugin;
import me.hammerle.mp.utils.Table;

public class TableCommands {
    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("table.new", (sc, in) -> {
            int[] widths = new int[in.length - 1];
            for(int i = 0; i < widths.length; i++) {
                widths[i] = in[i + 1].getInt(sc);
            }
            return new Table(in[0].getString(sc), widths);
        });
        MundusPlugin.scriptManager.registerFunction("table.getstart", (sc, in) -> {
            return ((Table) in[0].get(sc)).getStart();
        });
        MundusPlugin.scriptManager.registerFunction("table.getmiddle", (sc, in) -> {
            return ((Table) in[0].get(sc)).getMiddle();
        });
        MundusPlugin.scriptManager.registerFunction("table.getend", (sc, in) -> {
            return ((Table) in[0].get(sc)).getEnd();
        });
        MundusPlugin.scriptManager.registerFunction("table.get", (sc, in) -> {
            String[] columns = new String[in.length - 1];
            for(int i = 0; i < columns.length; i++) {
                columns[i] = in[i + 1].getString(sc);
            }
            return ((Table) in[0].get(sc)).get(columns);
        });
        MundusPlugin.scriptManager.registerConsumer("table.setsize", (sc, in) -> {
            Table.addSizeMapping(in[0].getString(sc).charAt(0), in[1].getInt(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("table.setempty4", (sc, in) -> {
            Table.empty4 = in[0].getString(sc);
        });
        MundusPlugin.scriptManager.registerConsumer("table.setempty2", (sc, in) -> {
            Table.empty2 = in[0].getString(sc);
        });
        MundusPlugin.scriptManager.registerConsumer("table.setempty1", (sc, in) -> {
            Table.empty1 = in[0].getString(sc);
        });
    }
}
