package me.hammerle.kp;

import org.bukkit.plugin.java.JavaPlugin;

public class KajetansPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        //PaperLib.suggestPaper(this);
        //saveDefaultConfig();
        this.getDataFolder();
        getLogger().info("HI");
    }
}
