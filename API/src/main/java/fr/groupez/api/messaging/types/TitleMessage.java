package fr.groupez.api.messaging.types;

import fr.groupez.api.placeholder.Placeholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record TitleMessage(String title, String subtitle, long start, long time,
                           long end) implements ZMessage {

    @Override
    public MessageType messageType() {
        return MessageType.TITLE;
    }

    @Override
    public void send(CommandSender sender) {
       if (sender instanceof Player player) {
            player.sendTitle(Placeholders.parse(player, getMessage(title)), Placeholders.parse(player, subtitle), (int) start, (int) time, (int) end);
       } else {
           sender.sendMessage(Placeholders.parse(null, getMessage(title)) + "\n" + Placeholders.parse(null, subtitle));
       }
    }
}