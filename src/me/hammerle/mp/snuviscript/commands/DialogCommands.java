package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import io.papermc.paper.registry.data.dialog.input.DialogInput;
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

        MundusPlugin.scriptManager.registerConsumer("dialog.addinputbool", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Key key = createInputKey(in[1].getString(sc));
            Component label = (Component) in[2].get(sc);
            boolean defaultValue = in.length > 3 && in[3].getBool(sc);
            spec.addInput(DialogInput.bool(key, label, defaultValue));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinputsingleoption", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Key key = createInputKey(in[1].getString(sc));
            Component label = (Component) in[2].get(sc);
            Object rawOptions = in[3].get(sc);
            String defaultId = in.length > 4 ? in[4].getString(sc) : null;
            List<DialogInput.SingleOption.Option> options = new ArrayList<>();
            List<String> optionIds = new ArrayList<>();
            appendSingleOptions(options, optionIds, rawOptions);
            if(defaultId == null && !optionIds.isEmpty()) {
                defaultId = optionIds.get(0);
            }
            spec.addInput(DialogInput.singleOption(key, label, options, defaultId));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinputtext", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Key key = createInputKey(in[1].getString(sc));
            Component label = (Component) in[2].get(sc);
            String defaultValue = in.length > 3 ? in[3].getString(sc) : "";
            int maxLength = in.length > 4 ? in[4].getInt(sc) : 256;
            spec.addInput(DialogInput.text(key, label, defaultValue, maxLength));
        });

        MundusPlugin.scriptManager.registerConsumer("dialog.addinputnumberrange", (sc, in) -> {
            DialogBuilderSpec spec = (DialogBuilderSpec) in[0].get(sc);
            Key key = createInputKey(in[1].getString(sc));
            Component label = (Component) in[2].get(sc);
            double min = in[3].getDouble(sc);
            double max = in[4].getDouble(sc);
            double step = in[5].getDouble(sc);
            double defaultValue = in.length > 6 ? in[6].getDouble(sc) : min;
            spec.addInput(DialogInput.numberRange(key, label, min, max, step, defaultValue));
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
        private final List<DialogInput> inputs = new ArrayList<>();

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

        void addInput(DialogInput input) {
            inputs.add(input);
        }

        Dialog build() {
            return Dialog.create(builder -> {
                var dialogBuilder = builder.empty()
                        .base(DialogBase.builder(title)
                                .body(List.of(DialogBody.plainMessage(body)))
                                .build());
                if(!inputs.isEmpty()) {
                    ActionButton submitButton = buttons.isEmpty()
                            ? ActionButton.builder(Component.text("Submit")).build()
                            : buttons.get(0);
                    dialogBuilder.type(
                            DialogType.input(List.copyOf(inputs), submitButton).build());
                } else if(!buttons.isEmpty()) {
                    dialogBuilder.type(DialogType.multiAction(List.copyOf(buttons)).build());
                }
            });
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

    private static void appendSingleOptions(List<DialogInput.SingleOption.Option> options,
            List<String> optionIds, Object rawOptions) {
        if(rawOptions instanceof List<?> rawList) {
            int index = 0;
            for(Object option : rawList) {
                appendSingleOption(options, optionIds, option, index++);
            }
            return;
        }
        if(rawOptions instanceof Object[] rawArray) {
            for(int i = 0; i < rawArray.length; i++) {
                appendSingleOption(options, optionIds, rawArray[i], i);
            }
            return;
        }
        appendSingleOption(options, optionIds, rawOptions, 0);
    }

    private static void appendSingleOption(List<DialogInput.SingleOption.Option> options,
            List<String> optionIds, Object option, int index) {
        String id = null;
        Component label = null;
        if(option instanceof Map<?, ?> map) {
            Object idValue = map.get("id");
            Object labelValue = map.get("label");
            if(idValue != null) {
                id = idValue.toString();
            }
            if(labelValue instanceof Component labelComponent) {
                label = labelComponent;
            } else if(labelValue != null) {
                label = Component.text(labelValue.toString());
            }
        } else if(option instanceof Component component) {
            label = component;
        } else if(option != null) {
            String text = option.toString();
            id = text;
            label = Component.text(text);
        }
        if(id == null || id.isBlank()) {
            id = "option-" + index;
        }
        if(label == null) {
            label = Component.text(id);
        }
        optionIds.add(id);
        options.add(DialogInput.SingleOption.Option.option(id, label));
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
