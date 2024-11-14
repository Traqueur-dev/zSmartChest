package fr.groupez.api.commands;

import fr.groupez.api.messaging.Message;
import fr.traqueur.commands.api.logging.MessageHandler;

public class CommandsHandler implements MessageHandler {
    @Override
    public String getNoPermissionMessage() {
        return Message.COMMAND_NO_PERMISSION.getMessage();
    }

    @Override
    public String getOnlyInGameMessage() {
        return Message.COMMAND_NO_CONSOLE.getMessage();
    }

    @Override
    public String getMissingArgsMessage() {
        return Message.COMMAND_SYNTAX_ERROR.getMessage();
    }

    @Override
    public String getArgNotRecognized() {
        return Message.ARG_NOT_RECOGNIZED.getMessage();
    }

    @Override
    public String getRequirementMessage() {
        return Message.NO_REQUIREMENT.getMessage();
    }
}
