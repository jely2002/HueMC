package com.jelleglebbeek.huemc;

import io.github.zeroone3010.yahueapi.*;
import io.github.zeroone3010.yahueapi.discovery.*;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class HueAPI {

    private String ip;
    private String apiKey;

    private Hue hue;

    public HueAPI(String ip, String apiKey) {
        this.ip = ip;
        this.apiKey = apiKey;
    }

    public HueAPI(String ip) {
        this.ip = ip;
        this.apiKey = null;
    }

    public void connect(Conversable conv, Config cfg) {
        if(apiKey == null) {
            conv.sendRawMessage("You have 30 seconds to press the link button.");
            final CompletableFuture<String> apiKey = Hue.hueBridgeConnectionBuilder(ip).initializeApiConnection("HueMC");
            String key = null;
            try {
                key = apiKey.get();
            } catch (InterruptedException e) {
                conv.sendRawMessage("An unknown interruption occured, failed to connect," + e.getMessage());
                return;
            } catch (ExecutionException e) {
                conv.sendRawMessage("The link button was not pressed in time.");
                return;
            }
            this.apiKey = key;
        }
        this.hue = new Hue(ip, apiKey);
        cfg.get().set("api-key", apiKey);
        cfg.get().set("hue-bridge-ip", ip);
        conv.sendRawMessage("HueMC is now connected to your bridge.");
    }

    public void disable() {
        if(this.hue != null) {
            hue.
        }
    }

    public static HashMap<String, String> discoverBridges() {
        Future<List<HueBridge>> bridgesFuture = new HueBridgeDiscoveryService()
                .discoverBridges(bridge -> System.out.println("Bridge found: " + bridge));
        List<HueBridge> bridges;
        try {
            bridges = bridgesFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            bridges = new ArrayList<>();
            e.printStackTrace();
        }
        if(!bridges.isEmpty()) {
            HashMap<String, String> bridgeInfo = new HashMap<>();
            for(HueBridge bridge : bridges) {
                bridgeInfo.put(bridge.getIp(), bridge.getName());
            }
            return bridgeInfo;
        } else {
            return null;
        }
    }
}
