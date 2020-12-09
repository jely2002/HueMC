package com.jelleglebbeek.huemc;

import org.bukkit.conversations.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Setup {

    public Setup(JavaPlugin pl, Conversable conversable) {
        ConversationFactory conversationFactory = new ConversationFactory(pl)
                .withModality(true)
                .withPrefix(new PluginNameConversationPrefix(pl)) //TODO create own prefix
                .withFirstPrompt(new InfoPrompt())
                .withEscapeSequence("stop")
                .withLocalEcho(false)
                .thatExcludesNonPlayersWithMessage("Only players can setup HueMC");
        conversationFactory.buildConversation(conversable).begin();
        //TODO add bye bye message when player stops the setup.
    }

    private class InfoPrompt extends MessagePrompt {
        @Override
        protected Prompt getNextPrompt(ConversationContext conversationContext) {
            return new EnterIPPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Welcome to HueMC setup! To exit type 'stop'.";
        }
    }

    private class EnterIPPrompt extends ValidatingPrompt {
        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Please enter the IP adress of your bridge:";
        }

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String input) {
            String ipv4Pattern = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
            return input.matches(ipv4Pattern);
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return invalidInput + " is not a valid IP address.";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String input) {
            conversationContext.setSessionData("ip", input);
            return new HasApiKeyPrompt();
        }
    }

    private class HasApiKeyPrompt extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String input) {
            return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no")  || input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String input) {
            if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true")) {
                return new EnterApiKeyPrompt();
            } else {
                //TODO start connect and ask user to press on the bridge button.
                conversationContext.getForWhom().sendRawMessage("Press the button on your Hue Bridge to finish connecting.");
                return Prompt.END_OF_CONVERSATION;
            }
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "Your answer must be either 'yes' or 'no'.";
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Do you have an API key for your bridge?";
        }
    }

    private class EnterApiKeyPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Please enter your API key:";
        }

        @Override
        public Prompt acceptInput(ConversationContext conversationContext, String input) {
            //TODO Start connection with API key
            conversationContext.setSessionData("apiKey", input);
            conversationContext.getForWhom().sendRawMessage("Connecting to your Hue Bridge via API key.");
            return Prompt.END_OF_CONVERSATION;
        }


    }
}
