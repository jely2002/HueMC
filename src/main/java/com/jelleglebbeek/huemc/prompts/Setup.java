package com.jelleglebbeek.huemc.prompts;

import com.jelleglebbeek.huemc.HueAPI;
import com.jelleglebbeek.huemc.Main;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;

import java.util.HashMap;
import java.util.logging.Level;

public class Setup {

    private Main pl;

    public Setup(Main pl, Conversable conversable) {
        this.pl = pl;
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            ConversationFactory conversationFactory = new ConversationFactory(pl)
                    .withModality(true)
                    .withPrefix(new StandardPrefix())
                    .withFirstPrompt(new InfoPrompt())
                    .withEscapeSequence("stop")
                    .withLocalEcho(false)
                    .thatExcludesNonPlayersWithMessage("Only players can setup HueMC");
            conversationFactory.buildConversation(conversable).begin();
        });
        //TODO add bye bye message when player stops the setup.
    }

    private class InfoPrompt extends MessagePrompt {
        @Override
        protected Prompt getNextPrompt(ConversationContext conversationContext) {
            return new BridgePrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            conversationContext.setSessionData("prefix", "HueMC Setup");
            return "Welcome to HueMC setup! To exit type 'stop'.";
        }
    }

    private class BridgePrompt extends MessagePrompt {
        private boolean noBridge = false;
        private boolean oneBridge = false;

        @Override
        protected Prompt getNextPrompt(ConversationContext conversationContext) {
            if(noBridge) {
                return Prompt.END_OF_CONVERSATION;
            } else {
                if(oneBridge) {
                    return new HasApiKeyPrompt();
                } else {
                    return new EnterIPPrompt();
                }
            }
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            conversationContext.getForWhom().sendRawMessage("Auto-detecting Hue Bridges...");
            HashMap<String, String> bridges = HueAPI.discoverBridges();
            if(bridges == null) {
                noBridge = true;
                return "No Hue Bridge detected in this network. Exiting setup...";
            } else {
                oneBridge = bridges.size() == 1;
                conversationContext.getForWhom().sendRawMessage(oneBridge ? "Detected Hue Bridge:" : "Detected Hue Bridges:");
                for(String ip : bridges.keySet()) {
                    conversationContext.getForWhom().sendRawMessage("- " + bridges.get(ip) + " IP: " + ip);
                }
                conversationContext.setSessionData("ip", bridges.keySet().toArray()[0]);
                return "Hue Bridge detection finished";
            }
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
                conversationContext.getForWhom().sendRawMessage("Press the button on your Hue Bridge to finish connecting.");
                String ip = (String) conversationContext.getSessionData("ip");
                Bukkit.getLogger().log(Level.INFO, ip);
                pl.hue = new HueAPI(ip);
                Bukkit.getScheduler().runTaskAsynchronously(pl, () -> pl.hue.connect(conversationContext.getForWhom(), pl.cfg));
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
            String ip = (String) conversationContext.getSessionData("ip");
            pl.hue = new HueAPI(ip, input);
            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> pl.hue.connect(conversationContext.getForWhom(), pl.cfg));
            return Prompt.END_OF_CONVERSATION;
        }


    }
}
