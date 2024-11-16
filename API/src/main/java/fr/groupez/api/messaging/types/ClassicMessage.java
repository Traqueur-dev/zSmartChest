package fr.groupez.api.messaging.types;

import fr.groupez.api.ZPlugin;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.placeholder.Placeholders;
import fr.groupez.api.zcore.DefaultFontInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

    public static ZMessage center(String... strings) {
        return new ClassicMessage(MessageType.CENTER, Arrays.asList(strings));
    }

    @Override
    public void send(CommandSender sender, Formatter... formatters) {
        for (String message : messages) {
            message = this.getMessage(message);
            for (Formatter formatter : formatters) {
                message = formatter.handle(JavaPlugin.getPlugin(ZPlugin.class), message);
            }
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
                if(messageType == MessageType.CENTER) {
                    message = getCenteredMessage(message);
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
    }

    private String getCenteredMessage(String message) {
        if (message == null || message.equals("")) return "";

        int CENTER_PX = 154;

        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }


}