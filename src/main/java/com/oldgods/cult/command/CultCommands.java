package com.oldgods.cult.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.oldgods.cult.state.CultState;
import com.oldgods.cult.world.OldGod;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Locale;

/**
 * Lightweight command layer to configure cults without a GUI yet.
 */
public final class CultCommands {
    private CultCommands() {}

    private static final DynamicCommandExceptionType INVALID_GOD = new DynamicCommandExceptionType(name -> Text.literal("Unknown Old God: " + name));

    public static void register() {
        CommandRegistrationCallback.EVENT.register(CultCommands::registerRoot);
    }

    private static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("cult")
                .then(CommandManager.literal("create")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .then(CommandManager.argument("god", StringArgumentType.greedyString())
                                        .executes(ctx -> createCult(ctx)))))
                .then(CommandManager.literal("join")
                        .then(CommandManager.argument("leader", UuidArgumentType.uuid())
                                .executes(ctx -> joinCult(ctx))))
                .then(CommandManager.literal("switch")
                        .then(CommandManager.argument("god", StringArgumentType.greedyString())
                                .executes(ctx -> switchGod(ctx))))
                .then(CommandManager.literal("info")
                        .executes(ctx -> describe(ctx))));
    }

    private static int createCult(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        CultState state = CultState.get(ctx.getSource().getWorld());
        String name = StringArgumentType.getString(ctx, "name");
        OldGod god = getGod(ctx);
        ctx.getSource().sendFeedback(() -> state.createCult(ctx.getSource().getPlayer(), name, god), false);
        return 1;
    }

    private static int joinCult(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        CultState state = CultState.get(ctx.getSource().getWorld());
        ctx.getSource().sendFeedback(() -> state.joinCult(ctx.getSource().getPlayer(), UuidArgumentType.getUuid(ctx, "leader")), false);
        return 1;
    }

    private static int switchGod(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        CultState state = CultState.get(ctx.getSource().getWorld());
        OldGod god = getGod(ctx);
        ctx.getSource().sendFeedback(() -> state.changeGod(ctx.getSource().getPlayer(), god), false);
        return 1;
    }

    private static int describe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        CultState state = CultState.get(ctx.getSource().getWorld());
        ctx.getSource().sendFeedback(() -> state.describePlayerCult(ctx.getSource().getPlayer()), false);
        return 1;
    }

    private static OldGod getGod(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String input = StringArgumentType.getString(ctx, "god");
        String normalized = input.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
        try {
            return OldGod.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw INVALID_GOD.create(input);
        }
    }
}
