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
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
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

        Dialog build() {
            return Dialog.create(builder -> {
                var dialogBuilder = builder.empty()
                        .base(DialogBase.builder(title)
                                .body(List.of(DialogBody.plainMessage(body)))
                                .build());
                if(!buttons.isEmpty()) {
                    dialogBuilder.type(DialogType.multiAction(List.copyOf(buttons)).build());
                }
            });
        }
    }

    private static DialogAction createAction(String type, String value) {
        // commandTemplate executes a command with variables, plain command executes directly
        if("command".equalsIgnoreCase(type) || "run".equalsIgnoreCase(type)) {
            return createCommandAction(value);
        }
        if("commandTemplate".equalsIgnoreCase(type) || "template".equalsIgnoreCase(type)) {
            return DialogAction.commandTemplate(value);
        }
        if("custom".equalsIgnoreCase(type)) {
            DialogAction action = tryCreatePrefixedAction(value);
            if(action != null) {
                return action;
            }
            return DialogAction.customClick(createCustomKey(value), null);
        }
        if("suggest".equalsIgnoreCase(type) || "suggestCommand".equalsIgnoreCase(type)) {
            DialogAction action = tryCreateSuggestAction(value);
            return action != null ? action : DialogAction.staticAction(null);
        }
        if("url".equalsIgnoreCase(type) || "openUrl".equalsIgnoreCase(type)) {
            DialogAction action = tryCreateOpenUrlAction(value);
            return action != null ? action : DialogAction.staticAction(null);
        }
        if("copy".equalsIgnoreCase(type) || "copyText".equalsIgnoreCase(type)) {
            DialogAction action = tryCreateCopyAction(value);
            return action != null ? action : DialogAction.staticAction(null);
        }
        // staticAction allows basic built-in click behavior
        return DialogAction.staticAction(null);
    }

    private static DialogAction createCommandAction(String value) {
        if(value != null && value.contains("${")) {
            return DialogAction.commandTemplate(value);
        }
        DialogAction directAction = tryCreateDirectCommandAction(value);
        if(directAction != null) {
            return directAction;
        }
        Key key = Key.key("mundus", "command/" + UUID.randomUUID());
        COMMAND_ACTIONS.put(key, value);
        return DialogAction.customClick(key, null);
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

    private static DialogAction tryCreateDirectCommandAction(String value) {
        if(value == null || value.isBlank()) {
            return null;
        }
        String[] candidates = {"command", "runCommand", "executeCommand"};
        for(String name : candidates) {
            try {
                var method = DialogAction.class.getMethod(name, String.class);
                return (DialogAction) method.invoke(null, value);
            } catch(ReflectiveOperationException ex) {
                // continue to next candidate
            }
        }
        return null;
    }

    private static DialogAction tryCreatePrefixedAction(String value) {
        if(value == null || value.isBlank()) {
            return null;
        }
        int separator = value.indexOf(':');
        if(separator <= 0) {
            return null;
        }
        String prefix = value.substring(0, separator).trim().toLowerCase(Locale.ROOT);
        String payload = value.substring(separator + 1);
        if(payload.isBlank()) {
            return null;
        }
        switch(prefix) {
            case "suggest":
                return tryCreateSuggestAction(payload);
            case "url":
                return tryCreateOpenUrlAction(payload);
            case "copy":
                return tryCreateCopyAction(payload);
            case "command":
            case "run":
                return createCommandAction(payload);
            default:
                return null;
        }
    }

    private static DialogAction tryCreateSuggestAction(String value) {
        return tryCreateStringAction(value, "suggestCommand", "suggest");
    }

    private static DialogAction tryCreateOpenUrlAction(String value) {
        return tryCreateStringAction(value, "openUrl", "openURL", "openUri", "openURI");
    }

    private static DialogAction tryCreateCopyAction(String value) {
        return tryCreateStringAction(value, "copyToClipboard", "copy");
    }

    private static DialogAction tryCreateStringAction(String value, String... methodNames) {
        if(value == null || value.isBlank()) {
            return null;
        }
        for(String name : methodNames) {
            try {
                var method = DialogAction.class.getMethod(name, String.class);
                return (DialogAction) method.invoke(null, value);
            } catch(ReflectiveOperationException ex) {
                // continue to next candidate
            }
        }
        return null;
    }

    private static void registerListener() {
        if(listenerRegistered) {
            return;
        }
        listenerRegistered = true;
        Bukkit.getPluginManager().registerEvents(new DialogCommandListener(), MundusPlugin.instance);
    }

    private static class DialogCommandListener implements Listener {
        @EventHandler
        public void onCustomClick(PlayerCustomClickEvent event) {
            Key key = resolveKey(event);
            if(key == null) {
                return;
            }
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

        private Key resolveKey(PlayerCustomClickEvent event) {
            try {
                return (Key) event.getClass().getMethod("key").invoke(event);
            } catch(ReflectiveOperationException ex) {
                try {
                    return (Key) event.getClass().getMethod("getKey").invoke(event);
                } catch(ReflectiveOperationException ex2) {
                    return null;
                }
            }
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
