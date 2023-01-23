package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.network.PacketSendChatMessage;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandSet {
	
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
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
	
	private static void setJobs(CommandSource source, ServerPlayerEntity target, String job, int lvl, long xp) {
		setJobs(source, target, job, ServerJobsData.JOBS_LEVELS.getTotalXPForLevel(job, lvl) + xp);
	}
	
	private static void setJobs(CommandSource source, ServerPlayerEntity target, String job, long total) {
		PlayerData.getPlayerJobs(target).set(job, total);
        PacketHandler.INSTANCE.sendTo(
				new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
										  target.connection.getConnection(),
										  NetworkDirection.PLAY_TO_CLIENT);
        
        if(source.getEntity() instanceof ServerPlayerEntity) {
        	ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
        	int lvl = PlayerData.getPlayerJobs(target).getLevelByJob(job);
        	long xp = PlayerData.getPlayerJobs(target).getXPByJob(job);
			String message = "Job " + job + " of " + target.getDisplayName().getString() + " set to lvl " + lvl + ", xp " + xp;
        	PacketHandler.INSTANCE.sendTo(
					new PacketSendChatMessage(new StringTextComponent(message)),
											  sender.connection.getConnection(),
											  NetworkDirection.PLAY_TO_CLIENT);
        }
	}

}
