package com.jelleglebbeek.huemc.prompts;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

public class StandardPrefix implements ConversationPrefix {
    @Override
    public String getPrefix(ConversationContext conversationContext) {
        String customPrefix = (String) conversationContext.getSessionData("prefix");
        if(customPrefix == null) {
            return ChatColor.GOLD + "[" + ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "HueMC" + ChatColor.RESET + "" + ChatColor.GOLD + "] ";
        } else {
            return ChatColor.GOLD + "[" + ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + customPrefix + ChatColor.RESET + "" + ChatColor.GOLD + "] ";
        }
    }

    public static String prefix() {
        return ChatColor.GOLD + "[" + ChatColor.RESET + "" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "HueMC" + ChatColor.RESET + "" + ChatColor.GOLD + "] ";
    }
}
