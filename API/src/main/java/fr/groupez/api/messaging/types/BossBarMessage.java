package fr.groupez.api.messaging.types;

import fr.groupez.api.ZLogger;
import fr.groupez.api.ZPlugin;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.placeholder.Placeholders;
import fr.groupez.api.zcore.BossBarAnimation;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record BossBarMessage(String text, String color, String overlay, List<String> flags, long duration,
                             boolean isStatic) implements ZMessage {

    @Override
    public MessageType messageType() {
        return MessageType.BOSSBAR;
    }

    @Override
    public void send(CommandSender sender, Formatter... formatters) {
        if (sender instanceof Player player) {
            String message = getMessage(text);
            for (Formatter formatter : formatters) {
                message = formatter.handle(JavaPlugin.getPlugin(ZPlugin.class), message);
            }
            BossBar bossBar = BossBar.bossBar(Component.text(Placeholders.parse(player, message)), 1, getColor(), getOverlay(), getFlags());
            new BossBarAnimation(JavaPlugin.getPlugin(ZPlugin.class), player, bossBar, this.duration());
        }
    }

    public boolean isValid() {

        try {
            BossBar.Color.valueOf(color);
        } catch (Exception ignored) {
            ZLogger.severe("BossBar Color " + color + " doesn't exit !");
            return false;
        }

        try {
            BossBar.Overlay.valueOf(overlay);
        } catch (Exception ignored) {
            ZLogger.severe("BossBar Overlay " + overlay + " doesn't exit !");
            return false;
        }

        for (String flag : flags) {
            try {
                BossBar.Flag.valueOf(flag);
            } catch (Exception ignored) {
                ZLogger.severe("BossBar Flag " + flag + " doesn't exit !");
                return false;
            }
        }

        return true;
    }

    public BossBar.Overlay getOverlay() {
        return BossBar.Overlay.valueOf(this.overlay);
    }

    public BossBar.Color getColor() {
        return BossBar.Color.valueOf(this.color);
    }

    public Set<BossBar.Flag> getFlags() {
        return this.flags.stream().map(BossBar.Flag::valueOf).collect(Collectors.toSet());
    }
}