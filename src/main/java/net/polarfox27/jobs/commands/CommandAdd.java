package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.network.PacketSendChatMessage;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandAdd {

	/**
	 * Registers the command jobs-add with the following arguments :
	 * jobs-add <player> <job> <xp>
	 * @param dispatcher the CommandDispatcher where the command will be registered
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("jobs-add")
									.requires((source)-> source.hasPermission(2))
			.then(Commands.argument("target", EntityArgument.player())
			.then(Commands.argument("job", StringArgumentType.string())
			.then(Commands.argument("xp", LongArgumentType.longArg(0, Long.MAX_VALUE))
			.executes((ctx) -> {
				addXP(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"),
						StringArgumentType.getString(ctx, "job"), LongArgumentType.getLong(ctx, "xp"));
				return 0;
			})))));
	}


	/**
	 * Adds xp to a player when the command jobs-add is executed
	 * @param source the Command Source
	 * @param target the player who will receive the xp
	 * @param job the job for which the target will gain xp
	 * @param xp the xp to be gained
	 */
	private static void addXP(CommandSourceStack source, ServerPlayer target, String job, long xp) {
		PlayerData.getPlayerJobs(target).gainXP(job, xp, target);
        PacketHandler.INSTANCE.sendTo(
				new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
										  target.connection.getConnection(),
										  NetworkDirection.PLAY_TO_CLIENT);
        
        if(source.getEntity() instanceof ServerPlayer) {
        	ServerPlayer sender = (ServerPlayer)source.getEntity();
			String message = xp + " xp added to " + target.getName().getString() + " for job " + job;
        	PacketHandler.INSTANCE.sendTo(
					new PacketSendChatMessage(
							new TextComponent(message)),
    				sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
	}
}
