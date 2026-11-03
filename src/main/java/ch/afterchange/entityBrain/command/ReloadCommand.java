package ch.afterchange.entityBrain.command;

import ch.afterchange.entityBrain.util.EBLog;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReloadCommand implements SubCommand {

    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String name() { return "reload"; }
    @Override public String permission() { return "entitybrain.command.reload"; }
    @Override public String description() { return "Reload the configuration."; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name())
                .executes(ctx -> {
                    plugin.reloadConfig();
                    EBLog.setDebug(plugin.getConfig().getBoolean("debug", false));
                    CommandUtil.reply(ctx, "Configuration reloaded.");
                    return Command.SINGLE_SUCCESS;
                });
    }
}
