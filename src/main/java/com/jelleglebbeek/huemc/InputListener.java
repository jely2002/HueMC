package com.jelleglebbeek.huemc;

import io.github.zeroone3010.yahueapi.Hue;
import io.github.zeroone3010.yahueapi.State;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.HashMap;

public class InputListener implements Listener {

    private Main pl;
    private HueController hue;
    private HashMap<Block, InputBlock> dimmerCombination = new HashMap<>();
    private boolean powerlineChange = false;

    public InputListener(Main pl, HueController hue) {
        this.pl = pl;
        this.hue = hue;
    }

    public void updateHue(HueController hueController) {
        this.hue = hueController;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (hue == null) return;
        if(!e.getBlock().getType().toString().contains("GLASS") || e.getBlock().getType().toString().contains("PANE")) return;
        for(InputBlock inputBlock : pl.inputBlocks) {
            if(inputBlock.getLocation().equals(e.getBlock().getRelative(BlockFace.UP).getLocation())) {
                if(e.getBlock().getType().toString().contains("GLASS") && !e.getBlock().getType().toString().contains("PANE")) {
                    String dyeColor = e.getBlock().getType().toString().replace("_STAINED_GLASS", "");
                    Color RGBcolor = DyeColor.valueOf(dyeColor).getColor();
                    java.awt.Color HueColor = new java.awt.Color(RGBcolor.getRed(), RGBcolor.getGreen(), RGBcolor.getBlue());
                    State lightState = State.builder().color(HueColor).keepCurrentState();
                    Bukkit.getScheduler().runTaskAsynchronously(pl, () -> hue.startAction(HueAction.STATE, hue.getLights(inputBlock), lightState));
                }
            }
        }
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent e) {
        if(!pl.cfg.getConfig().getBoolean("white-light-when-no-glass")) return;
        if (hue == null) return;
        if(!e.getBlock().getType().toString().contains("GLASS") || e.getBlock().getType().toString().contains("PANE")) return;
        for(InputBlock inputBlock : pl.inputBlocks) {
            if(inputBlock.getLocation().equals(e.getBlock().getRelative(BlockFace.UP).getLocation())) {
                if(e.getBlock().getType().toString().contains("GLASS") && !e.getBlock().getType().toString().contains("PANE")) {
                    State lightState = State.builder().color(java.awt.Color.WHITE).keepCurrentState();
                    inputBlock.setColor(java.awt.Color.WHITE);
                    Bukkit.getScheduler().runTaskAsynchronously(pl, () -> hue.startAction(HueAction.STATE, hue.getLights(inputBlock), lightState));
                }
            }
        }
    }

    private void updateColor(InputBlock inputBlock) {
        Block colorBlock = inputBlock.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if(colorBlock.getType().toString().contains("GLASS") && !colorBlock.getType().toString().contains("PANE")) {
            String dyeColor = colorBlock.getType().toString().replace("_STAINED_GLASS", "");
            Color RGBcolor = DyeColor.valueOf(dyeColor).getColor();
            java.awt.Color HueColor = new java.awt.Color(RGBcolor.getRed(), RGBcolor.getGreen(), RGBcolor.getBlue());
            if(inputBlock.getColor() != null) {
                if (HueColor.getRGB() == inputBlock.getColor().getRGB()) return;
            }
            inputBlock.setColor(HueColor);
            State lightState = State.builder().color(HueColor).keepCurrentState();
            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> hue.startAction(HueAction.STATE, hue.getLights(inputBlock), lightState));
        } else {
            if(pl.cfg.getConfig().getBoolean("white-light-when-no-glass")) {
                State lightState = State.builder().color(java.awt.Color.WHITE).keepCurrentState();
                inputBlock.setColor(java.awt.Color.WHITE);
                Bukkit.getScheduler().runTaskAsynchronously(pl, () -> hue.startAction(HueAction.STATE, hue.getLights(inputBlock), lightState));
            }
        }
    }

    @EventHandler
    public void onRedstoneChange(BlockRedstoneEvent e) {
        if (hue == null) return;
        if (e.getBlock().getType().equals(Material.REDSTONE_LAMP)) {
            for (InputBlock inputBlock : pl.inputBlocks) {
                if (inputBlock.getLocation().equals(e.getBlock().getLocation())) {
                    if (inputBlock.getType() == InputType.DIMMER) {
                        Block block = inputBlock.getLocation().getBlock();
                        Block powerableBlock = null;
                        AnaloguePowerable powerable = null;
                        for (BlockFace blockface : BlockFace.values()) {
                            if (block.getRelative(blockface).getBlockData() instanceof AnaloguePowerable) {
                                powerableBlock = block.getRelative(blockface);
                                powerable = (AnaloguePowerable) powerableBlock.getBlockData();
                                break;
                            }
                        }
                        int semiBrightness = 0;
                        if (powerable == null) {
                            semiBrightness = (int) Math.round((double) e.getNewCurrent() / 15d * 100d);
                        } else {
                            semiBrightness = (int) Math.round((double) powerable.getPower() / 15d * 100d);
                        }
                        final int brightness = semiBrightness;
                        if (brightness == 0) {
                            dimmerCombination.remove(powerableBlock);
                        } else {
                            dimmerCombination.putIfAbsent(powerableBlock, inputBlock);
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                            hue.startAction(HueAction.BRIGHTNESS, hue.getLights(inputBlock), brightness);
                            updateColor(inputBlock);
                        });
                        break;


                    } else if (inputBlock.getType() == InputType.SWITCH) {
                        if (e.getNewCurrent() > 0 && e.getOldCurrent() == 0) {
                            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                                hue.startAction(HueAction.TURN_ON, hue.getLights(inputBlock), null);
                                updateColor(inputBlock);
                            });
                        } else if (e.getNewCurrent() == 0 && e.getOldCurrent() > 0) {
                            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                                hue.startAction(HueAction.TURN_OFF, hue.getLights(inputBlock), null);
                                updateColor(inputBlock);
                            });
                        }
                        break;
                    }
                }
            }
        } else if(e.getBlock().getBlockData() instanceof AnaloguePowerable) {
            if(powerlineChange) return;
            if(dimmerCombination.containsKey(e.getBlock())) {
                powerlineChange = true;
                Bukkit.getScheduler().runTaskLater(pl, () -> {
                    InputBlock inputBlock = dimmerCombination.get(e.getBlock());
                    AnaloguePowerable powerable = (AnaloguePowerable) e.getBlock().getBlockData();
                    int brightness = (int) Math.round((double) powerable.getPower() / 15d * 100d);
                    updateColor(inputBlock);
                    Bukkit.getScheduler().runTaskAsynchronously(pl, () -> hue.startAction(HueAction.BRIGHTNESS, hue.getLights(inputBlock), brightness));
                    powerlineChange = false;
                }, 3);

            }
        }
    }
}
