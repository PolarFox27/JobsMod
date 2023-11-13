package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.polarfox27.jobs.commands.arguments.JobArgumentType;

public class JobCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("jobs")
                .requires((source) -> source.hasPermission(2))
                .then(Commands.literal("stats")
                        .then(Commands.literal("job")
                                .then(Commands.argument("job name", JobArgumentType.job())
                                        .executes((ctx) -> {
                                            CommandStats.showJobInfos(ctx.getSource(), JobArgumentType.getJob(ctx, "job name"));
                                            return 0;
                                        })))
                        .then(Commands.literal("player")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes((ctx) -> {
                                            CommandStats.showPlayersInfos(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"));
                                            return 0;
                                        }))))

                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("job", JobArgumentType.job())
                                        .then(Commands.argument("xp", LongArgumentType.longArg(0, Long.MAX_VALUE))
                                                .executes((ctx) -> {
                                                    CommandAdd.addXP(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                            StringArgumentType.getString(ctx, "job"), LongArgumentType.getLong(ctx, "xp"));
                                                    return 0;
                                                })))))

                .then(Commands.literal("set")
                        .then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("job", JobArgumentType.job())
                            .then(Commands.argument("level", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                            .then(Commands.argument("xp", LongArgumentType.longArg(0, Long.MAX_VALUE))
                                .executes((ctx) -> {
                                        CommandSet.setJobs(ctx.getSource(), EntityArgument.getPlayers(ctx, "targets"),
                                                JobArgumentType.getJob(ctx, "job"), IntegerArgumentType.getInteger(ctx, "level"),
                                                LongArgumentType.getLong(ctx, "xp"));
                                        return 0;
                                    })))))));
    }
}
