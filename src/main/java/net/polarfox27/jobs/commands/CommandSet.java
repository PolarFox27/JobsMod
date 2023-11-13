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

public class CommandSet {

	/**
	 * Sets the player xp and level when the command jobs-set is executed
	 * @param source the Command Source
	 * @param targets the players whom xp will be set
	 * @param job the job for which the xp is set
	 * @param lvl the new level value for the job
	 * @param xp the new xp value for the job
	 */
	public static void setJobs(CommandSource source, Collection<ServerPlayerEntity> targets, String job, int lvl, long xp) {
		setJobs(source, targets, job, ServerJobsData.JOBS_LEVELS.getTotalXPForLevel(job, lvl) + xp);
	}

	/**
	 * Sets the player total xp when the command jobs-set is executed
	 * @param source the Command Source
	 * @param targets the players whom xp will be set
	 * @param job the job for which the xp is set
	 * @param total the new total xp value for the job
	 */
	private static void setJobs(CommandSource source, Collection<ServerPlayerEntity> targets, String job, long total) {
		if(!(source.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
		if(!ServerJobsData.JOBS_LEVELS.exists(job)){
			PacketHandler.sendMessageToClient(sender, GuiUtil.coloredComponent(TextFormatting.RED,
					new TranslationTextComponent("argument.job.invalid", job)));
			return;
		}

		IFormattableTextComponent message = new TranslationTextComponent("command.job.set", job);
		for(ServerPlayerEntity target : targets){
			PlayerJobs jobs = PlayerData.getPlayerJobs(target);
			if(jobs == null || !jobs.getJobs().contains(job))
				continue;
			jobs.set(job, total);
			PacketHandler.INSTANCE.sendTo(
					new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)),
					target.connection.getConnection(),
					NetworkDirection.PLAY_TO_CLIENT);
			int lvl = jobs.getLevelByJob(job);
			long xp = jobs.getXPByJob(job);
			message.append(new TranslationTextComponent("command.job.set.row",
					TextFormatting.AQUA + target.getDisplayName().getString(),
					GuiUtil.coloredNum(TextFormatting.LIGHT_PURPLE, lvl), GuiUtil.coloredNum(TextFormatting.LIGHT_PURPLE, xp)));
		}
        PacketHandler.sendMessageToClient(sender, message);
	}

}
