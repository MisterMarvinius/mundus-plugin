package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.key.Key;
import me.hammerle.mp.MundusPlugin;

public class DialogCommands {
    private static final Map<Key, String> COMMAND_ACTIONS = new ConcurrentHashMap<>();
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

        MundusPlugin.scriptManager.registerConsumer("dialog.addinput", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            spec.addInput(in[1].get(sc));
        });

        MundusPlugin.scriptManager.registerFunction("dialog.input.bool", (sc, in) -> {
            Component label = (Component) in[0].get(sc);
            String key = in[1].getString(sc);
            boolean initial = in.length > 2 && in[2].getBoolean(sc);
            return createBoolInput(label, key, initial);
        });

        MundusPlugin.scriptManager.registerFunction("dialog.input.text", (sc, in) -> {
            Component label = (Component) in[0].get(sc);
            String key = in[1].getString(sc);
            String initial = in.length > 2 ? in[2].getString(sc) : "";
            int maxLength = in.length > 3 ? in[3].getInt(sc) : 100;
            return createTextInput(label, key, initial, maxLength);
        });

        MundusPlugin.scriptManager.registerFunction("dialog.input.numberrange", (sc, in) -> {
            Component label = (Component) in[0].get(sc);
            String key = in[1].getString(sc);
            double min = in[2].getDouble(sc);
            double max = in[3].getDouble(sc);
            double step = in[4].getDouble(sc);
            double initial = in[5].getDouble(sc);
            return createNumberRangeInput(label, key, min, max, step, initial);
        });

        MundusPlugin.scriptManager.registerFunction("dialog.input.singleoption", (sc, in) -> {
            Component label = (Component) in[0].get(sc);
            String key = in[1].getString(sc);
            List<DialogOptionSpec> options = (List<DialogOptionSpec>) in[2].get(sc);
            int initialIndex = in.length > 3 ? in[3].getInt(sc) : 0;
            return createSingleOptionInput(label, key, options, initialIndex);
        });

        MundusPlugin.scriptManager.registerFunction("dialog.option", (sc, in) -> {
            Component label = (Component) in[0].get(sc);
            String value = in.length > 1 ? in[1].getString(sc) : "";
            return new DialogOptionSpec(label, value);
        });

        MundusPlugin.scriptManager.registerFunction("dialog.optionlist.new", (sc, in) -> {
            return new ArrayList<DialogOptionSpec>();
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.optionlist.add", (sc, in) -> {
            List<DialogOptionSpec> list = (List<DialogOptionSpec>) in[0].get(sc);
            list.add((DialogOptionSpec) in[1].get(sc));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinput.bool", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String key = in[2].getString(sc);
            boolean initial = in.length > 3 && in[3].getBoolean(sc);
            spec.addBoolInput(label, key, initial);
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinput.text", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String key = in[2].getString(sc);
            String initial = in.length > 3 ? in[3].getString(sc) : "";
            int maxLength = in.length > 4 ? in[4].getInt(sc) : 100;
            spec.addTextInput(label, key, initial, maxLength);
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinput.numberrange", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String key = in[2].getString(sc);
            double min = in[3].getDouble(sc);
            double max = in[4].getDouble(sc);
            double step = in[5].getDouble(sc);
            double initial = in[6].getDouble(sc);
            spec.addNumberRangeInput(label, key, min, max, step, initial);
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinput.singleoption", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Component label = (Component) in[1].get(sc);
            String key = in[2].getString(sc);
            List<DialogOptionSpec> options = (List<DialogOptionSpec>) in[3].get(sc);
            int initialIndex = in.length > 4 ? in[4].getInt(sc) : 0;
            spec.addSingleOptionInput(label, key, options, initialIndex);
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

        Dialog dialog = spec.build();
        player.showDialog(dialog);
    }

    private static class DialogBuilderSpec {

        private Component title;
        private Component body;
        private final List<ActionButton> buttons = new ArrayList<>();
        private final List<Object> inputs = new ArrayList<>();

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
            DialogAction action = createAction(actionType, actionValue);
            ActionButton button = ActionButton.builder(label)
                    .action(action)
                    .build();
            buttons.add(button);
        }

        void addInput(Object input) {
            if(input == null) {
                return;
            }
            inputs.add(input);
        }

        void addBoolInput(Component label, String key, boolean initial) {
            addInput(createBoolInput(label, key, initial));
        }

        void addTextInput(Component label, String key, String initial, int maxLength) {
            addInput(createTextInput(label, key, initial, maxLength));
        }

        void addNumberRangeInput(Component label, String key, double min, double max, double step,
                double initial) {
            addInput(createNumberRangeInput(label, key, min, max, step, initial));
        }

        void addSingleOptionInput(Component label, String key, List<DialogOptionSpec> options,
                int initialIndex) {
            addInput(createSingleOptionInput(label, key, options, initialIndex));
        }

        Dialog build() {
            return Dialog.create(builder -> {
                var baseBuilder = DialogBase.builder(title)
                        .body(List.of(DialogBody.plainMessage(body)));
                applyInputs(baseBuilder, inputs);
                var dialogBuilder = builder.empty().base(baseBuilder.build());
                if(!buttons.isEmpty()) {
                    dialogBuilder.type(DialogType.multiAction(List.copyOf(buttons)).build());
                }
            });
        }
    }

    private static class DialogOptionSpec {
        private final Component label;
        private final String value;

        DialogOptionSpec(Component label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    private static DialogAction createAction(String type, String value) {
        if(value == null)
            value = "";

        switch(type.toLowerCase(Locale.ROOT)) {
            case "command":
            case "run":
                return DialogAction.staticAction(ClickEvent.runCommand(normalizeCommand(value)));

            case "suggest":
            case "suggestcommand":
                return DialogAction
                        .staticAction(ClickEvent.suggestCommand(normalizeCommand(value)));

            case "url":
            case "openurl":
                return DialogAction.staticAction(ClickEvent.openUrl(value));

            case "copy":
            case "copytext":
                return DialogAction.staticAction(ClickEvent.copyToClipboard(value));

            case "commandtemplate":
            case "template":
                // NOTE: Paper uses $(var) syntax, not ${var}
                return DialogAction.commandTemplate(value);

            case "custom":
                return DialogAction.customClick(createCustomKey(value), null);

            default:
                // If you want "No action", don't add an action at all, or use a custom click you ignore.
                return DialogAction.customClick(createCustomKey("noop"), null);
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

    private static Object createBoolInput(Component label, String key, boolean initial) {
        Key inputKey = createInputKey(key);
        return createDialogInput("bool",
                new Object[] {inputKey, label, initial},
                new Object[] {label, initial},
                new Object[] {inputKey, label});
    }

    private static Object createTextInput(Component label, String key, String initial,
            int maxLength) {
        Key inputKey = createInputKey(key);
        return createDialogInput("text",
                new Object[] {inputKey, label, initial, maxLength},
                new Object[] {label, initial, maxLength},
                new Object[] {inputKey, label, initial},
                new Object[] {label, initial});
    }

    private static Object createNumberRangeInput(Component label, String key, double min,
            double max, double step, double initial) {
        Key inputKey = createInputKey(key);
        int minInt = (int) min;
        int maxInt = (int) max;
        int stepInt = (int) step;
        int initialInt = (int) initial;
        return createDialogInput("numberRange",
                new Object[] {inputKey, label, min, max, step, initial},
                new Object[] {label, min, max, step, initial},
                new Object[] {inputKey, label, minInt, maxInt, stepInt, initialInt},
                new Object[] {label, minInt, maxInt, stepInt, initialInt},
                new Object[] {inputKey, label, min, max},
                new Object[] {label, min, max});
    }

    private static Object createSingleOptionInput(Component label, String key,
            List<DialogOptionSpec> options, int initialIndex) {
        Key inputKey = createInputKey(key);
        List<Object> optionList = new ArrayList<>();
        if(options != null) {
            for(DialogOptionSpec option : options) {
                Object built = createDialogOption(option);
                if(built != null) {
                    optionList.add(built);
                }
            }
        }
        return createDialogInput("singleOption",
                new Object[] {inputKey, label, optionList, initialIndex},
                new Object[] {label, optionList, initialIndex},
                new Object[] {inputKey, label, optionList},
                new Object[] {label, optionList});
    }

    private static Object createDialogOption(DialogOptionSpec option) {
        if(option == null) {
            return null;
        }
        Class<?> dialogInputClass = getDialogInputClass();
        Object created = invokeStatic(dialogInputClass, "option",
                new Object[] {option.label, option.value});
        if(created != null) {
            return created;
        }
        Class<?> optionClass = getDialogOptionClass();
        created = invokeStatic(optionClass, "of", new Object[] {option.label, option.value});
        if(created != null) {
            return created;
        }
        Object builder = invokeStatic(optionClass, "builder", new Object[] {option.label});
        if(builder != null) {
            invokeInstance(builder, "value", new Object[] {option.value});
            return invokeInstance(builder, "build", new Object[0]);
        }
        return null;
    }

    private static Object createDialogInput(String method, Object[]... candidates) {
        Class<?> dialogInputClass = getDialogInputClass();
        if(dialogInputClass == null) {
            return null;
        }
        for(Object[] args : candidates) {
            Object created = invokeStatic(dialogInputClass, method, args);
            if(created != null) {
                return created;
            }
        }
        return null;
    }

    private static void applyInputs(Object baseBuilder, List<Object> inputs) {
        if(inputs == null || inputs.isEmpty() || baseBuilder == null) {
            return;
        }
        if(invokeInstance(baseBuilder, "inputs", new Object[] {List.copyOf(inputs)}) != null) {
            return;
        }
        for(Object input : inputs) {
            if(invokeInstance(baseBuilder, "input", new Object[] {input}) == null) {
                break;
            }
        }
    }

    private static Key createInputKey(String value) {
        if(value != null && !value.isBlank()) {
            try {
                return Key.key(value);
            } catch(RuntimeException ex) {
                // fall through to generated key
            }
        }
        return Key.key("mundus", "input/" + UUID.randomUUID());
    }

    private static Class<?> getDialogInputClass() {
        try {
            return Class.forName("io.papermc.paper.registry.data.dialog.input.DialogInput");
        } catch(ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> getDialogOptionClass() {
        try {
            return Class.forName(
                    "io.papermc.paper.registry.data.dialog.input.DialogInput$Option");
        } catch(ClassNotFoundException ex) {
            return null;
        }
    }

    private static Object invokeStatic(Class<?> target, String methodName, Object[] args) {
        if(target == null) {
            return null;
        }
        for(Method method : target.getMethods()) {
            if(!method.getName().equals(methodName)) {
                continue;
            }
            if(!Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if(!matches(method.getParameterTypes(), args)) {
                continue;
            }
            try {
                return method.invoke(null, args);
            } catch(ReflectiveOperationException ex) {
                return null;
            }
        }
        return null;
    }

    private static Object invokeInstance(Object target, String methodName, Object[] args) {
        if(target == null) {
            return null;
        }
        for(Method method : target.getClass().getMethods()) {
            if(!method.getName().equals(methodName)) {
                continue;
            }
            if(!matches(method.getParameterTypes(), args)) {
                continue;
            }
            try {
                return method.invoke(target, args);
            } catch(ReflectiveOperationException ex) {
                return null;
            }
        }
        return null;
    }

    private static boolean matches(Class<?>[] params, Object[] args) {
        if(params.length != args.length) {
            return false;
        }
        for(int i = 0; i < params.length; i++) {
            if(!isCompatible(params[i], args[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isCompatible(Class<?> paramType, Object arg) {
        if(arg == null) {
            return !paramType.isPrimitive();
        }
        Class<?> wrapped = paramType.isPrimitive() ? wrapPrimitive(paramType) : paramType;
        return wrapped.isInstance(arg);
    }

    private static Class<?> wrapPrimitive(Class<?> type) {
        if(type == boolean.class) {
            return Boolean.class;
        }
        if(type == int.class) {
            return Integer.class;
        }
        if(type == long.class) {
            return Long.class;
        }
        if(type == double.class) {
            return Double.class;
        }
        if(type == float.class) {
            return Float.class;
        }
        if(type == short.class) {
            return Short.class;
        }
        if(type == byte.class) {
            return Byte.class;
        }
        if(type == char.class) {
            return Character.class;
        }
        return type;
    }

    private static void registerListener() {
        if(listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        Bukkit.getPluginManager().registerEvents(new DialogCommandListener(),
                MundusPlugin.instance);
    }

    private static class DialogCommandListener implements Listener {
        @EventHandler
        public void onCustomClick(PlayerCustomClickEvent event) {
            Key key = event.getIdentifier();
            String command = COMMAND_ACTIONS.get(key);
            if(command == null || command.isBlank()) {
                return;
            }
            Player player = resolvePlayer(event);
            if(player == null) {
                return;
            }
            if(command.startsWith("/")) {
                command = command.substring(1);
            }
            player.performCommand(command);
        }

        private Player resolvePlayer(PlayerCustomClickEvent event) {
            try {
                return (Player) event.getClass().getMethod("player").invoke(event);
            } catch(ReflectiveOperationException ex) {
                try {
                    return (Player) event.getClass().getMethod("getPlayer").invoke(event);
                } catch(ReflectiveOperationException ex2) {
                    return null;
                }
            }
        }
    }
}
