package ch.afterchange.entityBrain.command;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class CommandUtil {

    private CommandUtil() {}

    public static Player requirePlayer(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getExecutor() instanceof  Player p) {
            return p;
        }
        ctx.getSource().getSender().sendMessage(Component.text("This command is for players only."));
        return null;
    }

    public static void reply(CommandContext<CommandSourceStack> ctx, String message) {
        ctx.getSource().getSender().sendMessage(Component.text(message));
    }
}
