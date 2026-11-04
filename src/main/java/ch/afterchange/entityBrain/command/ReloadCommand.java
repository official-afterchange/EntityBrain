package ch.afterchange.entityBrain.command;

import ch.afterchange.entityBrain.species.SpeciesRegistry;
import ch.afterchange.entityBrain.util.EBLog;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReloadCommand implements SubCommand {

    private final JavaPlugin plugin;
    private final SpeciesRegistry speciesRegistry;

    public ReloadCommand(JavaPlugin plugin, SpeciesRegistry speciesRegistry) {
        this.plugin = plugin;
        this.speciesRegistry = speciesRegistry;
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

                    SpeciesRegistry.ReloadResult r = speciesRegistry.reload();
                    String message = "Reloaded: " + r.loaded() + " species loaded";
                    if (r.failed() > 0) {
                        message += ", " + r.failed() + " failed (" + r.kept()
                                + " kept at previous version), see console";
                    }

                    CommandUtil.reply(ctx, message + ".");
                    return Command.SINGLE_SUCCESS;
                });
    }
}
