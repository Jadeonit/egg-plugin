package ru.orbital.cannon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public final class Msg {

    private static String prefix = "";

    private Msg() {
    }

    public static void init(String configuredPrefix) {
        prefix = configuredPrefix == null ? "" : configuredPrefix;
    }

    public static void info(CommandSender sender, String text) {
        send(sender, text, NamedTextColor.GRAY);
    }

    public static void ok(CommandSender sender, String text) {
        send(sender, text, NamedTextColor.GREEN);
    }

    public static void warn(CommandSender sender, String text) {
        send(sender, text, NamedTextColor.YELLOW);
    }

    public static void error(CommandSender sender, String text) {
        send(sender, text, NamedTextColor.RED);
    }

    private static void send(CommandSender sender, String text, NamedTextColor color) {
        Component message = prefix.isEmpty()
                ? Component.empty()
                : Component.text(prefix, NamedTextColor.DARK_GRAY);
        sender.sendMessage(message.append(Component.text(text, color)));
    }
}
