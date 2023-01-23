package net.polarfox27.jobs.data.capabilities;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkDirection;
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

	public PlayerJobs(LevelData levelData) {
		this.levelData = levelData;
		for(String job : levelData.getJobs())
			this.XP.put(job, 0L);
	}

	public PlayerJobs(PacketBuffer buf) {
		this.levelData = new LevelData(buf);
		int size = buf.readInt();
		for(int i = 0; i < size; i++){
			String job = JobsUtil.readString(buf);
			long xp = buf.readLong();
			this.XP.put(job, xp);
		}
	}

	public void writeToBytes(PacketBuffer buf){
		this.levelData.writeToBytes(buf);
		buf.writeInt(XP.size());
		for(Map.Entry<String, Long> e : XP.entrySet()){
			JobsUtil.writeString(e.getKey(), buf);
			buf.writeLong(e.getValue());
		}
	}

	public Set<String> getJobs(){
		return this.levelData.getJobs();
	}


	public void set(String j, int lvl, long xp) {
		if(!levelData.exists(j))
			return;
		long total = this.levelData.getTotalXPForLevel(j, lvl) + xp;
		set(j, total);
	}
	
	public void set(String j, long value) {
		if(!levelData.exists(j))
			return;
		long total = JobsUtil.clamp(value, 0, levelData.getTotalXPForLevel(j, levelData.getMaxLevel(j)));
		this.XP.put(j, total);
	}
	

	public int getLevelByJob(String j) {
		return this.levelData.getLevelFromTotal(j, this.getTotalXPByJob(j));
	}
	
	public long getXPByJob(String j) {
		return this.levelData.getCurrentXPFromTotal(j, this.getTotalXPByJob(j));
	}

	public long getTotalXPByJob(String j) {
		if(!levelData.exists(j))
			return 0;
		return this.XP.getOrDefault(j, 0L);
	}
	
	public long getMissingXPForJob(String j) {
		return this.levelData.getMissingXPFromTotal(j, this.getTotalXPByJob(j));
	}
	
	//Add XP
	public void addXP(String j, long xp) {
		set(j, this.getTotalXPByJob(j)+xp);
	}
	
	public boolean isMax(String j) {
		return levelData.exists(j) && this.getLevelByJob(j) >= levelData.getMaxLevel(j);
	}
	
	public void gainXP(String j, long xp, ServerPlayerEntity p) {
		if(xp <= 0 || !levelData.exists(j))
			return;
		System.out.println("before: " + print(j));
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

		if(LVL == levelData.getMaxLevel(j)) {
			for(ServerPlayerEntity mp : p.getServer().getPlayerList().getPlayers()) {
				String message = TextFormatting.DARK_PURPLE + p.getName().getString() +
						TextFormatting.BLUE + " has reached level " + levelData.getMaxLevel(j) + "for the job " + j + " !";
				mp.sendMessage(new StringTextComponent(message),
							   mp.getGameProfile().getId());
				p.getServer().sendMessage(new StringTextComponent(message),
							   mp.getGameProfile().getId());
			}
		}
		System.out.println("after: " + print(j));
	}

	private void giveReward(ServerPlayerEntity p, String j, int lvl) {
		if(!levelData.exists(j))
			return;
		List<ItemStack> list = ServerJobsData.REWARDS.getRewards(j, lvl);
		PacketHandler.INSTANCE.sendTo(new PacketSendRewardsClient(list),
									  p.connection.getConnection(),
									  NetworkDirection.PLAY_TO_CLIENT);
		for(ItemStack s : list)
			p.inventory.add(s.copy());
		p.inventory.setChanged();
	}
	
	public void fromNBT(CompoundNBT nbt) {
		for(String job : nbt.getAllKeys())
			this.set(job, nbt.getLong(job));
	}
	
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		for(Map.Entry<String, Long> e : this.XP.entrySet())
			nbt.putLong(e.getKey(), e.getValue());
		return nbt;
	}
	
	public void copy(PlayerJobs other) {
		this.fromNBT(other.toNBT());
	}

	public String print(String job){
		return job + " : lvl " + this.getLevelByJob(job) + ", xp " + this.getXPByJob(job) + " (" + this.getTotalXPByJob(job) + " -> " + this.getMissingXPForJob(job) + ")";
	}

}
