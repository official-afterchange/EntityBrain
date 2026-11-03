package ch.afterchange.entityBrain.command;

import ch.afterchange.entityBrain.util.EBLog;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CommandManager {

    private static final String ROOT_PERMISSION = "entitybrain.command";
    private static final String WILDCARD_PERMISSION = "entitybrain.command.*";

    private final JavaPlugin plugin;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void add(SubCommand sub) {
        if (subCommands.putIfAbsent(sub.name(), sub) != null) {
            EBLog.warn("Duplicate subcommand ignored" + sub.name());
            return;
        }
        declarePermission(sub.permission(), "Use /eb " + sub.name(), null);
    }

    public void registerAll() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
            event.registrar().register(
                    buildRoot(),
                    "EntityBrain main command",
                    List.of("entitybrain")));
    }

    private LiteralCommandNode<CommandSourceStack> buildRoot() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("eb")
                .requires(src -> src.getSender().hasPermission(ROOT_PERMISSION))
                .executes(ctx -> {
                    sendHelp(ctx.getSource().getSender());
                    return Command.SINGLE_SUCCESS;
                });

        for (SubCommand sub : subCommands.values()) {
            root.then(sub.build()
                    .requires(src -> src.getSender().hasPermission(sub.permission())));
        }
        return root.build();
    }

    private void sendHelp(CommandSender sender) {
        HelpMessage.send(sender, plugin.getPluginMeta().getVersion(), subCommands.values());
    }

    private void declareRootPermission() {
        declarePermission(ROOT_PERMISSION, "Use /eb", null);

        Map<String, Boolean> children = new LinkedHashMap<>();
        children.put(ROOT_PERMISSION, true);
        for (SubCommand sub : subCommands.values()) {
            children.put(sub.permission(), true);
        }
        declarePermission(WILDCARD_PERMISSION, "All EntityBrain commands", children);
    }

    private void declarePermission(String node, String description, Map<String, Boolean> children) {
        PluginManager pm = plugin.getServer().getPluginManager();
        if(pm.getPermission(node) != null) return;

        Permission permission = children == null
                ? new Permission(node, description, PermissionDefault.OP)
                : new Permission(node, description, PermissionDefault.OP, children);

        pm.addPermission(permission);
    }
}
