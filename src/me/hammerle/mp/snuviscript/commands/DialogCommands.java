package me.hammerle.mp.snuviscript.commands;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.key.Key;
import me.hammerle.mp.MundusPlugin;

public class DialogCommands {
    private static final Map<Key, String> CUSTOM_ACTIONS = new ConcurrentHashMap<>();
    private static boolean listenerRegistered = false;

    public static void registerFunctions() {
        registerListener();
        MundusPlugin.scriptManager.registerFunction("dialog.new",
                (sc, in) -> new DialogBuilderSpec((Component) in[0].get(sc),
                        (Component) in[1].get(sc)));

        MundusPlugin.scriptManager.registerConsumer("dialog.settitle", (sc, in) -> {
            ((DialogBuilderSpec) in[0].get(sc)).setTitle((Component) in[1].get(sc));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.setbody", (sc, in) -> {
            ((DialogBuilderSpec) in[0].get(sc)).setBody((Component) in[1].get(sc));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addbutton", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String actionType = in[2].getString(sc);
            String actionValue = in[3].getString(sc);
            spec.addButton(label, actionType, actionValue);
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.show", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Player player = (Player) in[1].get(sc);
            showDialog(player, spec);
        });
    }

    private static void showDialog(Player player, DialogBuilderSpec spec) {
        if(spec == null)
            return;

        UniDialogBridge.showDialog(player, spec);
    }

    private static class DialogBuilderSpec {

        private Component title;
        private Component body;
        private final List<DialogButtonSpec> buttons = new ArrayList<>();

        DialogBuilderSpec(Component title, Component body) {
            this.title = title;
            this.body = body;
        }

        void setTitle(Component title) {
            this.title = title;
        }

        void setBody(Component body) {
            this.body = body;
        }

        void addButton(Component label, String actionType, String actionValue) {
            buttons.add(new DialogButtonSpec(label, actionType, actionValue));
        }
    }

    private static String normalizeCommand(String cmd) {
        cmd = cmd.trim();
        return cmd.startsWith("/") ? cmd : "/" + cmd;
    }

    private static Key createCustomKey(String value) {
        if(value != null && !value.isBlank()) {
            try {
                return Key.key(value);
            } catch(RuntimeException ex) {
                // fall through to generated key
            }
        }
        Key key = Key.key("mundus", "custom/" + UUID.randomUUID());
        CUSTOM_ACTIONS.put(key, value);
        return key;
    }

    private static void registerListener() {
        if(listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        Class<? extends Event> customEventClass = UniDialogBridge.getCustomClickEventClass();
        if(customEventClass == null) {
            MundusPlugin.instance.getLogger()
                    .warning("UniDialog custom click event not available; dialogs won't dispatch commands.");
            return;
        }
        DialogCommandListener listener = new DialogCommandListener();
        Bukkit.getPluginManager().registerEvent(customEventClass, listener, EventPriority.NORMAL,
                (ignored, event) -> listener.handleCustomClick(event), MundusPlugin.instance);
    }

    private static class DialogCommandListener implements Listener {
        public void handleCustomClick(Event event) {
            if(!UniDialogBridge.isCustomClickEvent(event)) {
                return;
            }
            Key key = UniDialogBridge.resolveIdentifier(event);
            if(key == null) {
                return;
            }
            String command = CUSTOM_ACTIONS.get(key);
            if(command == null || command.isBlank()) {
                return;
            }
            Player player = UniDialogBridge.resolvePlayer(event);
            if(player == null) {
                return;
            }
            if(command.startsWith("/")) {
                command = command.substring(1);
            }
            player.performCommand(command);
        }

    }

    private static class DialogButtonSpec {
        private final Component label;
        private final String actionType;
        private final String actionValue;

        DialogButtonSpec(Component label, String actionType, String actionValue) {
            this.label = label;
            this.actionType = actionType;
            this.actionValue = actionValue;
        }
    }

    private static class UniDialogBridge {
        private static final UniDialogReflection REFLECTION = UniDialogReflection.load();

        static boolean showDialog(Player player, DialogBuilderSpec spec) {
            if(REFLECTION == null) {
                MundusPlugin.instance.getLogger()
                        .warning("UniDialog plugin not available; dialog was not shown.");
                return false;
            }
            return REFLECTION.showDialog(player, spec);
        }

        static boolean isCustomClickEvent(Event event) {
            return REFLECTION != null && REFLECTION.isCustomClickEvent(event);
        }

        static Key resolveIdentifier(Event event) {
            if(REFLECTION == null) {
                return null;
            }
            return REFLECTION.resolveIdentifier(event);
        }

        static Player resolvePlayer(Event event) {
            if(REFLECTION == null) {
                return null;
            }
            return REFLECTION.resolvePlayer(event);
        }

        static Class<? extends Event> getCustomClickEventClass() {
            if(REFLECTION == null) {
                return null;
            }
            return REFLECTION.getCustomClickEventClass();
        }
    }

    private static class UniDialogReflection {
        private final Plugin plugin;
        private final Class<?> dialogClass;
        private final Class<?> dialogBaseClass;
        private final Class<?> dialogBodyClass;
        private final Class<?> dialogActionClass;
        private final Class<?> dialogTypeClass;
        private final Class<?> actionButtonClass;
        private final Class<?> customClickEventClass;

        private UniDialogReflection(Plugin plugin, Class<?> dialogClass, Class<?> dialogBaseClass,
                Class<?> dialogBodyClass, Class<?> dialogActionClass, Class<?> dialogTypeClass,
                Class<?> actionButtonClass, Class<?> customClickEventClass) {
            this.plugin = plugin;
            this.dialogClass = dialogClass;
            this.dialogBaseClass = dialogBaseClass;
            this.dialogBodyClass = dialogBodyClass;
            this.dialogActionClass = dialogActionClass;
            this.dialogTypeClass = dialogTypeClass;
            this.actionButtonClass = actionButtonClass;
            this.customClickEventClass = customClickEventClass;
        }

        static UniDialogReflection load() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("UniDialog");
            if(plugin == null) {
                return null;
            }
            try {
                Path jarPath = Path.of(plugin.getClass().getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI());
                ClassLoader classLoader = plugin.getClass().getClassLoader();
                Class<?> dialogClass = findClassBySimpleName(classLoader, jarPath, "Dialog");
                Class<?> dialogBaseClass = findClassBySimpleName(classLoader, jarPath, "DialogBase");
                Class<?> dialogBodyClass = findClassBySimpleName(classLoader, jarPath, "DialogBody");
                Class<?> dialogActionClass =
                        findClassBySimpleName(classLoader, jarPath, "DialogAction");
                Class<?> dialogTypeClass = findClassBySimpleName(classLoader, jarPath, "DialogType");
                Class<?> actionButtonClass =
                        findClassBySimpleName(classLoader, jarPath, "ActionButton");
                Class<?> customClickEventClass =
                        findClassBySimpleName(classLoader, jarPath, "PlayerCustomClickEvent");
                if(dialogClass == null || dialogBaseClass == null || dialogBodyClass == null
                        || dialogActionClass == null || dialogTypeClass == null
                        || actionButtonClass == null) {
                    MundusPlugin.instance.getLogger()
                            .warning("UniDialog classes not found; dialog support disabled.");
                    return null;
                }
                return new UniDialogReflection(plugin, dialogClass, dialogBaseClass, dialogBodyClass,
                        dialogActionClass, dialogTypeClass, actionButtonClass,
                        customClickEventClass);
            } catch(URISyntaxException | IOException ex) {
                MundusPlugin.instance.getLogger()
                        .warning("Unable to inspect UniDialog plugin jar: " + ex.getMessage());
                return null;
            }
        }

        boolean showDialog(Player player, DialogBuilderSpec spec) {
            Object dialog = buildDialog(spec);
            if(dialog == null) {
                return false;
            }
            if(tryInvoke(dialog, "open", player) || tryInvoke(dialog, "show", player)
                    || tryInvoke(dialog, "display", player)) {
                return true;
            }
            if(tryInvoke(player, "showDialog", dialog)) {
                return true;
            }
            if(tryInvoke(plugin, "openDialog", player, dialog)
                    || tryInvoke(plugin, "showDialog", player, dialog)) {
                return true;
            }
            MundusPlugin.instance.getLogger()
                    .warning("UniDialog dialog built but no show method was found.");
            return false;
        }

        boolean isCustomClickEvent(Event event) {
            return customClickEventClass != null && customClickEventClass.isInstance(event);
        }

        Key resolveIdentifier(Event event) {
            try {
                Object id = invoke(event, "getIdentifier");
                if(id instanceof Key) {
                    return (Key) id;
                }
                id = invoke(event, "identifier");
                if(id instanceof Key) {
                    return (Key) id;
                }
                return null;
            } catch(ReflectiveOperationException ex) {
                return null;
            }
        }

        Player resolvePlayer(Event event) {
            try {
                Object player = invoke(event, "player");
                if(player instanceof Player) {
                    return (Player) player;
                }
                player = invoke(event, "getPlayer");
                if(player instanceof Player) {
                    return (Player) player;
                }
            } catch(ReflectiveOperationException ex) {
                return null;
            }
            return null;
        }

        Class<? extends Event> getCustomClickEventClass() {
            if(customClickEventClass == null) {
                return null;
            }
            return customClickEventClass.asSubclass(Event.class);
        }

        private Object buildDialog(DialogBuilderSpec spec) {
            try {
                List<Object> actionButtons = new ArrayList<>();
                for(DialogButtonSpec button : spec.buttons) {
                    Object action = createAction(button.actionType, button.actionValue);
                    Object buttonBuilder = invoke(actionButtonClass, "builder", button.label);
                    if(action != null) {
                        invoke(buttonBuilder, "action", action);
                    }
                    Object actionButton = invoke(buttonBuilder, "build");
                    actionButtons.add(actionButton);
                }

                Consumer<Object> builderConsumer = dialogBuilder -> {
                    try {
                        Object dialogBuilderInstance = invoke(dialogBuilder, "empty");
                        Object dialogBody = invoke(dialogBodyClass, "plainMessage", spec.body);
                        Object baseBuilder = invoke(dialogBaseClass, "builder", spec.title);
                        Object base = invoke(baseBuilder, "body", List.of(dialogBody));
                        base = invoke(base, "build");
                        invoke(dialogBuilderInstance, "base", base);
                        if(!actionButtons.isEmpty()) {
                            Object typeBuilder = invoke(dialogTypeClass, "multiAction",
                                    List.copyOf(actionButtons));
                            Object dialogType = invoke(typeBuilder, "build");
                            invoke(dialogBuilderInstance, "type", dialogType);
                        }
                    } catch(ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                    }
                };
                return invoke(dialogClass, "create", builderConsumer);
            } catch(ReflectiveOperationException ex) {
                MundusPlugin.instance.getLogger()
                        .warning("Failed to build UniDialog dialog: " + ex.getMessage());
                return null;
            }
        }

        private Object createAction(String type, String value) throws ReflectiveOperationException {
            if(value == null) {
                value = "";
            }
            switch(type.toLowerCase(Locale.ROOT)) {
                case "command":
                case "run":
                    return invoke(dialogActionClass, "staticAction",
                            ClickEvent.runCommand(normalizeCommand(value)));

                case "suggest":
                case "suggestcommand":
                    return invoke(dialogActionClass, "staticAction",
                            ClickEvent.suggestCommand(normalizeCommand(value)));

                case "url":
                case "openurl":
                    return invoke(dialogActionClass, "staticAction", ClickEvent.openUrl(value));

                case "copy":
                case "copytext":
                    return invoke(dialogActionClass, "staticAction",
                            ClickEvent.copyToClipboard(value));

                case "commandtemplate":
                case "template":
                    // NOTE: Paper uses $(var) syntax, not ${var}
                    return invoke(dialogActionClass, "commandTemplate", value);

                case "custom":
                    return invokeCustomClick(createCustomKey(value));

                default:
                    return invokeCustomClick(createCustomKey("noop"));
            }
        }

        private Object invokeCustomClick(Key key) throws ReflectiveOperationException {
            try {
                return invoke(dialogActionClass, "customClick", key, null);
            } catch(ReflectiveOperationException ex) {
                return invoke(dialogActionClass, "customClick", key);
            }
        }

        private static Class<?> findClassBySimpleName(ClassLoader classLoader, Path jarPath,
                String simpleName) throws IOException {
            try(JarFile jarFile = new JarFile(jarPath.toFile())) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while(entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if(!name.endsWith(simpleName + ".class")) {
                        continue;
                    }
                    String className = name.replace('/', '.').replace(".class", "");
                    try {
                        return Class.forName(className, false, classLoader);
                    } catch(ClassNotFoundException ex) {
                        // Keep searching.
                    }
                }
            }
            return null;
        }

        private static Object invoke(Object target, String methodName, Object... args)
                throws ReflectiveOperationException {
            Class<?> clazz = target instanceof Class<?> ? (Class<?>) target : target.getClass();
            var method = findMethod(clazz, methodName, args);
            if(method == null) {
                throw new NoSuchMethodException(methodName);
            }
            return method.invoke(target instanceof Class<?> ? null : target, args);
        }

        private static boolean tryInvoke(Object target, String methodName, Object... args) {
            try {
                invoke(target, methodName, args);
                return true;
            } catch(ReflectiveOperationException ex) {
                return false;
            }
        }

        private static java.lang.reflect.Method findMethod(Class<?> clazz, String methodName,
                Object... args) {
            for(var method : clazz.getMethods()) {
                if(!method.getName().equals(methodName)) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if(params.length != args.length) {
                    continue;
                }
                boolean match = true;
                for(int i = 0; i < params.length; i++) {
                    Object arg = args[i];
                    if(arg == null) {
                        continue;
                    }
                    Class<?> paramType = wrapPrimitive(params[i]);
                    if(!paramType.isAssignableFrom(arg.getClass())) {
                        match = false;
                        break;
                    }
                }
                if(match) {
                    return method;
                }
            }
            return null;
        }

        private static Class<?> wrapPrimitive(Class<?> type) {
            if(!type.isPrimitive()) {
                return type;
            }
            if(type == boolean.class) {
                return Boolean.class;
            }
            if(type == byte.class) {
                return Byte.class;
            }
            if(type == short.class) {
                return Short.class;
            }
            if(type == int.class) {
                return Integer.class;
            }
            if(type == long.class) {
                return Long.class;
            }
            if(type == float.class) {
                return Float.class;
            }
            if(type == double.class) {
                return Double.class;
            }
            if(type == char.class) {
                return Character.class;
            }
            return type;
        }
    }
}
