package net.polarfox27.jobs.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.Collection;

public class CommandAdd {

	/**
	 * Adds xp to a player when the command jobs-add is executed
	 * @param source the Command Source
	 * @param targets the players who will receive the xp
	 * @param job the job for which the target will gain xp
	 * @param xp the xp to be gained
	 */
	public static void addXP(CommandSource source, Collection<ServerPlayerEntity> targets, String job, long xp) {
		if(!(source.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
		if(!ServerJobsData.JOBS_LEVELS.exists(job)){
			PacketHandler.sendMessageToClient(sender, GuiUtil.coloredComponent(TextFormatting.RED,
					new TranslationTextComponent("argument.job.invalid", job)));
			return;
		}

		IFormattableTextComponent message = new TranslationTextComponent("command.job.add", job);
		for(ServerPlayerEntity target : targets){
			PlayerJobs jobs = PlayerData.getPlayerJobs(target);
			if(jobs == null || !jobs.getJobs().contains(job))
				continue;
			jobs.gainXP(job, xp, target);
			message = message.append(new TranslationTextComponent("command.job.add.row",
					TextFormatting.AQUA + target.getName().getString(), GuiUtil.coloredNum(TextFormatting.LIGHT_PURPLE, xp)));
			PacketHandler.INSTANCE.sendTo(
					new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
					target.connection.getConnection(),
					NetworkDirection.PLAY_TO_CLIENT);
		}
		PacketHandler.sendMessageToClient(sender, message);
	}
}
