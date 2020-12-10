package com.jelleglebbeek.huemc;

import com.jelleglebbeek.huemc.prompts.AddInput;
import com.jelleglebbeek.huemc.prompts.Setup;
import com.jelleglebbeek.huemc.prompts.StandardPrefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!interactList.containsKey(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "Block selection canceled.");
            interactList.remove(e.getPlayer().getUniqueId());
        } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getClickedBlock().getType().isSolid()) {
                if (interactList.get(e.getPlayer().getUniqueId())) {
                    new AddInput(pl, e.getPlayer());
                    //TODO send block instance or smth
                    interactList.remove(e.getPlayer().getUniqueId());
                } else {
                    //TODO remove input
                }
            } else {
                e.getPlayer().sendRawMessage(StandardPrefix.prefix() + "An input block must be solid, try again.");
            }
        }


    }

    private void help(CommandSender sender, String type) {
        sender.sendMessage("This is where help is gonna come");
    }

}
