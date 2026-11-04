package ch.afterchange.entityBrain.command;

import ch.afterchange.entityBrain.species.SpeciesDefinition;
import ch.afterchange.entityBrain.species.SpeciesRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class SpeciesCommand implements SubCommand {

    private final SpeciesRegistry registry;

    public SpeciesCommand(SpeciesRegistry registry) {
        this.registry = registry;
    }

    @Override public String name() { return "species"; }
    @Override public String permission() { return "entitybrain.command.species"; }
    @Override public String description() { return "List species or show one."; }
    @Override public String usage() { return "[id]"; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name())
                .executes(ctx -> {
                    CommandUtil.reply(ctx, "Species loaded: " + registry.ids().size());
                    for (SpeciesDefinition s : registry.all()) {
                        CommandUtil.reply(ctx, " " + s.id() + " (" + s.displayName()
                                + ") - model " + s.model());
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("id", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            String typed = builder.getRemainingLowerCase();
                            for (String id : registry.ids()) {
                                if (id.startsWith(typed)) builder.suggest(id);
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String id = StringArgumentType.getString(ctx, "id");
                            SpeciesDefinition s = registry.get(id);
                            if (s == null) {
                                CommandUtil.reply(ctx, "Unknown species: " + id);
                                return Command.SINGLE_SUCCESS;
                            }
                            var p = s.personality();
                            var n = s.needs();
                            CommandUtil.reply(ctx, s.id() + " - " + s.displayName());
                            CommandUtil.reply(ctx, " model: " + s.model());
                            CommandUtil.reply(ctx, " personality: aggression=" + p.aggression()
                                    + ", courage=" + p.courage() + ", curiosity=" + p.curiosity());
                            CommandUtil.reply(ctx, " needs: hunger=" + n.hunger()
                                    + ", energy=" + n.energy() + ", fear=" + n.fear());
                            CommandUtil.reply(ctx, " behaviors: " + s.behaviors());
                            CommandUtil.reply(ctx, " animations: " + s.animations());
                            CommandUtil.reply(ctx, " prey: " + s.hunting().prey()
                                    + ", enemies: " + s.hunting().enemies());
                            return Command.SINGLE_SUCCESS;
                        }));
    }
}
