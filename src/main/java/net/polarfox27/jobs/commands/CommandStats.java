package net.polarfox27.jobs.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.*;
import java.util.stream.Collectors;

public class CommandStats {

	/**
	 * shows the infos of the target to the source when the command jobs-info is executed
	 * @param source the Command Source
	 * @param targets the players whom information will be printed to the source
	 */
	public static void showPlayersInfos(CommandSource source, Collection<ServerPlayerEntity> targets) {
		if(!(source.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();

		List<IFormattableTextComponent> lines = new ArrayList<>(targets.size() * ServerJobsData.JOBS_LEVELS.getJobs().size());


		for(ServerPlayerEntity target : targets){
			PlayerJobs infos = PlayerData.getPlayerJobs(target);
			if(sender.getGameProfile().getId().equals(target.getGameProfile().getId()))
				lines.add(new TranslationTextComponent("command.job.stats.you"));
			else
				lines.add(new TranslationTextComponent("command.job.stats.of",
						TextFormatting.AQUA + target.getName().getString()));

			for(String job : infos.getJobs()) {
				int lvl = infos.getLevelByJob(job);
				long xp = infos.getXPByJob(job);
				boolean isMax = infos.isMax(job);
				if(isMax)
					lines.add(new TranslationTextComponent("command.job.stats.player.row.maxlevel", job,
							TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, lvl)));
				else
					lines.add(new TranslationTextComponent("command.job.stats.player.row", job,
							TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, lvl),
							TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, xp)));
			}
		}
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(lines));
	}

	/**
	 * shows the infos of the job from all players to the source when the command jobs-info is executed
	 * @param source the Command Source
	 * @param job the info that will be queried about all players
	 */
	public static void showJobInfos(CommandSource source, String job) {
		if(!(source.getEntity() instanceof ServerPlayerEntity))
			return;
		ServerPlayerEntity sender = (ServerPlayerEntity)source.getEntity();
		if(!ServerJobsData.JOBS_LEVELS.exists(job)){
			PacketHandler.sendMessageToClient(sender, TextUtil.coloredComponent(TextFormatting.RED,
					new TranslationTextComponent("argument.job.invalid", job)));
			return;
		}

		List<IFormattableTextComponent> stats = source.getServer().getPlayerList().getPlayers()
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

		stats.add(0, new TranslationTextComponent("command.job.stats.job", job));
		PacketHandler.sendMessageToClient(sender, TextUtil.appendAllLines(stats));
	}


	private static class JobStats implements Comparable<JobStats>{

		private final String player;
		private final int level;
		private final long xp;
		private final boolean isMax;


		private JobStats(String player, int level, long xp, boolean isMax) {
			this.player = player;
			this.level = level;
			this.xp = xp;
			this.isMax = isMax;
		}

		@Override
		public int compareTo(JobStats o) {
			if(this.level == o.level){
				return Long.compare(this.xp, o.xp);
			}
			return Integer.compare(this.level, o.level);
		}

		public TranslationTextComponent toTextComponent() {
			if(isMax)
				return new TranslationTextComponent("command.job.stats.player.row.maxlevel",
						TextFormatting.AQUA + player, TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, level));
			else
				return new TranslationTextComponent("command.job.stats.player.row",
						TextFormatting.AQUA + player, TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, level),
						TextUtil.coloredNum(TextFormatting.LIGHT_PURPLE, xp));

		}
	}

}
