package fr.groupez.api.messaging;

import fr.groupez.api.messaging.types.*;
import org.bukkit.command.CommandSender;

import java.util.List;

public enum Messages {

    PREFIX("<gray>[<color:#92bed8>ᴢsᴛᴏʀᴀɢᴇᴘʟᴜs<gray>] "),

    TIME_DAY("%02d %day% %02d %hour% %02d %minute% %02d %second%"),
    TIME_HOUR("%02d %hour% %02d minute(s) %02d %second%"),
    TIME_MINUTE("%02d %minute% %02d %second%"),
    TIME_SECOND("%02d %second%"),

    FORMAT_SECOND("second"), FORMAT_SECONDS("seconds"),

    FORMAT_MINUTE("minute"), FORMAT_MINUTES("minutes"),

    FORMAT_HOUR("hour"), FORMAT_HOURS("hours"),

    FORMAT_DAY("d"), FORMAT_DAYS("days"),

    COMMAND_SYNTAX_ERROR("&cYou must execute the command like this&7: &a%syntax%"),
    COMMAND_NO_PERMISSION("&cYou do not have permission to run this command."),
    COMMAND_NO_CONSOLE("&cOnly one player can execute this command."),
    COMMAND_NO_ARG("&cImpossible to find the command with its arguments."),
    COMMAND_SYNTAX_HELP(MessageType.WITHOUT_PREFIX, "%syntax% &f» &7%description%"),
    NO_REQUIREMENT("&cYou do not have the necessary requirements to execute this command."),

    RELOAD("&aYou have just reloaded the configuration files."),
    DESCRIPTION_RELOAD("Reload configuration files"),

    DESCRIPTION_GIVE_COMMAND("Give a chest to a player."),

    NOT_ENOUGH_MONEY("You do not have enough money to do this."),
    DESCRIPTION_PURGE("Delete all chests save in chunks around you."),
    PURGE("&aYou have just purged the chests in %amount% chunks"),
    GIVE_CHEST("&aYou have just received the chest &f%chest%&7."),
    SELL_ALL("&aYou have just sold all the items in the chest."),;

    private final ZMessage message;

    Messages(String... messages) {
        this.message = ClassicMessage.tchat(messages);
    }

    Messages(MessageType type, String message) {
        if(type == MessageType.ACTION) {
            this.message = ClassicMessage.action(message);
        } else if (type == MessageType.WITHOUT_PREFIX) {
            this.message = ClassicMessage.withoutPrefix(message);
        } else if (type == MessageType.CENTER) {
            this.message = ClassicMessage.center(message);
        } else {
            throw new UnsupportedOperationException("Unsupported message type for message " + this.name());
        }
    }

    Messages(String title, String subtitle, long start, long time,
             long end) {
        this.message = new TitleMessage(title, subtitle, start, time, end);
    }

    Messages(String text, String color, String overlay, List<String> flags, long duration,
             boolean isStatic) {
        this.message = new BossBarMessage(text, color, overlay, flags, duration, isStatic);
    }

    public void send(CommandSender sender, Formatter... formatters) {
        this.message.send(sender, formatters);
    }

    public String toString() {
        if(this.message instanceof ClassicMessage classicMessage) {
            if(classicMessage.messages().size() == 1) {
                return classicMessage.messages().getFirst();
            }
            return classicMessage.messages().stream().reduce("", (a, b) -> a + "\n" + b);
        }
        throw new UnsupportedOperationException("Unsupported message type for message " + this.name());
    }

}
