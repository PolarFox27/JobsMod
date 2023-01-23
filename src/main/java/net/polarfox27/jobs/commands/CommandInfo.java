package net.polarfox27.jobs.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.handler.PacketHandler;

public class CommandInfo {
	
	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("jobs-info")
							.requires((source) -> source.hasPermission(2))
				.executes((ctx) -> {
					if(!(ctx.getSource().getEntity() instanceof ServerPlayerEntity))
						return 0;
					showInfos(ctx.getSource(), (ServerPlayerEntity)ctx.getSource().getEntity());
					return 0;
				})
				.then(Commands.argument("target", EntityArgument.player())
				.executes((ctx)-> {
					showInfos(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"));
					return 0;
				})));
	}
	
	
	private static void showInfos(CommandSource source, ServerPlayerEntity target) {
		if(!(source.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
		PlayerJobs infos = PlayerData.getPlayerJobs(target);

		if(sender.getGameProfile().getId().equals(target.getGameProfile().getId()))
			PacketHandler.sendMessageToClient(sender,
					new StringTextComponent(TextFormatting.BLUE + "Your Stats"));
		else
			PacketHandler.sendMessageToClient(sender,
					new StringTextComponent(TextFormatting.LIGHT_PURPLE + "Stats of " +
							TextFormatting.BLUE + target.getName().getString()));

		for(String job : infos.getJobs()) {
            int lvl = infos.getLevelByJob(job);
            long xp = infos.getXPByJob(job);
            PacketHandler.sendMessageToClient(sender,
					new StringTextComponent(TextFormatting.LIGHT_PURPLE + job + " : lvl " +
                    TextFormatting.BLUE + lvl + TextFormatting.LIGHT_PURPLE + ", xp " + TextFormatting.BLUE + xp));
        }
	}

}
