package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.polarfox27.jobs.commands.arguments.JobArgumentType;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class JobCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    /**
     * Checks in the command context that everything is valid for execution.
     * @param source the command source. It must be a player.
     * @param job the job argument. It must be a valid job.
     * @return the sender if no error has been detected, null otherwise.
     */
    public static ServerPlayer checkForCommandError(CommandSourceStack source, String job){
        if(!(source.getEntity() instanceof ServerPlayer sender))
            return null;
        if(!ServerJobsData.JOBS_LEVELS.exists(job)){
            PacketHandler.sendMessageToClient(sender, TextUtil.coloredComponent(ChatFormatting.RED,
                    new TranslatableComponent("argument.job.invalid", job)));
            return null;
        }
        return sender;
    }
}
