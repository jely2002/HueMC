package com.jelleglebbeek.huemc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.event.Listener;

public class Commands implements CommandExecutor, Listener {

    private Main pl;
    ConversationFactory factory;

    public Commands(Main pl) {
        this.pl = pl;
        this.factory = new ConversationFactory(pl);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {
        if(!(command.getName().equalsIgnoreCase("huemc") || command.getName().equalsIgnoreCase("hmc"))) return false;
        if(args.length == 0) {
            help(commandSender);
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("setup")) {
            if(commandSender instanceof Conversable) {
                new Setup(pl, (Conversable) commandSender);
            } else {
                commandSender.sendMessage("This command can't be executed from here.");
            }
        }
        return true;
    }

    private void help(CommandSender sender) {
        sender.sendMessage("This is where help is gonna come");
    }

}
