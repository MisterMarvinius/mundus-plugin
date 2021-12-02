/*package me.km.snuviscript.commands;

import me.hammerle.snuviscript.code.ScriptManager;
import me.km.utils.Location;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class TextCommands {
    public static void registerFunctions(ScriptManager sm) {
        sm.registerFunction("text.location", (sc, in) -> ((Location) in[0].get(sc)).toString());
        sm.registerFunction("text.locationblock", (sc, in) -> ((Location) in[0].get(sc)).toBlockString());
        sm.registerFunction("text.item", (sc, in) -> ((ItemStack) in[0].get(sc)).write(new CompoundNBT()).toString());
        sm.registerFunction("text.click", (sc, in) -> {
            Object message = in[0].get(sc);
            IFormattableTextComponent text;
            if(message instanceof IFormattableTextComponent) {
                text = (IFormattableTextComponent) message;
            } else {
                text = new StringTextComponent(String.valueOf(message));
            }
            text.modifyStyle(style -> {
                return style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, in[1].getString(sc)));
            });
            return text;
        });
        sm.registerFunction("text.hover", (sc, in) -> {
            Object message = in[0].get(sc);
            IFormattableTextComponent text;
            if(message instanceof IFormattableTextComponent) {
                text = (IFormattableTextComponent) message;
            } else {
                text = new StringTextComponent(String.valueOf(message));
            }
            text.modifyStyle(style -> {
                return style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(in[1].getString(sc))));
            });
            return text;
        });
        sm.registerFunction("text.link", (sc, in) -> {
            StringTextComponent text = new StringTextComponent(in[0].getString(sc));
            text.modifyStyle(style -> {
                return style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, in[1].getString(sc)));
            });
            return text;
        });
        sm.registerFunction("text.clipboard", (sc, in) -> {
            StringTextComponent text = new StringTextComponent(in[0].getString(sc));
            text.modifyStyle(style -> {
                return style.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, in[1].getString(sc)));
            });
            return text;
        });
        sm.registerFunction("text.copytext", (sc, in) -> {
            String s = in[1].getString(sc).replace(" ", "%20");
            StringTextComponent text = new StringTextComponent(in[0].getString(sc));
            text.modifyStyle(style -> {
                return style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://minecraft.hammerle.me/showtext.php/?text=" + s));
            });
            return text;
        });
        sm.registerFunction("text.entity", (sc, in) -> {
            CompoundNBT tag = new CompoundNBT();
            ((Entity) in[0].get(sc)).writeWithoutTypeId(tag);
            return tag.toString();
        });
    }
}
*/
