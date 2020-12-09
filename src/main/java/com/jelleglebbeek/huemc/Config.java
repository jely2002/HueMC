package com.jelleglebbeek.huemc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private final FileConfiguration cfg;
    private final JavaPlugin pl;

    public Config(JavaPlugin pl) {
        this.cfg = pl.getConfig();
        this.pl = pl;
        cfg.addDefault("hue-bridge-ip", "");
        cfg.addDefault("api-key", "");
        cfg.options().copyDefaults(true);
        pl.saveConfig();
    }

    public void disable() {
        pl.saveConfig();
    }

    public FileConfiguration get() {
        return this.cfg;
    }

}
