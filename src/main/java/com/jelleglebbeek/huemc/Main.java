package com.jelleglebbeek.huemc;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private Config cfg;
    private Commands cmd;

    @Override
    public void onEnable() {
        this.cfg = new Config(this);
        this.cmd = new Commands(this);
        getCommand("huemc").setExecutor(cmd);
        getCommand("hmc").setExecutor(cmd);
    }

    @Override
    public void onDisable() {
        cfg.disable();
    }
}
