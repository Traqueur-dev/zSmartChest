
package fr.groupez.api.messaging.types;

import fr.groupez.api.messaging.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

/**
 * An interface representing an essential message with a specific type.
 */
public interface ZMessage {

    MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Retrieves the type of the message.
     *
     * @return the {@link MessageType} of the message
     */
    MessageType messageType();

    void send(CommandSender sender);

    default String getMessage(String message) {
        message = message.replace("§", "&");
        if(messageType() == MessageType.TCHAT) {
            message = Messages.PREFIX + message;
        }
        var deserialized = miniMessage.deserialize(message);
        return LegacyComponentSerializer.legacyAmpersand().serialize(deserialized);
    }

}
