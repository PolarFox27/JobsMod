package net.polarfox27.jobs.data.capabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
	public PlayerJobs(PacketBuffer buf) {
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
	public void writeToBytes(PacketBuffer buf){
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
	public void gainXP(String j, long xp, ServerPlayerEntity p) {
		if(xp <= 0 || !levelData.exists(j))
			return;
		int previousLVL = this.getLevelByJob(j);
		addXP(j, xp);
		PacketHandler.sendPacketToClient(p, new PacketUpdateClientJob(this));
		PacketHandler.sendPacketToClient(p, new PacketAddXP(j, xp));
		int LVL = this.getLevelByJob(j);
		if(LVL > previousLVL) {
			PacketHandler.sendPacketToClient(p, new PacketLevelUp(j));
			giveReward(p, j, LVL);
		}

		if(LVL == levelData.getMaxLevel(j) && p.getServer() != null) {
			for(ServerPlayerEntity mp : p.getServer().getPlayerList().getPlayers()) {
				IFormattableTextComponent message = new TranslationTextComponent("text.reached.maxlevel",
						TextFormatting.DARK_PURPLE + p.getName().getString(), TextFormatting.BLUE + j,
						TextUtil.coloredNum(TextFormatting.BLUE, levelData.getMaxLevel(j)));
				mp.sendMessage(message,
							   mp.getGameProfile().getId());
				p.getServer().sendMessage(message,
							   mp.getGameProfile().getId());
			}
		}
	}

	/**
	 * Gives the rewards to a player when he reaches a new level
	 * @param p the player to reward
	 * @param j the job for which the player has leveled up
	 * @param lvl the level the player reached
	 */
	private void giveReward(ServerPlayerEntity p, String j, int lvl) {
		if(!levelData.exists(j))
			return;
		List<ItemStack> list = ServerJobsData.REWARDS.getRewards(j, lvl);
		PacketHandler.sendPacketToClient(p, new PacketSendRewardsClient(list));
		for(ItemStack s : list)
			p.inventory.add(s.copy());
		p.inventory.setChanged();
	}

	/**
	 * Deserialize Jobs from NBT
	 * @param nbt the nbt to read from
	 */
	public void fromNBT(CompoundNBT nbt) {
		for(String job : nbt.getAllKeys())
			this.set(job, nbt.getLong(job));
	}

	/**
	 * Serialize Jobs to NBT
	 * @return the serialized NBT
	 */
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		for(Map.Entry<String, Long> e : this.XP.entrySet())
			nbt.putLong(e.getKey(), e.getValue());
		return nbt;
	}

	/**
	 * Copies the values of the other Jobs
	 * @param other the Jobs to copy
	 */
	public void copy(PlayerJobs other) {
		this.fromNBT(other.toNBT());
	}
}
