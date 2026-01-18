package me.hammerle.mp.snuviscript.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.key.Key;
import me.hammerle.mp.MundusPlugin;

public class DialogCommands {

    public static void registerFunctions() {
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
        // commandTemplate executes a command with variables
        if("command".equalsIgnoreCase(type) || "run".equalsIgnoreCase(type)) {
            return DialogAction.commandTemplate(value);
        }
        // customClick will later be captured with PlayerCustomClickEvent
        if("custom".equalsIgnoreCase(type)) {
            return DialogAction.customClick(Key.key(value), null);
        }
        // staticAction allows basic built-in click behavior
        return DialogAction.staticAction(null);
    }
}
