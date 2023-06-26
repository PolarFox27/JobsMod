package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.network.PacketSendChatMessage;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandSet {

	/**
	 * Registers the command jobs-set with the following arguments :
	 * jobs-set <player> <job> (<total xp> | <level> <xp>)
	 * @param dispatcher the CommandDispatcher where the command will be registered
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("jobs-set")
			.requires((source) -> source.hasPermission(2))
			.then(Commands.argument("target", EntityArgument.player())
			.then(Commands.argument("job", StringArgumentType.string())
			.then(Commands.argument("total xp", LongArgumentType.longArg(0, Long.MAX_VALUE))
			.executes((ctx) -> {
				setJobs(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"),
						StringArgumentType.getString(ctx, "job"), LongArgumentType.getLong(ctx, "total xp"));
				return 0;
			}))))
			.then(Commands.argument("target", EntityArgument.player())
			.then(Commands.argument("job", StringArgumentType.string())
			.then(Commands.argument("level", IntegerArgumentType.integer(0, 25))
			.then(Commands.argument("xp", LongArgumentType.longArg(0, Long.MAX_VALUE))
			.executes((ctx) -> {
				setJobs(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"), StringArgumentType.getString(ctx, "job"), IntegerArgumentType.getInteger(ctx, "level"), LongArgumentType.getLong(ctx, "xp"));
				return 0;
			}))))));
	}

	/**
	 * Sets the player xp and level when the command jobs-set is executed
	 * @param source the Command Source
	 * @param target the player whom xp will be set
	 * @param job the job for which the xp is set
	 * @param lvl the new level value for the job
	 * @param xp the new xp value for the job
	 */
	private static void setJobs(CommandSourceStack source, ServerPlayer target, String job, int lvl, long xp) {
		setJobs(source, target, job, ServerJobsData.JOBS_LEVELS.getTotalXPForLevel(job, lvl) + xp);
	}

	/**
	 * Sets the player total xp when the command jobs-set is executed
	 * @param source the Command Source
	 * @param target the player whom xp will be set
	 * @param job the job for which the xp is set
	 * @param total the new total xp value for the job
	 */
	private static void setJobs(CommandSourceStack source, ServerPlayer target, String job, long total) {
		PlayerData.getPlayerJobs(target).set(job, total);
        PacketHandler.INSTANCE.sendTo(
				new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
										  target.connection.getConnection(),
										  NetworkDirection.PLAY_TO_CLIENT);
        
        if(source.getEntity() instanceof ServerPlayer sender) {
			int lvl = PlayerData.getPlayerJobs(target).getLevelByJob(job);
        	long xp = PlayerData.getPlayerJobs(target).getXPByJob(job);
			String message = "Job " + job + " of " + target.getDisplayName().getString() + " set to lvl " + lvl + ", xp " + xp;
        	PacketHandler.INSTANCE.sendTo(
					new PacketSendChatMessage(Component.literal(message)),
											  sender.connection.getConnection(),
											  NetworkDirection.PLAY_TO_CLIENT);
        }
	}

}
