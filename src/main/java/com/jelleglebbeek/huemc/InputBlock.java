package com.jelleglebbeek.huemc;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("InputBlock")
public class InputBlock implements ConfigurationSerializable {

    private UUID uuid;
    private Location location;
    private InputType type;
    private String areaName;
    private String lightName;
    private Color color;
    private boolean isZone;
    private boolean isPowered;

    public InputBlock(String uuid, Location location, InputType type, String areaName, boolean isZone) {
        if(uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
        this.location = location;
        this.color = null;
        this.type = type;
        this.areaName = areaName;
        this.lightName = null;
        this.isZone = isZone;
    }

    public InputBlock(String uuid, Location location, InputType type, String areaName, boolean isZone, String lightName) {
        if(uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
        this.location = location;
        this.color = null;
        this.type = type;
        this.areaName = areaName;
        this.lightName = lightName;
        this.isZone = isZone;
    }

    public InputBlock(String uuid, Location location, InputType type, String areaName, boolean isZone, String lightName, Color color) {
        if(uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
        this.location = location;
        this.color = color;
        this.type = type;
        this.areaName = areaName;
        this.lightName = lightName;
        this.isZone = isZone;
    }

    public InputBlock(String uuid, Location location, InputType type, String areaName, boolean isZone, Color color) {
        if(uuid == null) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
        this.location = location;
        this.color = color;
        this.type = type;
        this.areaName = areaName;
        this.lightName = null;
        this.isZone = isZone;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isZone() {
        return isZone;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }

    public Location getLocation() {
        return this.location;
    }

    public InputType getType() {
        return this.type;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public String getLightName() {
        return this.lightName;
    }

    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("location", this.location);
        result.put("inputType", this.type.toString());
        result.put("areaName", this.areaName);
        result.put("lightName", this.lightName);
        result.put("isZone", this.isZone);
        result.put("color", this.color);
        result.put("uuid", this.uuid.toString());
        return result;
    }

    public static InputBlock deserialize(Map<String, Object> args) {
        if(args.get("lightName") == null) {
            if (args.get("color") == null) {
                return new InputBlock((String) args.get("uuid"),(Location) args.get("location"), InputType.valueOf((String) args.get("inputType")), (String) args.get("areaName"), (boolean) args.get("isZone"));
            } else {
                return new InputBlock((String) args.get("uuid"),(Location) args.get("location"), InputType.valueOf((String) args.get("inputType")), (String) args.get("areaName"), (boolean) args.get("isZone"), (Color) args.get("color"));
            }
        } else {
            if(args.get("color") == null) {
                return new InputBlock((String) args.get("uuid"), (Location) args.get("location"), InputType.valueOf((String) args.get("inputType")), (String) args.get("areaName"), (boolean) args.get("isZone"), (String) args.get("lightName"));

            } else {
                return new InputBlock((String) args.get("uuid"), (Location) args.get("location"), InputType.valueOf((String) args.get("inputType")), (String) args.get("areaName"), (boolean) args.get("isZone"), (String) args.get("lightName"), (Color) args.get("color"));
            }
        }
    }
}
