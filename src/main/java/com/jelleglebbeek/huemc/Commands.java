package com.jelleglebbeek.huemc;

import com.jelleglebbeek.huemc.prompts.AddInput;
import com.jelleglebbeek.huemc.prompts.Setup;
import com.jelleglebbeek.huemc.prompts.StandardPrefix;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.UUID;

public class Commands implements CommandExecutor, Listener {

    private Main pl;
    private HashMap<UUID, Boolean> interactList = new HashMap();
    ConversationFactory factory;

    public Commands(Main pl) {
        this.pl = pl;
        this.factory = new ConversationFactory(pl);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {
        if(!(command.getName().equalsIgnoreCase("huemc") || command.getName().equalsIgnoreCase("hmc"))) return false;
        if(args.length == 0) {
            help(commandSender, "main");
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("setup")) {
            if(commandSender instanceof Conversable) {
                new Setup(pl, (Conversable) commandSender);
            } else {
                commandSender.sendMessage("This command can't be executed from here.");
            }
        } else if(args[0].equalsIgnoreCase("input")) {
            if(pl.hue == null) {
                commandSender.sendMessage(StandardPrefix.prefix() + "Please setup HueMC with /hmc setup first.");
                return true;
            }
            if(args.length == 1) {
                help(commandSender, "input");
                return true;
            }
            if(commandSender instanceof Player) {
                Player p = (Player) commandSender;
                if (args.length == 2 && args[1].equalsIgnoreCase("add")) {
                    interactList.put(p.getUniqueId(), true);
                    p.sendRawMessage(StandardPrefix.prefix() + "Right click on a block to add an input.");
                    p.sendRawMessage(StandardPrefix.prefix() + "Left click to cancel.");
                } else if (args.length == 2 && args[1].equalsIgnoreCase("remove")) {
                    if(interactList.containsKey(p.getUniqueId())) {
                        p.sendRawMessage(StandardPrefix.prefix() + "");
                    } else {
                        p.sendRawMessage(StandardPrefix.prefix() + "Right click on a block to remove the input.");
                        p.sendRawMessage(StandardPrefix.prefix() + "Left click to cancel.");
                        interactList.put(p.getUniqueId(), false);
                    }
                } else if(args.length == 2 && args[1].equalsIgnoreCase("list")) {
                    int index = 1;
                    for(InputBlock inputBlock : pl.inputBlocks) {
                        p.sendRawMessage(StandardPrefix.prefix() + "+------- " + index + " -------+");
                        p.sendRawMessage(StandardPrefix.prefix() + "Location: " + inputBlock.getLocation().getBlockX() + ", " + inputBlock.getLocation().getBlockY() + ", " + inputBlock.getLocation().getBlockZ());
                        p.sendRawMessage(StandardPrefix.prefix() + "Type: " + inputBlock.getType().toString());
                        p.sendRawMessage(StandardPrefix.prefix() + "Area name: " + inputBlock.getAreaName());
                        p.sendRawMessage(StandardPrefix.prefix() + "Light name: " + ((inputBlock.getLightName() == null) ? "N/A" : inputBlock.getLightName()));
                        index++;
                        if(index == 1) {
                            p.sendRawMessage(StandardPrefix.prefix() + "No inputs detected at this time.");
                        }
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getHand() == EquipmentSlot.OFF_HAND) return;
        if(!interactList.containsKey(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "Block selection canceled.");
            interactList.remove(e.getPlayer().getUniqueId());
        } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null) {
                if (e.getClickedBlock().getType() == Material.REDSTONE_LAMP) {
                    if (interactList.get(e.getPlayer().getUniqueId())) {
                        boolean isDupe = false;
                        for(InputBlock inputBlock : pl.inputBlocks) {
                            if(inputBlock.getLocation().equals(e.getClickedBlock().getLocation())) {
                                isDupe = true;
                            }
                        }
                        if(!isDupe) {
                            new AddInput(pl, e.getPlayer(), e.getClickedBlock());
                            interactList.remove(e.getPlayer().getUniqueId());
                        } else {
                            e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "This block already has an input, try again.");
                        }
                    } else {
                        for(InputBlock inputBlock : pl.inputBlocks) {
                            if(inputBlock.getLocation().equals(e.getClickedBlock().getLocation())) {
                                pl.inputBlocks.remove(inputBlock);
                                pl.cfg.removeInputBlock(inputBlock);
                                interactList.remove(e.getPlayer().getUniqueId());
                                e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "Input removed.");
                                return;
                            }
                        }
                        e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "This block does not have an input, try again");
                    }
                } else {
                    e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "An input block must be of type 'redstone lamp', try again.");
                }
            } else {
                e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "Something went wrong, try again.");
            }
        }


    }

    private void help(CommandSender sender, String type) {
        sender.sendMessage("This is where help is gonna come");
    }

}
