package fr.groupez.api.messaging.types;

import fr.groupez.api.ZPlugin;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.placeholder.Placeholders;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public record TitleMessage(String title, String subtitle, long start, long time,
                           long end) implements ZMessage {

    @Override
    public MessageType messageType() {
        return MessageType.TITLE;
    }

    @Override
    public void send(CommandSender sender, Formatter... formatters) {
        String title = getMessage(this.title);
        String subtitle = getMessage(this.subtitle);

        for (Formatter formatter : formatters) {
            title = formatter.handle(JavaPlugin.getPlugin(ZPlugin.class), title);
            subtitle = formatter.handle(JavaPlugin.getPlugin(ZPlugin.class), subtitle);
        }

        if (sender instanceof Player player) {
            player.sendTitle(Placeholders.parse(player, title), Placeholders.parse(player, subtitle), (int) start, (int) time, (int) end);
        } else {
           sender.sendMessage(Placeholders.parse(null, title) + "\n" + Placeholders.parse(null, subtitle));
        }
    }
}