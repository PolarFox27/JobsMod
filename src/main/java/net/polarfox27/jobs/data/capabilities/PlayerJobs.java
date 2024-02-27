package net.polarfox27.jobs.data.capabilities;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.network.PacketAddXP;
import net.polarfox27.jobs.network.PacketLevelUp;
import net.polarfox27.jobs.network.PacketSendRewardsClient;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.JobsUtil;
import net.polarfox27.jobs.util.TextUtil;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerJobs {

	private final LevelData levelData;
	private final Map<String, Long> XP = new HashMap<>();

	/**
	 * Creates Jobs from levels data
	 * @param levelData the levels data
	 */
	public PlayerJobs(LevelData levelData) {
		this.levelData = levelData;
		for(String job : levelData.getJobs())
			this.XP.put(job, 0L);
	}

	/**
	 * Reads Jobs from a buffer
	 * @param buf the buffer from where to read
	 */
	public PlayerJobs(FriendlyByteBuf buf) {
		this.levelData = new LevelData(buf);
		int size = buf.readInt();
		for(int i = 0; i < size; i++){
			String job = JobsUtil.readString(buf);
			long xp = buf.readLong();
			this.XP.put(job, xp);
		}
	}

	/**
	 * Writes the Jobs to a buffer
	 * @param buf the buffer where to write the Jobs
	 */
	public void writeToBytes(FriendlyByteBuf buf){
		this.levelData.writeToBytes(buf);
		buf.writeInt(XP.size());
		for(Map.Entry<String, Long> e : XP.entrySet()){
			JobsUtil.writeString(e.getKey(), buf);
			buf.writeLong(e.getValue());
		}
	}

	/**
	 * @return a set of all the job names
	 */
	public Set<String> getJobs(){
		return this.levelData.getJobs();
	}

	/**
	 * Set the total xp of a job
	 * @param j the job to modify
	 * @param value the new total xp value
	 */
	public void set(String j, long value) {
		if(!levelData.exists(j))
			return;
		long total = JobsUtil.clamp(value, 0, levelData.getTotalXPForLevel(j, levelData.getMaxLevel(j)));
		this.XP.put(j, total);
	}


	/**
	 * Get the level the player has in that job
	 * @param j the job
	 * @return the player's level
	 */
	public int getLevelByJob(String j) {
		return this.levelData.getLevelFromTotal(j, this.getTotalXPByJob(j));
	}

	/**
	 * Get the xp the player has in that job
	 * @param j the job
	 * @return the player's xp
	 */
	public long getXPByJob(String j) {
		return this.levelData.getCurrentXPFromTotal(j, this.getTotalXPByJob(j));
	}

	/**
	 * Get the total xp the player has in that job
	 * @param j the job
	 * @return the player's total xp
	 */
	public long getTotalXPByJob(String j) {
		if(!levelData.exists(j))
			return 0;
		return this.XP.getOrDefault(j, 0L);
	}

	/**
	 * Adds xp to a job
	 * @param j the job
	 * @param xp the xp amount to add
	 */
	public void addXP(String j, long xp) {
		set(j, this.getTotalXPByJob(j)+xp);
	}

	/**
	 * checks if a player has reach the maximum level for a job
	 * @param j the job to check
	 * @return true if the player has reached the max level
	 */
	public boolean isMax(String j) {
		return levelData.exists(j) && this.getLevelByJob(j) >= levelData.getMaxLevel(j);
	}

	/**
	 * Gives xp to a player for a job and updates everything to the client, including level ups and rewards
	 * @param j the job to which the xp is added
	 * @param xp the xp amount added
	 * @param p the player who receives the xp
	 */
	public void gainXP(String j, long xp, ServerPlayer p) {
		if(xp <= 0 || !levelData.exists(j))
			return;
		int previousLVL = this.getLevelByJob(j);
		addXP(j, xp);
		PacketHandler.sendPacketToClient(p, new PacketUpdateClientJob(this));
		PacketHandler.sendPacketToClient(p, new PacketAddXP(j, xp));
		int LVL = this.getLevelByJob(j);
		if(LVL > previousLVL) {
			PacketHandler.sendPacketToClient(p, new PacketLevelUp(j, previousLVL));
			giveReward(p, j, LVL, previousLVL);
		}

		if(LVL == levelData.getMaxLevel(j) && p.getServer() != null) {
			for(ServerPlayer mp : p.getServer().getPlayerList().getPlayers()) {
				MutableComponent message = Component.translatable("text.reached.maxlevel",
						ChatFormatting.DARK_PURPLE + p.getName().getString(), ChatFormatting.BLUE + j,
						TextUtil.coloredNum(ChatFormatting.BLUE, levelData.getMaxLevel(j)));
				PacketHandler.sendMessageToClient(mp, message);
			}
		}
	}

	/**
	 * Gives the rewards to a player when they reach a new level. If multiple levels are gained at once, the player
	 * receives all the rewards for all the levels.
	 * @param p the player to reward
	 * @param j the job for which the player has leveled up
	 * @param lvl the level the player reached
	 * @param previous the previous level the player was at
	 */
	private void giveReward(ServerPlayer p, String j, int lvl, int previous) {
		if(!levelData.exists(j))
			return;
		List<ItemStack> list = ServerJobsData.REWARDS.getRewards(j, lvl, previous);
		PacketHandler.sendPacketToClient(p, new PacketSendRewardsClient(list));
		for(ItemStack s : list)
			p.getInventory().add(s.copy());
		p.getInventory().setChanged();
	}

	/**
	 * Deserialize Jobs from NBT
	 * @param nbt the nbt to read from
	 */
	public void fromNBT(CompoundTag nbt) {
		for(String job : nbt.getAllKeys())
			this.set(job, nbt.getLong(job));
	}

	/**
	 * Serialize Jobs to NBT
	 * @return the serialized NBT
	 */
	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		for(Map.Entry<String, Long> e : this.XP.entrySet())
			nbt.putLong(e.getKey(), e.getValue());
		return nbt;
	}
}
