package ch.afterchange.entityBrain.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Banner {

    private static final int HEIGHT = 5;

    private static final String ART = """
             $$$$$$$$\\            $$\\     $$\\   $$\\
             $$  _____|           $$ |    \\__|  $$ |
             $$ |      $$$$$$$\\ $$$$$$\\   $$\\ $$$$$$\\   $$\\   $$\\
             $$$$$\\    $$  __$$\\\\_$$  _|  $$ |\\_$$  _|  $$ |  $$ |
             $$  __|   $$ |  $$ | $$ |    $$ |  $$ |    $$ |  $$ |
             $$ |      $$ |  $$ | $$ |$$\\ $$ |  $$ |$$\\ $$ |  $$ |
             $$$$$$$$\\ $$ |  $$ | \\$$$$  |$$ |  \\$$$$  |\\$$$$$$$ |
             \\________|\\__|  \\__|  \\____/ \\__|   \\____/  \\____$$ |
            $$$$$$$\\                     $$\\            $$\\   $$ |
            $$  __$$\\                    \\__|           \\$$$$$$  |
            $$ |  $$ | $$$$$$\\  $$$$$$\\  $$\\ $$$$$$$\\    \\______/
            $$$$$$$\\ |$$  __$$\\ \\____$$\\ $$ |$$  __$$\\
            $$  __$$\\ $$ |  \\__|$$$$$$$ |$$ |$$ |  $$ |
            $$ |  $$ |$$ |     $$  __$$ |$$ |$$ |  $$ |
            $$$$$$$  |$$ |     \\$$$$$$$ |$$ |$$ |  $$ |
            \\_______/ \\__|      \\_______|\\__|\\__|  \\__|
            """;

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Banner() {}

    public static void print(String version) {
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        console.sendMessage(Component.empty());
        for (String line : ART.split("\n")) {
            console.sendMessage(MM.deserialize(
                    "<gradient:#4ade80:#22d3ee><line></gradient>",
                    Placeholder.unparsed("line", line)));
        }
        console.sendMessage(MM.deserialize(
                " <gray>Creature AI & ecosystem engine <dark_gray>- <gray>v<version>",
                Placeholder.unparsed("version", version)));
        console.sendMessage(Component.empty());
    }
}
