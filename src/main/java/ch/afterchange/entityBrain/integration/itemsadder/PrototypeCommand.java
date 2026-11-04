package ch.afterchange.entityBrain.integration.itemsadder;

import ch.afterchange.entityBrain.command.CommandUtil;
import ch.afterchange.entityBrain.command.SubCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.lone.itemsadder.api.CustomEntity;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class PrototypeCommand implements SubCommand {

    @Override public String name() { return "proto"; }
    @Override public String permission() { return "entitybrain.command.proto"; }
    @Override public String description() { return "Step 1 host prototype (temporary)."; }
    @Override public String usage() { return "<spawn|walk|anim|info>"; }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal(name())
                .then(Commands.literal("spawn")
                        .then(Commands.argument("id", StringArgumentType.greedyString())
                                .suggests((ctx, builder) -> {
                                    String typed = builder.getRemainingLowerCase();
                                    for (String id : CustomEntity.getNamespacedIdsInRegistry()) {
                                        if (id.toLowerCase().startsWith(typed)) {
                                            builder.suggest(id);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    Player p = CommandUtil.requirePlayer(ctx);
                                    if (p != null) {
                                        CommandUtil.reply(ctx, HostPrototype.spawn(p,
                                                StringArgumentType.getString(ctx, "id")));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("walk")
                        .executes(ctx -> {
                            Player p = CommandUtil.requirePlayer(ctx);
                            if (p != null) CommandUtil.reply(ctx, HostPrototype.walkToPlayer(p));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("anim")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(ctx -> {
                                    CommandUtil.reply(ctx, HostPrototype.anim(
                                            StringArgumentType.getString(ctx, "name")));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("info")
                        .executes(ctx -> {
                            CommandUtil.reply(ctx, HostPrototype.info());
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("strip")
                        .executes(ctx -> {
                            CommandUtil.reply(ctx, HostPrototype.stripVanillaAi());
                            return  Command.SINGLE_SUCCESS;
                        }));
    }
}