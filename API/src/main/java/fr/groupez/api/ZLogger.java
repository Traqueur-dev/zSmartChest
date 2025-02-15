package fr.groupez.api;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Une classe utilitaire pour la journalisation des messages avec support de formatage de couleur.
 * Cette classe utilise {@link java.util.logging.Logger} pour la journalisation et fournit des méthodes pour
 * enregistrer des messages avec différents niveaux de gravité et des couleurs.
 */
public class ZLogger {

    /**
     * Le logger Java utilisé pour la journalisation des messages.
     */
    private static final java.util.logging.Logger LOGGER;

    /**
     * Le motif de couleur pour les messages.
     */
    private static final String COLOR_PATTERN = "\u001b[38;5;%dm";

    /**
     * Le motif de formatage pour les messages.
     */
    private static final String FORMAT_PATTERN = "\u001b[%dm";

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Le caractère utilisé comme formateur de couleur dans les messages.
     */
    private static final char colorFormatter;

    static {
        LOGGER = JavaPlugin.getPlugin(ZPlugin.class).getLogger();
        colorFormatter = '&';
    }

    public static void info(String message) {
        ZLogger.log(Level.INFO, message);
    }

    public static void success(String message) {
        ZLogger.log(Level.INFO, "&a" + message);
    }

    public static void severe(String message) {
        ZLogger.log(Level.SEVERE, "&c" + message);
    }

    public static void severe(String message, Exception e) {
        ZLogger.log(Level.SEVERE, "&c" + message, e);
    }

    public static void warning(String message) {
        ZLogger.log(Level.WARNING, "&e" + message);}

    /**
     * Méthode pour enregistrer un message avec un niveau de gravité spécifié.
     * @param level Le niveau de gravité du message.
     * @param message Le message à enregistrer.
     */
    public static void log(Level level, String message) {
        LOGGER.log(level, () -> convertStringMessage(message, colorFormatter));
    }

    public static void log(Level level, String message, Exception e) {
        LOGGER.log(level, convertStringMessage(message, colorFormatter), e);
    }

    /**
     * Convertit les codes de couleur dans une chaîne de message en leur équivalent ANSI.
     * @param message La chaîne de message à convertir.
     * @param colorFormatter Le caractère utilisé comme formateur de couleur dans les messages.
     * @return La chaîne de message avec les codes de couleur convertis en ANSI.
     */
    private static String convertStringMessage(String message, char colorFormatter) {
        message = LegacyComponentSerializer.legacyAmpersand().serialize(MINI_MESSAGE.deserialize(message));
        if(message != null && !message.isEmpty()) {
            String messageCopy = String.copyValueOf(message.toCharArray()) + ConsoleColor.RESET.ansiColor;
            Matcher matcher = Pattern.compile(String.format("(%c[0-9a-fk-or])(?!.*\\1)", colorFormatter)).matcher(message);
            while(matcher.find()) {
                String result = matcher.group(1);
                ConsoleColor color = ConsoleColor.getColorByCode(result.charAt(1));
                messageCopy = messageCopy.replace(result, color.getAnsiColor());
            }
            return messageCopy;
        }
        return message;
    }

    /**
     * Une énumération représentant les codes de couleur ANSI avec leur correspondance Bukkit.
     */
    private enum ConsoleColor {

        // Les différentes couleurs et styles ANSI avec leurs codes Bukkit correspondants
        BLACK('0', COLOR_PATTERN, 0),
        DARK_GREEN('2', COLOR_PATTERN, 2),
        DARK_RED('4', COLOR_PATTERN, 1),
        GOLD('6', COLOR_PATTERN, 172),
        DARK_GREY('8', COLOR_PATTERN, 8),
        GREEN('a', COLOR_PATTERN, 10),
        RED('c', COLOR_PATTERN, 9),
        YELLOW('e', COLOR_PATTERN, 11),
        DARK_BLUE('1', COLOR_PATTERN, 4),
        DARK_AQUA('3', COLOR_PATTERN, 30),
        DARK_PURPLE('5', COLOR_PATTERN, 54),
        GRAY('7', COLOR_PATTERN, 246),
        BLUE('9', COLOR_PATTERN, 4),
        AQUA('b', COLOR_PATTERN, 51),
        LIGHT_PURPLE('d', COLOR_PATTERN, 13),
        WHITE('f', COLOR_PATTERN, 15),
        STRIKETHROUGH('m', FORMAT_PATTERN, 9),
        ITALIC('o', FORMAT_PATTERN, 3),
        BOLD('l', FORMAT_PATTERN, 1),
        UNDERLINE('n', FORMAT_PATTERN, 4),
        RESET('r', FORMAT_PATTERN, 0);

        /**
         * Le code de couleur Bukkit correspondant à la couleur ANSI.
         */
        private final char bukkitColor;

        /**
         * Le motif de couleur ANSI pour cette couleur.
         */
        private final String ansiColor;

        /**
         * Constructeur de l'énumération ConsoleColor.
         * @param bukkitColor Le code de couleur Bukkit.
         * @param pattern Le motif de couleur ANSI.
         * @param ansiCode Le code ANSI de la couleur.
         */
        ConsoleColor(char bukkitColor, String pattern, int ansiCode) {
            this.bukkitColor = bukkitColor;
            this.ansiColor = String.format(pattern, ansiCode);
        }

        /**
         * Obtient la couleur ANSI correspondant au code spécifié.
         * @param code Le code de couleur.
         * @return La couleur ANSI correspondante.
         */
        public static ConsoleColor getColorByCode(char code) {
            // Parcourir les couleurs
            for(ConsoleColor color: values()) {
                // Vérifier le code
                if(color.bukkitColor == code) return color;
            }
            // Retourner null si non trouvé
            throw new IllegalArgumentException("Color with code " + code + " does not exists");
        }

        /**
         * Obtient le motif de couleur ANSI de cette couleur.
         * @return Le motif de couleur ANSI.
         */
        public String getAnsiColor() {
            return ansiColor;
        }
    }
}