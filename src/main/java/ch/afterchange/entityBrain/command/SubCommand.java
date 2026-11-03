package ch.afterchange.entityBrain.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface SubCommand {

    String name();

    String permission();

    String description();

    default String usage() {
        return "";
    }

    LiteralArgumentBuilder<CommandSourceStack> build();
}
