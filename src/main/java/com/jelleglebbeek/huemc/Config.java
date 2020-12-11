package com.jelleglebbeek.huemc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {

    private final FileConfiguration cfg;
    private final FileConfiguration data;
    private final File dataFile;
    private final Main pl;

    public Config(Main pl) {
        this.cfg = pl.getConfig();
        this.dataFile = new File(pl.getDataFolder(), "data.yml");
        this.data = YamlConfiguration.loadConfiguration(dataFile);

        this.pl = pl;
        cfg.addDefault("hue-bridge-ip", "");
        cfg.addDefault("api-key", "");
        cfg.options().copyDefaults(true);
        pl.saveConfig();

    }

    public void removeInputBlock(InputBlock block) {
        for(String key : data.getKeys(false)) {
            if(key.equals(block.getUUID())) {
                data.set(key, null);
            }
        }
        saveData();
    }

    public void saveAllInputBlocks() {
        for(InputBlock inputBlock : pl.inputBlocks) {
            data.set(inputBlock.getUUID(), inputBlock);
        }
        saveData();
    }

    public void loadAllInputBlocks() {
        for(String key : data.getKeys(false)) {
            pl.inputBlocks.add((InputBlock) data.get(key));
        }
    }

    public void disable() {
        saveAllInputBlocks();
        pl.saveConfig();
        saveData();
    }

    public FileConfiguration getConfig() { return this.cfg; }
    public FileConfiguration getData() { return this.data; }
    public void saveData() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
