package net.polarfox27.jobs.commands;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandSet {

	/**
	 * Sets the player xp and level when the command jobs-set is executed
	 * @param source the Command Source
	 * @param targets the players whom xp will be set
	 * @param job the job for which the xp is set
	 * @param lvl the new level value for the job
	 * @param xp the new xp value for the job
	 */
	public static void setJobs(CommandSourceStack source, Collection<ServerPlayer> targets, String job, int lvl, long xp) {
		setJobs(source, targets, job, ServerJobsData.JOBS_LEVELS.getTotalXPForLevel(job, lvl) + xp);
	}

	/**
	 * Sets the player total xp when the command jobs-set is executed
	 * @param source the Command Source
	 * @param targets the players whom xp will be set
	 * @param job the job for which the xp is set
	 * @param total the new total xp value for the job
	 */
	private static void setJobs(CommandSourceStack source, Collection<ServerPlayer> targets, String job, long total) {
		ServerPlayer sender = JobCommand.checkForCommandError(source, job);
		if(sender == null)
			return;

		List<MutableComponent> lines = new ArrayList<>(1 + targets.size());
		lines.add(Component.translatable("command.job.set", job));

		for(ServerPlayer target : targets){
			PlayerJobs jobs = PlayerData.getPlayerJobs(target);
			if(jobs == null || !jobs.getJobs().contains(job))
				continue;
			jobs.set(job, total);
			PacketHandler.sendPacketToClient(target, new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)));
			int lvl = jobs.getLevelByJob(job);
			long xp = jobs.getXPByJob(job);
			lines.add(Component.translatable("command.job.set.row",
					ChatFormatting.AQUA + target.getDisplayName().getString(),
					TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, lvl), TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, xp)));
		}
        PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(lines));
	}

}
