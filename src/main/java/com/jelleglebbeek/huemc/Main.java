package com.jelleglebbeek.huemc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public Config cfg;
    private Commands cmd;
    public HueAPI hue;

    @Override
    public void onEnable() {
        this.cfg = new Config(this);
        this.cmd = new Commands(this);
        getCommand("huemc").setExecutor(cmd);
        getCommand("hmc").setExecutor(cmd);
        if(!cfg.get().getString("api-key").equalsIgnoreCase("") && !cfg.get().getString("hue-bridge-ip").equalsIgnoreCase("")) {
            this.hue = new HueAPI(cfg.get().getString("hue-bridge-ip"), cfg.get().getString("api-key"));
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> this.hue.connect(Bukkit.getConsoleSender(), this.cfg));
        }
    }

    @Override
    public void onDisable() {
        cfg.disable();
        if(hue != null) hue.disable();
    }
}
