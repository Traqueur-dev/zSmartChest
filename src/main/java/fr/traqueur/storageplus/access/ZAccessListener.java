package fr.traqueur.storageplus.access;

import fr.groupez.api.MainConfiguration;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.messaging.Formatter;
import fr.groupez.api.messaging.Messages;
import fr.traqueur.storageplus.api.access.AccessManager;
import fr.traqueur.storageplus.api.domains.AccessChest;
import fr.traqueur.storageplus.domains.ZAccessChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ZAccessListener implements Listener {

    private final AccessManager accessManager;

    public ZAccessListener(AccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @EventHandler
    public void onPlayerWrite(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!this.accessManager.isPending(player.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        String message = event.getMessage();
        if (Configuration.get(MainConfiguration.class).getAccessManagingCancelWords().contains(message.toLowerCase())) {
            this.accessManager.removePending(player.getUniqueId());
            Messages.ACCESS_CANCELLED.send(player);
            return;
        }

        Player target = Bukkit.getPlayer(message);
        if(target == null) {
            Messages.PLAYER_NOT_FOUND.send(player);
            return;
        }
        this.accessManager.addAccess(new ZAccessChest(UUID.randomUUID(), this.accessManager.getPending(player.getUniqueId()), target.getUniqueId()));
        this.accessManager.removePending(player.getUniqueId());
        Messages.ACCESS_GRANTED.send(player, Formatter.format("%target%",target.getName()));
    }

}
