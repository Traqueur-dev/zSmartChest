package fr.traqueur.storageplus.commands.converters;

import fr.traqueur.commands.api.arguments.ArgumentConverter;
import fr.traqueur.commands.api.arguments.TabCompleter;
import fr.traqueur.storageplugs.api.SmartChest;
import fr.traqueur.storageplugs.api.StoragePlusManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SmartChestConverter implements ArgumentConverter<SmartChest>, TabCompleter {

    private final StoragePlusManager manager;

    public SmartChestConverter(StoragePlusManager manager) {
        this.manager = manager;
    }

    @Override
    public SmartChest apply(String s) {
        return this.manager.getSmartChest(s);
    }

    @Override
    public List<String> onCompletion(CommandSender commandSender, List<String> list) {
        return this.manager.getSmartChests().keySet().stream().toList();
    }
}
