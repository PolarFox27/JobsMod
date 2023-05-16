package net.polarfox27.jobs.data.capabilities;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.network.PacketAddXP;
import net.polarfox27.jobs.network.PacketLevelUp;
import net.polarfox27.jobs.network.PacketSendRewardsClient;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.JobsUtil;
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
		PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(this),
									  p.connection.getConnection(),
									  NetworkDirection.PLAY_TO_CLIENT);
		PacketHandler.INSTANCE.sendTo(new PacketAddXP(j, xp),
									  p.connection.getConnection(),
									  NetworkDirection.PLAY_TO_CLIENT);
		int LVL = this.getLevelByJob(j);
		if(LVL > previousLVL) {
			PacketHandler.INSTANCE.sendTo(new PacketLevelUp(j),
										  p.connection.getConnection(),
										  NetworkDirection.PLAY_TO_CLIENT);
			giveReward(p, j, LVL);
		}

		if(LVL == levelData.getMaxLevel(j) && p.getServer() != null) {
			for(ServerPlayer mp : p.getServer().getPlayerList().getPlayers()) {
				String message = ChatFormatting.DARK_PURPLE + p.getName().getString() +
						ChatFormatting.BLUE + " has reached level " + levelData.getMaxLevel(j) + "for the job " + j + " !";
				mp.sendMessage(new TextComponent(message),
							   mp.getGameProfile().getId());
				p.getServer().sendMessage(new TextComponent(message),
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
	private void giveReward(ServerPlayer p, String j, int lvl) {
		if(!levelData.exists(j))
			return;
		List<ItemStack> list = ServerJobsData.REWARDS.getRewards(j, lvl);
		PacketHandler.INSTANCE.sendTo(new PacketSendRewardsClient(list),
									  p.connection.getConnection(),
									  NetworkDirection.PLAY_TO_CLIENT);
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

	/**
	 * Copies the values of the other Jobs
	 * @param other the Jobs to copy
	 */
	public void copy(PlayerJobs other) {
		this.fromNBT(other.toNBT());
	}
}
