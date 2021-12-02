/*package me.km.inventory;

import me.hammerle.snuviscript.code.Script;
import me.km.Server;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ServerCustomContainer extends CustomContainer {
    private final Script script;
    private final ITextComponent name;

    public ServerCustomContainer(int id, PlayerInventory pInv, ModInventory inv, String name, Script sc) {
        super(id, pInv, inv);
        this.script = sc;
        this.name = new StringTextComponent(name);
    }

    @Override
    public void onContainerClosed(PlayerEntity p) {
        Server.scriptEvents.onInventoryClose(script, name, getInventoryBase(), p);
        super.onContainerClosed(p);
    }

    @Override
    public boolean onButtonClick(int slot, int dragType, ClickType click, PlayerEntity p) {
        return Server.scriptEvents.onInventoryClick(script, name, getInventoryBase(), slot, click, p);
    }

    public ITextComponent getName() {
        return name;
    }
}
*/
