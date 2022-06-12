package me.hammerle.kp.snuviscript.commands;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import me.hammerle.kp.KajetansPlugin;
import me.hammerle.kp.NMS;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class TextCommands {
    public static void registerFunctions() {
        KajetansPlugin.scriptManager.registerFunction("string.text",
                (sc, in) -> PlainTextComponentSerializer.plainText()
                        .serialize((Component) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("string.item",
                (sc, in) -> NMS.toString((ItemStack) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("string.entity",
                (sc, in) -> NMS.toString((Entity) in[0].get(sc)));
        KajetansPlugin.scriptManager.registerFunction("string.blockdata",
                (sc, in) -> ((BlockData) in[0].get(sc)).getAsString());
        KajetansPlugin.scriptManager.registerFunction("string.blockentity",
                (sc, in) -> in[0].getString(sc));

        KajetansPlugin.scriptManager.registerFunction("text.new",
                (sc, in) -> Component.text(in[0].getString(sc)));
        KajetansPlugin.scriptManager.registerFunction("text.color", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.color(TextColor.color(in[1].getInt(sc), in[2].getInt(sc), in[3].getInt(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.click", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.clickEvent(ClickEvent.runCommand(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.suggest", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.clickEvent(ClickEvent.suggestCommand(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.item", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.hoverEvent(((ItemStack) in[1].get(sc)).asHoverEvent());
        });
        KajetansPlugin.scriptManager.registerFunction("text.hover", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.hoverEvent(HoverEvent.showText((Component) in[1].get(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.link", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.clickEvent(ClickEvent.openUrl(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.clipboard", (sc, in) -> {
            Component c = (Component) in[0].get(sc);
            return c.clickEvent(ClickEvent.copyToClipboard(in[1].getString(sc)));
        });
        KajetansPlugin.scriptManager.registerFunction("text.merge", (sc, in) -> {
            TextComponent.Builder c = Component.text();
            for(int i = 0; i < in.length; i++) {
                c.append((Component) in[i].get(sc));
            }
            return c.build();
        });
    }
}
