package net.polarfox27.jobs.commands;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandStats {

	/**
	 * shows the infos of the target to the source when the command jobs-info is executed
	 * @param source the Command Source
	 * @param targets the players whom information will be printed to the source
	 */
	public static void showPlayersInfos(CommandSourceStack source, Collection<ServerPlayer> targets) {
		if(!(source.getEntity() instanceof ServerPlayer sender))
			return;

		List<MutableComponent> lines = new ArrayList<>(targets.size() * ServerJobsData.JOBS_LEVELS.getJobs().size());


		for(ServerPlayer target : targets){
			PlayerJobs infos = PlayerData.getPlayerJobs(target);
			if(sender.getGameProfile().getId().equals(target.getGameProfile().getId()))
				lines.add(new TranslatableComponent("command.job.stats.you"));
			else
				lines.add(new TranslatableComponent("command.job.stats.of",
						ChatFormatting.AQUA + target.getName().getString()));

			for(String job : infos.getJobs()) {
				int lvl = infos.getLevelByJob(job);
				long xp = infos.getXPByJob(job);
				boolean isMax = infos.isMax(job);
				if(isMax)
					lines.add(new TranslatableComponent("command.job.stats.player.row.maxlevel", job,
							TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, lvl)));
				else
					lines.add(new TranslatableComponent("command.job.stats.player.row", job,
							TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, lvl),
							TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, xp)));
			}
		}
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(lines));
	}

	/**
	 * shows the infos of the job from all players to the source when the command jobs-info is executed
	 * @param source the Command Source
	 * @param job the info that will be queried about all players
	 */
	public static void showJobInfos(CommandSourceStack source, String job) {
		ServerPlayer sender = JobCommand.checkForCommandError(source, job);
		if(sender == null)
			return;

		List<MutableComponent> stats = source.getServer().getPlayerList().getPlayers()
				.stream()
				.filter(p -> PlayerData.getPlayerJobs(p) != null)
				.map(p -> {
					PlayerJobs jobs = PlayerData.getPlayerJobs(p);
					return new JobStats(p.getName().getString(), jobs.getLevelByJob(job),
							jobs.getXPByJob(job), jobs.isMax(job));
				})
				.sorted(Comparator.reverseOrder())
				.map(JobStats::toTextComponent)
				.collect(Collectors.toList());

		stats.add(0, new TranslatableComponent("command.job.stats.job", job));
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(stats));
	}


	private record JobStats(String player, int level, long xp, boolean isMax) implements Comparable<JobStats> {

		@Override
			public int compareTo(JobStats o) {
				if (this.level == o.level) {
					return Long.compare(this.xp, o.xp);
				}
				return Integer.compare(this.level, o.level);
			}

			public TranslatableComponent toTextComponent() {
				if (isMax)
					return new TranslatableComponent("command.job.stats.player.row.maxlevel",
							ChatFormatting.AQUA + player, TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, level));
				else
					return new TranslatableComponent("command.job.stats.player.row",
							ChatFormatting.AQUA + player, TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, level),
							TextUtil.coloredNum(ChatFormatting.LIGHT_PURPLE, xp));

			}
		}

}
