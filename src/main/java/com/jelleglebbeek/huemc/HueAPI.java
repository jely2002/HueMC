package com.jelleglebbeek.huemc;

import io.github.zeroone3010.yahueapi.*;
import io.github.zeroone3010.yahueapi.discovery.*;
import org.bukkit.conversations.Conversable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
                conv.sendRawMessage("An unknown interruption occured, failed to connect.");
                conv.sendRawMessage(e.getMessage());
                return;
            } catch (ExecutionException e) {
                conv.sendRawMessage("The link button was not pressed in time.");
                conv.sendRawMessage("HueMC could not connect to your bridge.");
                return;
            }
            this.apiKey = key;
        }
        this.hue = new Hue(ip, apiKey);
        cfg.get().set("api-key", apiKey);
        cfg.get().set("hue-bridge-ip", ip);
        conv.sendRawMessage("HueMC is now connected to your bridge.");
    }

    public ArrayList<String> getRooms() {
        ArrayList<String> roomNames = new ArrayList<>();
        for (Room room : hue.getRooms()) {
            roomNames.add(room.getName());
        }
        return roomNames;
    }

    public ArrayList<String> getZones() {
        ArrayList<String> zoneNames = new ArrayList<>();
        for (Room room : hue.getZones()) {
            zoneNames.add(room.getName());
        }
        return zoneNames;
    }

    public ArrayList<String> getLights(String name, boolean isZone) {
        ArrayList<String> lightNames = new ArrayList<>();
        for(Light light : isZone ? hue.getZoneByName(name).get().getLights() : hue.getRoomByName(name).get().getLights()) {
            lightNames.add(light.getName());
        }
        return lightNames;
    }

    public void disable() {
        //Empty method stub
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
