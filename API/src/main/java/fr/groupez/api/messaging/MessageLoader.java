package fr.groupez.api.messaging;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import fr.groupez.api.ZLogger;
import fr.groupez.api.ZPlugin;
import fr.groupez.api.configurations.Configuration;
import fr.groupez.api.configurations.NonLoadable;
import fr.groupez.api.nms.NmsVersion;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageLoader implements Configuration {

    @NonLoadable
    private boolean load;
    @NonLoadable
    private final List<Message> loadedMessages;

    public MessageLoader() {
        this.loadedMessages = new ArrayList<>();
    }

    @Override
    public String getFile() {
        return "messages.yml";
    }

    public YamlDocument saveIfNotExists() {
        File file = new File(JavaPlugin.getPlugin(ZPlugin.class).getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (created) {
                    ZLogger.info("The messages.yml file has been created.");
                    return this.save(file);
                }
                throw new IOException("The messages.yml file could not be created.");
            } catch (IOException exception) {
                ZLogger.severe("An error occurred while creating the messages.yml file.", exception);
                throw new RuntimeException(exception);
            }
        }
        try {
            return YamlDocument.create(new File(JavaPlugin.getPlugin(ZPlugin.class).getDataFolder(), this.getFile()), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private YamlDocument save(File file) {
        YamlDocument configuration;
        try {
            configuration = YamlDocument.create(new File(JavaPlugin.getPlugin(ZPlugin.class).getDataFolder(), this.getFile()), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Message message : Message.values()) {
            String path = message.name().toLowerCase().replace("_", "-");

            if (configuration.contains(path)) continue;

            if (message.getType() != MessageType.TCHAT) {
                configuration.set(path + ".type", message.getType().name());
            }
            if (message.getType().equals(MessageType.TCHAT) || message.getType().equals(MessageType.ACTION) || message.getType().equals(MessageType.CENTER)) {

                if (message.isMultipart()) {
                    if (message.getType() != MessageType.TCHAT) {
                        configuration.set(path + ".messages", colorReverse(message.getMessages()));
                    } else {
                        configuration.set(path, colorReverse(message.getMessages()));
                    }
                } else {
                    if (message.getType() != MessageType.TCHAT) {
                        configuration.set(path + ".message", colorReverse(message.getMessage()));
                    } else {
                        configuration.set(path, colorReverse(message.getMessage()));
                    }
                }
            } else if (message.getType().equals(MessageType.TITLE)) {

                configuration.set(path + ".title", colorReverse(message.getTitle()));
                configuration.set(path + ".subtitle", colorReverse(message.getSubTitle()));
                configuration.set(path + ".fadeInTime", message.getStart());
                configuration.set(path + ".showTime", message.getTime());
                configuration.set(path + ".fadeOutTime", message.getEnd());
            }
        }

        try {
            configuration.save(file);
        } catch (IOException e) {
            ZLogger.severe("An error occurred while saving the messages.yml file.", e);
            throw new RuntimeException(e);
        }
        return configuration;
    }

    @Override
    public void loadConfig() {
        YamlDocument config = this.saveIfNotExists();
        this.loadedMessages.clear();

        config.getKeys().forEach(key -> this.loadMessage(config, key.toString()));

        boolean canSave = false;
        for (Message message : Message.values()) {
            if (!this.loadedMessages.contains(message)) {
                canSave = true;
                break;
            }
        }

        if (canSave) {
            ZLogger.info("Save the message file, add new settings");
            try {
                config.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.load = true;
    }

    @Override
    public boolean isLoad() {
        return load;
    }

    /**
     * Loads a single message from the given YAML configuration.
     *
     * @param configuration The YAML configuration to load the message from.
     * @param key           The key under which the message is stored.
     */
    private void loadMessage(YamlDocument configuration, String key) {
        try {

            Message message = Message.valueOf(key.toUpperCase().replace("-", "_"));

            if (configuration.contains(key + ".type")) {

                MessageType messageType = MessageType.valueOf(configuration.getString(key + ".type", "TCHAT").toUpperCase());
                message.setType(messageType);
                switch (messageType) {
                    case ACTION:
                    case TCHAT_AND_ACTION: {
                        message.setMessage(configuration.getString(key + ".message"));
                        break;
                    }
                    case CENTER:
                    case TCHAT:
                    case WITHOUT_PREFIX: {
                        List<String> messages = configuration.getStringList(key + ".messages");
                        if (messages.isEmpty()) {
                            message.setMessage(configuration.getString(key + ".message"));
                        } else message.setMessages(messages);
                        break;
                    }
                    case TITLE: {
                        String title = configuration.getString(key + ".title");
                        String subtitle = configuration.getString(key + ".subtitle");
                        int fadeInTime = configuration.getInt(key + ".fadeInTime");
                        int showTime = configuration.getInt(key + ".showTime");
                        int fadeOutTime = configuration.getInt(key + ".fadeOutTime");
                        Map<String, Object> titles = new HashMap<>();
                        titles.put("title", color(title));
                        titles.put("subtitle", color(subtitle));
                        titles.put("start", fadeInTime);
                        titles.put("time", showTime);
                        titles.put("end", fadeOutTime);
                        titles.put("isUse", true);
                        message.setTitles(titles);
                        break;
                    }
                }

            } else {
                message.setType(MessageType.TCHAT);
                List<String> messages = configuration.getStringList(key);
                if (messages.isEmpty()) {
                    message.setMessage(configuration.getString(key));
                } else message.setMessages(messages);
            }

            this.loadedMessages.add(message);
        } catch (Exception ignored) {
        }
    }

    protected String colorReverse(String message) {
        Pattern pattern = Pattern.compile(net.md_5.bungee.api.ChatColor.COLOR_CHAR + "x[a-fA-F0-9-" + net.md_5.bungee.api.ChatColor.COLOR_CHAR + "]{12}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            String colorReplace = color.replace("§x", "#");
            colorReplace = colorReplace.replace("§", "");
            message = message.replace(color, colorReplace);
            matcher = pattern.matcher(message);
        }

        return message.replace("§", "&");
    }

    protected List<String> colorReverse(List<String> messages) {
        return messages.stream().map(this::colorReverse).collect(Collectors.toList());
    }

    protected String color(String message) {
        if (message == null) {
            return null;
        }
        if (NmsVersion.nmsVersion.isHexVersion()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
                matcher = pattern.matcher(message);
            }
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }
}
