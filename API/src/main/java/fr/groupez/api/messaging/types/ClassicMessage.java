package fr.groupez.api.messaging.types;

import fr.groupez.api.messaging.Messages;
import fr.groupez.api.placeholder.Placeholders;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record ClassicMessage(MessageType messageType, List<String> messages) implements ZMessage {

    public static ZMessage tchat(String... strings) {
        return new ClassicMessage(MessageType.TCHAT, Arrays.asList(strings));
    }

    public static ZMessage action(String strings) {
        return new ClassicMessage(MessageType.ACTION, Collections.singletonList(strings));
    }

    public static ZMessage withoutPrefix(String... strings) {
        return new ClassicMessage(MessageType.WITHOUT_PREFIX, Arrays.asList(strings));
    }

    @Override
    public void send(CommandSender sender) {
        for (String message : messages) {
            message = this.getMessage(message);
            if (messageType == MessageType.ACTION) {
                if (sender instanceof Player player) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(Placeholders.parse(player, message)));
                } else {
                    sender.sendMessage(message);
                }
            } else {
                if (sender instanceof Player player) {
                    message = Placeholders.parse(player, message);
                } else {
                    message = Placeholders.parse(null, message);
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }


}