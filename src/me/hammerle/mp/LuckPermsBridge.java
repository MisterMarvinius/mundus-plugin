package me.hammerle.mp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsBridge {
    private static final String LUCKPERMS_CLASS = "net.luckperms.api.LuckPerms";
    private static final String NODE_CLASS = "net.luckperms.api.node.Node";
    private static final String PERMISSION_NODE_CLASS = "net.luckperms.api.node.types.PermissionNode";

    public static boolean addTransientPermission(Player player, String permission, boolean value) {
        Object user = getUser(player);
        if(user == null) {
            return false;
        }
        Object data = invoke(user, "transientData");
        Object node = buildPermissionNode(permission, value);
        if(data == null || node == null) {
            return false;
        }
        Object nodeClass = loadClass(NODE_CLASS);
        if(nodeClass == null) {
            return false;
        }
        invoke(data, "add", new Class<?>[] {(Class<?>) nodeClass}, new Object[] {node});
        return true;
    }

    public static boolean removeTransientPermission(Player player, String permission, boolean value) {
        Object user = getUser(player);
        if(user == null) {
            return false;
        }
        Object data = invoke(user, "transientData");
        Object node = buildPermissionNode(permission, value);
        if(data == null || node == null) {
            return false;
        }
        Object nodeClass = loadClass(NODE_CLASS);
        if(nodeClass == null) {
            return false;
        }
        invoke(data, "remove", new Class<?>[] {(Class<?>) nodeClass}, new Object[] {node});
        return true;
    }

    public static boolean clearTransientPermissions(Player player) {
        Object user = getUser(player);
        if(user == null) {
            return false;
        }
        Object data = invoke(user, "transientData");
        if(data == null) {
            return false;
        }
        invoke(data, "clear");
        return true;
    }

    private static Object getUser(Player player) {
        Object luckPerms = getLuckPerms();
        if(luckPerms == null) {
            return null;
        }
        Object adapter = invoke(luckPerms, "getPlayerAdapter",
                new Class<?>[] {Class.class}, new Object[] {Player.class});
        if(adapter == null) {
            return null;
        }
        return invoke(adapter, "getUser", new Class<?>[] {Player.class}, new Object[] {player});
    }

    private static Object buildPermissionNode(String permission, boolean value) {
        Object permissionNodeClass = loadClass(PERMISSION_NODE_CLASS);
        if(permissionNodeClass == null) {
            return null;
        }
        Object builder = invokeStatic((Class<?>) permissionNodeClass, "builder",
                new Class<?>[] {String.class}, new Object[] {permission});
        if(builder == null) {
            return null;
        }
        Object withValue = invoke(builder, "value", new Class<?>[] {boolean.class},
                new Object[] {value});
        if(withValue == null) {
            return null;
        }
        return invoke(withValue, "build");
    }

    private static Object getLuckPerms() {
        Class<?> luckPermsClass = loadClass(LUCKPERMS_CLASS);
        if(luckPermsClass == null) {
            return null;
        }
        RegisteredServiceProvider<?> provider =
                Bukkit.getServicesManager().getRegistration(luckPermsClass);
        if(provider == null) {
            MundusPlugin.warn("LuckPerms not found. Permission scripts will be ignored.");
            return null;
        }
        return provider.getProvider();
    }

    private static Object loadClass(String name) {
        try {
            return Class.forName(name);
        } catch(ClassNotFoundException e) {
            return null;
        }
    }

    private static Object invoke(Object target, String methodName) {
        return invoke(target, methodName, new Class<?>[0], new Object[0]);
    }

    private static Object invoke(Object target, String methodName,
            Class<?>[] argTypes, Object[] args) {
        try {
            return target.getClass().getMethod(methodName, argTypes).invoke(target, args);
        } catch(Exception e) {
            MundusPlugin.warn("LuckPerms access failed: " + e.getMessage());
            return null;
        }
    }

    private static Object invokeStatic(Class<?> target, String methodName,
            Class<?>[] argTypes, Object[] args) {
        try {
            return target.getMethod(methodName, argTypes).invoke(null, args);
        } catch(Exception e) {
            MundusPlugin.warn("LuckPerms access failed: " + e.getMessage());
            return null;
        }
    }
}
