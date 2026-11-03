package ch.afterchange.entityBrain.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public final class HelpMessage {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final String LINE = "<dark_gray>" + "-".repeat(36);

    private HelpMessage() {}

    public static void send(CommandSender sender, String version, Collection<SubCommand> subCommands) {
        sender.sendMessage(MM.deserialize(LINE));
        sender.sendMessage(MM.deserialize(
                " <gradient:#4ade80:#22d3ee><bold>EntityBrain</bold></gradient> <dark_gray>v<version>",
                Placeholder.unparsed("version", version)));
        sender.sendMessage(MM.deserialize(LINE));

        boolean any = false;
        for (SubCommand sub : subCommands) {
            if (!sender.hasPermission(sub.permission())) continue;
            any = true;

            String usage = sub.usage().isEmpty() ? "" : " " + sub.usage();
            sender.sendMessage(MM.deserialize(
                    " <click:suggest_command:'<cmd> '>"
                            + "<hover:show_text:'<gray>Click to fill in the chat'>"
                            + "<green><cmd></green><yellow><usage></yellow> "
                            + "<dark_gray>-</dark_gray> <gray><desc></gray>"
                            + "</hover></click>",
                    Placeholder.parsed("cmd", "/eb " + sub.name()),
                    Placeholder.unparsed("usage", usage),
                    Placeholder.unparsed("desc", sub.description())));
        }

        if (!any) {
            sender.sendMessage(MM.deserialize(" <gray>No command available for you."));
        }
        sender.sendMessage(MM.deserialize(LINE));
    }
}
