package fr.groupez.api.messaging;

import fr.groupez.api.ZPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.function.Function;

public class Formatter {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final String pattern;
    private final Function<ZPlugin, String> supplier;

    private Formatter(String pattern, Object supplier) {
        this.pattern = pattern;
        this.supplier = (api) -> supplier.toString();
    }

    private Formatter(String pattern, Function<ZPlugin, String> supplier) {
        this.pattern = pattern;
        this.supplier = supplier;
    }

    public static Formatter format(String pattern, Object supplier) {
        return new Formatter(pattern, supplier);
    }

    public static Formatter format(String pattern, Function<ZPlugin, String> supplier) {
        return new Formatter(pattern, supplier);
    }

    public String handle(ZPlugin api, String message) {
        String content = this.supplier.apply(api);
        content = LegacyComponentSerializer.legacyAmpersand().serialize(MINI_MESSAGE.deserialize(content));
        return message.replaceAll(this.pattern, content);
    }
}