package ch.afterchange.entityBrain.command;

import ch.afterchange.entityBrain.core.BrainRegistryImpl;
import ch.afterchange.entityBrain.core.EntityBrain;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class BrainsCommand implements SubCommand {

    private final BrainRegistryImpl registry;

    public BrainsCommand(BrainRegistryImpl registry) {
        this.registry = registry;
    }

    @Override public String name() { return "brains"; }
    @Override public String permission() { return "entitybrain.command.brains"; }
    @Override public String description() { return "List active brains."; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name())
                .executes(ctx -> {
                    CommandUtil.reply(ctx, "Active brains: " + registry.size());
                    int shown = 0;
                    for (EntityBrain b : registry.snapshot()) {
                        if (shown++ >= 10) {
                            CommandUtil.reply(ctx, " ... and more");
                            break;
                        }
                        CommandUtil.reply(ctx, " " + b.speciesId()
                                + " | brain=" + b.brainId().toString().substring(0, 8)
                                + " | slot=" + b.slot()
                                + " | ticks=" + b.tickCount());
                    }
                    return Command.SINGLE_SUCCESS;
                });
    }
}
