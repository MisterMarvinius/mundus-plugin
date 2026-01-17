package me.hammerle.mp.snuviscript.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import me.hammerle.mp.MundusPlugin;
import net.kyori.adventure.text.Component;

public class DialogCommands {
    private static final String[] DIALOG_CLASSES = {
            "io.papermc.paper.dialog.Dialog",
            "net.kyori.adventure.dialog.Dialog"
    };
    private static final String[] BUTTON_CLASSES = {
            "io.papermc.paper.dialog.DialogButton",
            "net.kyori.adventure.dialog.DialogButton"
    };
    private static final String[] ACTION_CLASSES = {
            "io.papermc.paper.dialog.DialogAction",
            "io.papermc.paper.dialog.DialogButtonAction",
            "net.kyori.adventure.dialog.DialogAction",
            "net.kyori.adventure.dialog.DialogButtonAction"
    };

    public static void registerFunctions() {
        MundusPlugin.scriptManager.registerFunction("dialog.new",
                (sc, in) -> new DialogSpec((Component) in[0].get(sc),
                        (Component) in[1].get(sc)));
        MundusPlugin.scriptManager.registerConsumer("dialog.settitle", (sc, in) -> {
            ((DialogSpec) in[0].get(sc)).setTitle((Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("dialog.setbody", (sc, in) -> {
            ((DialogSpec) in[0].get(sc)).setBody((Component) in[1].get(sc));
        });
        MundusPlugin.scriptManager.registerConsumer("dialog.addbutton", (sc, in) -> {
            DialogSpec dialog = (DialogSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String actionType = in[2].getString(sc);
            String actionValue = in[3].getString(sc);
            dialog.addButton(new ButtonSpec(label, actionType, actionValue));
        });
        MundusPlugin.scriptManager.registerConsumer("dialog.show", (sc, in) -> {
            Object dialog = in[0].get(sc);
            Player player = (Player) in[1].get(sc);
            showDialog(player, dialog);
        });
    }

    private static void showDialog(Player player, Object dialog) {
        if(dialog == null) {
            return;
        }
        if(dialog instanceof DialogSpec) {
            DialogSpec spec = (DialogSpec) dialog;
            Class<?> dialogClass = dialogClassForPlayer(player);
            Object built = spec.buildDialog(dialogClass);
            if(built != null && tryShow(player, built)) {
                return;
            }
            sendFallback(player, spec);
            return;
        }
        if(!tryShow(player, dialog)) {
            player.sendMessage(Component.text(dialog.toString()));
        }
    }

    private static boolean tryShow(Player player, Object dialog) {
        for(Method method : player.getClass().getMethods()) {
            if(!method.getName().equals("showDialog") || method.getParameterCount() != 1) {
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
            if(!param.isInstance(dialog)) {
                continue;
            }
            try {
                method.invoke(player, dialog);
                return true;
            } catch(Exception ex) {
                continue;
            }
        }
        return false;
    }

    private static void sendFallback(Player player, DialogSpec spec) {
        if(spec.title != null) {
            player.sendMessage(spec.title);
        }
        if(spec.body != null) {
            player.sendMessage(spec.body);
        }
        for(ButtonSpec button : spec.buttons) {
            Component line = Component.text("[")
                    .append(button.label == null ? Component.text("?") : button.label)
                    .append(Component.text("] "))
                    .append(Component.text(button.actionType))
                    .append(Component.text(": "))
                    .append(Component.text(button.actionValue));
            player.sendMessage(line);
        }
    }

    private static Class<?> firstClass(String[] names) {
        for(String name : names) {
            try {
                return Class.forName(name);
            } catch(ClassNotFoundException ex) {
            }
        }
        return null;
    }

    private static Class<?> dialogClassForPlayer(Player player) {
        Class<?> fallback = null;
        for(Method method : player.getClass().getMethods()) {
            if(!method.getName().equals("showDialog") || method.getParameterCount() != 1) {
                continue;
            }
            Class<?> param = method.getParameterTypes()[0];
            if(fallback == null) {
                fallback = param;
            }
            if(isDialogClass(param)) {
                return param;
            }
        }
        return fallback;
    }

    private static Class<?> classForPackage(String[] names, String prefix) {
        if(prefix != null) {
            for(String name : names) {
                if(!name.startsWith(prefix)) {
                    continue;
                }
                try {
                    return Class.forName(name);
                } catch(ClassNotFoundException ex) {
                }
            }
        }
        return firstClass(names);
    }

    private static String dialogPackagePrefix(Class<?> dialogClass) {
        if(dialogClass == null) {
            return null;
        }
        String name = dialogClass.getName();
        if(name.startsWith("io.papermc.paper.dialog.")) {
            return "io.papermc.paper.dialog.";
        }
        if(name.startsWith("net.kyori.adventure.dialog.")) {
            return "net.kyori.adventure.dialog.";
        }
        return null;
    }

    private static boolean isDialogClass(Class<?> type) {
        if(type == null) {
            return false;
        }
        String name = type.getName();
        for(String dialogName : DIALOG_CLASSES) {
            if(dialogName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static Object buildAction(String actionType, String actionValue, Class<?> actionClass) {
        if(actionClass == null) {
            return null;
        }
        String normalized = actionType == null ? "" : actionType.toLowerCase();
        if(normalized.isEmpty() || normalized.equals("none")) {
            return invokeStatic(actionClass, "none");
        }
        if(normalized.equals("command") || normalized.equals("run")) {
            return invokeStatic(actionClass, "runCommand", String.class, actionValue);
        }
        if(normalized.equals("suggest")) {
            return invokeStatic(actionClass, "suggestCommand", String.class, actionValue);
        }
        if(normalized.equals("url") || normalized.equals("link")) {
            return invokeStatic(actionClass, "openUrl", String.class, actionValue);
        }
        if(normalized.equals("clipboard") || normalized.equals("copy")) {
            return invokeStatic(actionClass, "copyToClipboard", String.class, actionValue);
        }
        return null;
    }

    private static Object buildButton(ButtonSpec spec, Class<?> buttonClass, Class<?> actionClass) {
        if(buttonClass == null) {
            return null;
        }
        Object action = buildAction(spec.actionType, spec.actionValue, actionClass);
        Object button = null;
        if(action != null) {
            button = invokeStaticAssignable(buttonClass, "of", new Object[] {spec.label, action});
            if(button == null) {
                button = invokeStaticAssignable(buttonClass, "create",
                        new Object[] {spec.label, action});
            }
        }
        if(button == null) {
            button = invokeStatic(buttonClass, "of", Component.class, spec.label);
        }
        if(button == null) {
            button = invokeStatic(buttonClass, "create", Component.class, spec.label);
        }
        if(button == null) {
            Object builder = invokeStatic(buttonClass, "builder", Component.class, spec.label);
            if(builder == null) {
                builder = invokeStatic(buttonClass, "builder");
            }
            if(builder != null) {
                invoke(builder, "label", Component.class, spec.label);
                if(action != null) {
                    invoke(builder, "action", action.getClass(), action);
                }
                button = invoke(builder, "build");
            }
        }
        return button;
    }

    private static Object buildDialog(DialogSpec spec, Class<?> dialogClass) {
        if(dialogClass == null) {
            dialogClass = firstClass(DIALOG_CLASSES);
        }
        if(dialogClass == null) {
            return null;
        }
        String prefix = dialogPackagePrefix(dialogClass);
        Class<?> buttonClass = classForPackage(BUTTON_CLASSES, prefix);
        Class<?> actionClass = classForPackage(ACTION_CLASSES, prefix);
        Object builder = invokeStatic(dialogClass, "builder", Component.class, spec.title);
        if(builder == null) {
            builder = invokeStatic(dialogClass, "builder");
        }
        if(builder != null) {
            invoke(builder, "title", Component.class, spec.title);
            if(!invoke(builder, "body", Component.class, spec.body)) {
                invoke(builder, "content", Component.class, spec.body);
            }
            for(ButtonSpec button : spec.buttons) {
                Object builtButton = buildButton(button, buttonClass, actionClass);
                if(builtButton == null) {
                    continue;
                }
                if(!invoke(builder, "button", builtButton.getClass(), builtButton)) {
                    invoke(builder, "addButton", builtButton.getClass(), builtButton);
                }
            }
            Object built = invoke(builder, "build");
            if(built != null) {
                return built;
            }
            return invoke(builder, "create");
        }
        Object dialog = invokeStatic(dialogClass, "create", Component.class, Component.class,
                spec.title, spec.body);
        if(dialog != null) {
            return dialog;
        }
        return invokeStatic(dialogClass, "of", Component.class, Component.class, spec.title,
                spec.body);
    }

    private static Object invokeStatic(Class<?> type, String name, Class<?> param,
            Object arg) {
        if(type == null) {
            return null;
        }
        try {
            return type.getMethod(name, param).invoke(null, arg);
        } catch(Exception ex) {
            return null;
        }
    }

    private static Object invokeStatic(Class<?> type, String name) {
        if(type == null) {
            return null;
        }
        try {
            return type.getMethod(name).invoke(null);
        } catch(Exception ex) {
            return null;
        }
    }

    private static Object invokeStatic(Class<?> type, String name, Class<?> paramOne,
            Class<?> paramTwo, Object argOne, Object argTwo) {
        if(type == null) {
            return null;
        }
        try {
            return type.getMethod(name, paramOne, paramTwo).invoke(null, argOne, argTwo);
        } catch(Exception ex) {
            return null;
        }
    }

    private static Object invokeStaticAssignable(Class<?> type, String name, Object[] args) {
        if(type == null) {
            return null;
        }
        for(Method method : type.getMethods()) {
            if(!method.getName().equals(name) || method.getParameterCount() != args.length) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            boolean matches = true;
            for(int i = 0; i < params.length; i++) {
                if(args[i] == null || !params[i].isInstance(args[i])) {
                    matches = false;
                    break;
                }
            }
            if(!matches) {
                continue;
            }
            try {
                return method.invoke(null, args);
            } catch(Exception ex) {
                return null;
            }
        }
        return null;
    }

    private static boolean invoke(Object target, String name, Class<?> param, Object arg) {
        if(target == null || arg == null) {
            return false;
        }
        try {
            Method method = target.getClass().getMethod(name, param);
            method.invoke(target, arg);
            return true;
        } catch(Exception ex) {
            for(Method method : target.getClass().getMethods()) {
                if(!method.getName().equals(name) || method.getParameterCount() != 1) {
                    continue;
                }
                Class<?> parameter = method.getParameterTypes()[0];
                if(!parameter.isInstance(arg)) {
                    continue;
                }
                try {
                    method.invoke(target, arg);
                    return true;
                } catch(Exception inner) {
                    return false;
                }
            }
            return false;
        }
    }

    private static Object invoke(Object target, String name) {
        if(target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(name);
            return method.invoke(target);
        } catch(Exception ex) {
            return null;
        }
    }

    private static class DialogSpec {
        private Component title;
        private Component body;
        private final List<ButtonSpec> buttons = new ArrayList<>();

        DialogSpec(Component title, Component body) {
            this.title = title;
            this.body = body;
        }

        void setTitle(Component title) {
            this.title = title;
        }

        void setBody(Component body) {
            this.body = body;
        }

        void addButton(ButtonSpec button) {
            buttons.add(button);
        }

        Object buildDialog() {
            return DialogCommands.buildDialog(this, null);
        }

        Object buildDialog(Class<?> dialogClass) {
            return DialogCommands.buildDialog(this, dialogClass);
        }
    }

    private static class ButtonSpec {
        private final Component label;
        private final String actionType;
        private final String actionValue;

        ButtonSpec(Component label, String actionType, String actionValue) {
            this.label = label;
            this.actionType = actionType == null ? "" : actionType;
            this.actionValue = actionValue == null ? "" : actionValue;
        }
    }
}
