package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.network.PacketSendChatMessage;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandAdd {
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
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

	
	private static void addXP(CommandSource source, ServerPlayerEntity target, String job, long xp) {
		PlayerData.getPlayerJobs(target).gainXP(job, xp, target);
        PacketHandler.INSTANCE.sendTo(
				new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
										  target.connection.getConnection(),
										  NetworkDirection.PLAY_TO_CLIENT);
        
        if(source.getEntity() instanceof ServerPlayerEntity) {
        	ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
			String message = xp + " xp added to " + target.getName().getString() + " for job " + job;
        	PacketHandler.INSTANCE.sendTo(
					new PacketSendChatMessage(
							new StringTextComponent(message)),
    				sender.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
        }
	}
}
