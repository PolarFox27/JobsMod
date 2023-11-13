package net.polarfox27.jobs.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.ArrayList;
import java.util.List;
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
			PacketHandler.sendMessageToClient(sender, TextUtil.coloredComponent(TextFormatting.RED,
					new TranslationTextComponent("argument.job.invalid", job)));
			return;
		}

		List<IFormattableTextComponent> lines = new ArrayList<>(1 + targets.size());
		lines.add(new TranslationTextComponent("command.job.add", job));
		for(ServerPlayerEntity target : targets){
			PlayerJobs jobs = PlayerData.getPlayerJobs(target);
			if(jobs == null || !jobs.getJobs().contains(job))
				continue;
			jobs.gainXP(job, xp, target);
			lines.add(new TranslationTextComponent("command.job.add.row",
					TextFormatting.AQUA + target.getName().getString(), TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, xp)));
			PacketHandler.sendPacketToClient(target, new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)));
		}
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(lines));
	}
}
