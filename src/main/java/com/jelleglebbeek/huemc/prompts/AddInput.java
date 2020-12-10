package com.jelleglebbeek.huemc.prompts;

import com.jelleglebbeek.huemc.Main;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;

import java.util.ArrayList;

public class AddInput {

    private Main pl;

    public AddInput(Main pl, Conversable conversable) {
        this.pl = pl;
        Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
            ConversationFactory conversationFactory = new ConversationFactory(pl)
                    .withModality(true)
                    .withPrefix(new StandardPrefix())
                    .withFirstPrompt(new InfoPrompt())
                    .withEscapeSequence("stop")
                    .withLocalEcho(false)
                    .thatExcludesNonPlayersWithMessage("Only players can add a trigger to HueMC");
            conversationFactory.buildConversation(conversable).begin();
        });
    }

    private class InfoPrompt extends MessagePrompt {
        @Override
        protected Prompt getNextPrompt(ConversationContext conversationContext) {
            return new SelectTypePrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Adding an input. To exit type 'stop'.";
        }
    }

    private class SelectTypePrompt extends FixedSetPrompt {
        public SelectTypePrompt() {
            super("switch", "dimmer","Switch","Dimmer","SWITCH","DIMMER");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
            conversationContext.setSessionData("type", s);
            return new SelectLinkPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "What kind of input do you want to create?");
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- Switch");
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- Dimmer");
            return "Please type in one of the above types.";
        }
    }

    private class SelectLinkPrompt extends FixedSetPrompt {
        public SelectLinkPrompt() {
            super("room","zone","Room","Zone","ZONE","ROOM");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
            conversationContext.setSessionData("link", s.toLowerCase());
            return new WholeAreaPrompt();
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "Do you want to link this input to a:");
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- Room");
            conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- Zone");
            return "Please type in one of the above types.";
        }
    }

    private class WholeAreaPrompt extends ValidatingPrompt {
        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String input) {
            return input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no")  || input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false");
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String input) {
            if(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true")) {
                conversationContext.setSessionData("whole", true);
            } else {
                conversationContext.setSessionData("whole", false);
            }
            return new SelectAreaPrompt();
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "Your answer must be either 'yes' or 'no'.";
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            return "Do you want to link this input with the whole " + conversationContext.getSessionData("link") + "?";
        }
    }

    private class SelectAreaPrompt extends ValidatingPrompt {

        private ArrayList<String> availableAreas;
        private boolean isDone = false;

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            if (isDone) {
                for (String area : availableAreas) {
                    if (area.equalsIgnoreCase(s)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
            if((boolean) conversationContext.getSessionData("whole")) {
                //TODO attempt to create the input block with this whole area
                return Prompt.END_OF_CONVERSATION;
            } else {
                for(String area : availableAreas) {
                    if(area.equalsIgnoreCase(s)) {
                        conversationContext.setSessionData("area", area);
                    }
                }
                return new SelectLightPrompt();
            }
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The " + context.getSessionData("link") + " " + invalidInput + " can't be found.";
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            if(conversationContext.getSessionData("link").equals("room")) {
                Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                    ArrayList<String> roomNames = pl.hue.getRooms();
                    availableAreas = roomNames;
                    conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "The following rooms were found:");
                    for(String roomName : roomNames) {
                        conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- " + roomName);
                    }
                    isDone = true;
                });
                return "Please type in the name of a room.";
            } else if(conversationContext.getSessionData("link").equals("zone")) {
                Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                    ArrayList<String> zoneNames = pl.hue.getZones();
                    availableAreas = zoneNames;
                    conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "The following zones were found:");
                    for(String zoneName : zoneNames) {
                        conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- " + zoneName);
                    }
                    isDone = true;
                });
                return "Please type in the name of a zone.";
            }
            return "Something went wrong..";
        }
    }

    private class SelectLightPrompt extends ValidatingPrompt {

        private ArrayList<String> availableLights;
        private boolean isDone = false;

        @Override
        protected boolean isInputValid(ConversationContext conversationContext, String s) {
            if (isDone) {
                for (String light : availableLights) {
                    if (light.equalsIgnoreCase(s)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
            //TODO Create the input with the specified light.
            String lightName;
            for(String light : availableLights) {
                if(light.equalsIgnoreCase(s)) {
                    lightName = light;
                }
            }
            return Prompt.END_OF_CONVERSATION;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "The light " + invalidInput + " can't be found.";
        }

        @Override
        public String getPromptText(ConversationContext conversationContext) {
            Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
                ArrayList<String> lightNames = pl.hue.getLights((String) conversationContext.getSessionData("area"), false);
                availableLights = lightNames;
                conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "The following lights were found:");
                for(String lightName : lightNames) {
                    conversationContext.getForWhom().sendRawMessage(StandardPrefix.prefix() + "- " + lightName);
                }
                isDone = true;
            });
            return "Please type in the name of a light.";
        }
    }
}
