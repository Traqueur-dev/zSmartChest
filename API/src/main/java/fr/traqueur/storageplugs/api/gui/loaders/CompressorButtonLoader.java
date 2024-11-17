package fr.traqueur.storageplugs.api.gui.loaders;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplugs.api.gui.buttons.ZCompressorButton;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CompressorButtonLoader implements ButtonLoader {

    private final StoragePlusPlugin plugin;
    private final String name;

    public CompressorButtonLoader(StoragePlusPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    @Override
    public Class<? extends Button> getButton() {
        return ZCompressorButton.class;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Button load(YamlConfiguration yamlConfiguration, String s, DefaultButtonValue defaultButtonValue) {
        List<Material> availableMaterials = yamlConfiguration.getStringList(s + "authorized-materials")
                .stream()
                .map(Material::valueOf)
                .toList();
        return new ZCompressorButton(this.plugin, availableMaterials);
    }
}
