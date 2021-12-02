package me.hammerle.kp.snuviscript.commands;

import java.util.UUID;
import org.bukkit.entity.Player;

public class CommandUtils {
    private final static UUID SERVER_UUID = new UUID(0, 0);

    public static UUID getUUID(Object o) {
        if(o instanceof Player) {
            return ((Player) o).getUniqueId();
        } else if(o instanceof UUID) {
            return (UUID) o;
        } else if("SERVER".equals(o)) {
            return SERVER_UUID;
        }
        return UUID.fromString(o.toString());
    }

    /*public static ITextComponent concat(Script sc, int start, String pre, InputProvider... ob)
            throws Exception {
        StringTextComponent text = new StringTextComponent(pre);
        Object o;
        for(int i = start; i < ob.length; i++) {
            o = ob[i].get(sc);
            if(o instanceof ITextComponent) {
                text.append((ITextComponent) o);
            } else {
                text.appendString(String.valueOf(o));
            }
        }
        return text;
    }
    
    public static void sendMessageToGroup(MinecraftServer server, Scripts scripts,
            Permissions perms, Object group, Script sc, ITextComponent text) {
        doForGroup(server, scripts, perms, group, sc, p -> p.sendMessage(text, Util.DUMMY_UUID));
    }
    
    public static void doForGroup(MinecraftServer server, Scripts scripts, Permissions perms,
            Object group, Script sc, Consumer<ICommandSource> c) {
        if(group instanceof String) {
            switch(group.toString().toLowerCase()) {
                case "online":
                    if(server.getPlayerList() != null) {
                        server.getPlayerList().getPlayers().forEach(p -> c.accept(p));
                    }
                    return;
                case "dev":
                    if(server.getPlayerList() != null) {
                        server.getPlayerList().getPlayers().stream()
                                .filter(p -> perms.has(p, "script.error")).forEach(c);
                    }
                    return;
                case "server":
                    c.accept(server);
                    return;
            }
        }
        c.accept((PlayerEntity) group);
    }
    
    public static int getId(IPlayerBank bank, Object o) {
        if(o instanceof ModEntityPlayerMP) {
            return ((ModEntityPlayerMP) o).getId();
        } else if(o instanceof Double) {
            return ((Double) o).intValue();
        }
        UUID uuid = getUUID(o);
        return bank.getId(uuid);
    }
    
    public static Class<?> getNamedClass(String s) throws ClassNotFoundException {
        return Class.forName(s);
    }
    
    public static BlockState getBlockState(Location l) {
        return l.getWorld().getBlockState(l.getBlockPos());
    }*/
}
