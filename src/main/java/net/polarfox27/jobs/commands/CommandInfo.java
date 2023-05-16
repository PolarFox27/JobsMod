package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandInfo {

	/**
	 * Registers the command jobs-info with the following arguments :
	 * jobs-info <player>
	 * @param dispatcher the CommandDispatcher where the command will be registered
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("jobs-info")
							.requires((source) -> source.hasPermission(2))
				.executes((ctx) -> {
					if(!(ctx.getSource().getEntity() instanceof ServerPlayer))
						return 0;
					showInfos(ctx.getSource(), (ServerPlayer)ctx.getSource().getEntity());
					return 0;
				})
				.then(Commands.argument("target", EntityArgument.player())
				.executes((ctx)-> {
					showInfos(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"));
					return 0;
				})));
	}


	/**
	 * shows the infos of the target to the source when the command jobs-info is executed
	 * @param source the Command Source
	 * @param target the player whom information will be printed to the source
	 */
	private static void showInfos(CommandSourceStack source, ServerPlayer target) {
		if(!(source.getEntity() instanceof ServerPlayer sender))
			return;
		PlayerJobs infos = PlayerData.getPlayerJobs(target);

		if(sender.getGameProfile().getId().equals(target.getGameProfile().getId()))
			PacketHandler.sendMessageToClient(sender,
					new TextComponent(ChatFormatting.BLUE + "Your Stats"));
		else
			PacketHandler.sendMessageToClient(sender,
					new TextComponent(ChatFormatting.LIGHT_PURPLE + "Stats of " +
							ChatFormatting.BLUE + target.getName().getString()));

		for(String job : infos.getJobs()) {
            int lvl = infos.getLevelByJob(job);
            long xp = infos.getXPByJob(job);
            PacketHandler.sendMessageToClient(sender,
					new TextComponent(ChatFormatting.LIGHT_PURPLE + job + " : lvl " +
                    ChatFormatting.BLUE + lvl + ChatFormatting.LIGHT_PURPLE + ", xp " + ChatFormatting.BLUE + xp));
        }
	}

}
