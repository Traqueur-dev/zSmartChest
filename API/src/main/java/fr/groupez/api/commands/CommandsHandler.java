package fr.groupez.api.commands;

import fr.groupez.api.messaging.Messages;
import fr.traqueur.commands.api.logging.MessageHandler;

public class CommandsHandler implements MessageHandler {
    @Override
    public String getNoPermissionMessage() {
        return Messages.COMMAND_NO_PERMISSION.toString();
    }

    @Override
    public String getOnlyInGameMessage() {
        return Messages.COMMAND_NO_CONSOLE.toString();
    }

    @Override
    public String getMissingArgsMessage() {
        return Messages.COMMAND_SYNTAX_ERROR.toString();
    }

    @Override
    public String getArgNotRecognized() {
        return Messages.COMMAND_NO_ARG.toString();
    }

    @Override
    public String getRequirementMessage() {
        return Messages.NO_REQUIREMENT.toString();
    }
}
