package com.jelleglebbeek.huemc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public Config cfg;
    private Commands cmd;
    public InputListener inputListener;
    public HueController hue;
    public ArrayList<InputBlock> inputBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(InputBlock.class, "InputBlock");
        this.cfg = new Config(this);
        this.cmd = new Commands(this);
        this.inputListener = new InputListener(this, hue);
        getCommand("huemc").setExecutor(cmd);
        getCommand("hmc").setExecutor(cmd);
        this.getServer().getPluginManager().registerEvents(cmd, this);
        this.getServer().getPluginManager().registerEvents(inputListener, this);
        attemptBridgeConnection();
    }

    @Override
    public void onDisable() {
        cfg.disable();
        if(hue != null) hue.disable();
    }

    private void attemptBridgeConnection() {
        if(!cfg.getConfig().getString("api-key").equalsIgnoreCase("") && !cfg.getConfig().getString("hue-bridge-ip").equalsIgnoreCase("")) {
            hue = new HueController(cfg.getConfig().getString("hue-bridge-ip"), cfg.getConfig().getString("api-key"));
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                hue.connect(Bukkit.getConsoleSender(), cfg);
                inputListener.updateHue(hue);
                cfg.loadAllInputBlocks();
                Bukkit.getLogger().log(Level.INFO, "Loaded " + inputBlocks.size() + " inputs");
            });
        }
    }
}
