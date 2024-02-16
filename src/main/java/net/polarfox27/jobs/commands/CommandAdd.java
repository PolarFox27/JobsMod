package net.polarfox27.jobs.commands;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandAdd {

	/**
	 * Adds xp to a player when the command jobs-add is executed
	 * @param source the Command Source
	 * @param targets the players who will receive the xp
	 * @param job the job for which the target will gain xp
	 * @param xp the xp to be gained
	 */
	public static void addXP(CommandSourceStack source, Collection<ServerPlayer> targets, String job, long xp) {
		ServerPlayer sender = JobCommand.checkForCommandError(source, job);
		if(sender == null)
			return;

		List<MutableComponent> lines = new ArrayList<>(1 + targets.size());
		lines.add(Component.translatable("command.job.add", job));
		for(ServerPlayer target : targets){
			PlayerJobs jobs = PlayerData.getPlayerJobs(target);
			if(jobs == null || !jobs.getJobs().contains(job))
				continue;
			jobs.gainXP(job, xp, target);
			lines.add(Component.translatable("command.job.add.row",
					ChatFormatting.AQUA + target.getName().getString(), TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, xp)));
			PacketHandler.sendPacketToClient(target, new PacketUpdateClientJob(PlayerData.getPlayerJobs(target)));
		}
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(lines));
	}
}
