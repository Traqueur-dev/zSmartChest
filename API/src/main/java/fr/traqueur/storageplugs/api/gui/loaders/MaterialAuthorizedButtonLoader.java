package fr.traqueur.storageplugs.api.gui.loaders;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.traqueur.storageplugs.api.StoragePlusPlugin;
import fr.traqueur.storageplugs.api.gui.buttons.MaterialAuthorizedButton;
import fr.traqueur.storageplugs.api.gui.buttons.ZCompressorButton;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MaterialAuthorizedButtonLoader implements ButtonLoader {

    private final StoragePlusPlugin plugin;
    private final String name;
    private final Class<? extends MaterialAuthorizedButton> clazz;

    public MaterialAuthorizedButtonLoader(StoragePlusPlugin plugin, Class<? extends MaterialAuthorizedButton> clazz, String name) {
        this.plugin = plugin;
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public Class<? extends Button> getButton() {
        return this.clazz;
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

        try {
            return this.clazz.getConstructor(StoragePlusPlugin.class, List.class).newInstance(this.plugin, availableMaterials);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
