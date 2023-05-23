package net.polarfox27.jobs.data;


import net.polarfox27.jobs.network.PacketAddXP;
import net.polarfox27.jobs.network.PacketLevelUp;
import net.polarfox27.jobs.network.PacketSendRewardsClient;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.Constants;
import net.polarfox27.jobs.util.JobsMath;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.ArrayList;
import java.util.List;

public class JobsInfo {
	
	private int[] levels = new int[] {0, 0, 0, 0};
	private long[] XPs = new long[] {0, 0, 0, 0};
	
	
	//Set Values
	public void set(Constants.Job j, int lvl, long xp)
	{
		long total = Constants.TOTAL_XP_BY_LEVEL[lvl] + xp;
		set(j, total);
	}
	
	public void set(Constants.Job j, long value)
	{
		long total = JobsMath.clamp(value, 0, Constants.TOTAL_XP_BY_LEVEL[25]);
		int lvl = 0;
		long xp = 0;
		for(int i = 25; i >= 0; i--)
		{
			if(Constants.TOTAL_XP_BY_LEVEL[i] <= total)
			{
				lvl = i;
				xp = total - Constants.TOTAL_XP_BY_LEVEL[i];
				break;
			}
		}
		
		this.levels[j.index] = lvl;
		this.XPs[j.index] = xp;
	}
	
	
	//Get Values
	public int getLevelByJob(Constants.Job j)
	{
		return this.levels[j.index];
	}
	
	public long getXPByJob(Constants.Job j)
	{
		return this.XPs[j.index];
	}
	
	public long getMissingXPForJob(Constants.Job j)
	{
		if(this.levels[j.index] >= 25) return 0;
		
		return Constants.XP_BY_LEVEL[j.index + 1] - this.XPs[j.index];
	}
	
	
	//Conversion with total xp long[]
	public JobsInfo fromTotalXPs(long[] total)
	{
		if(total.length != 4) return new JobsInfo();
		
		for(int i = 0; i < 4; i++)
		{
			long l = total[i];
			Constants.Job j = Constants.Job.byIndex(i);
			set(j, l);
		}
		return this;
	}
	
	public long[] toTotalXPs()
	{
		long[] total = new long[] {0, 0, 0, 0};
		
		for(int i = 0; i < 4; i++)
		{
			long l = Constants.TOTAL_XP_BY_LEVEL[this.levels[i]] + this.XPs[i];
			total[i] = l;
				
		}
		return total;
	}
	
	
	//Add XP
	public void addXP(Constants.Job j, long xp)
	{
		long total = Constants.TOTAL_XP_BY_LEVEL[this.levels[j.index]] + this.XPs[j.index] + xp;
		set(j, total);
	}
	
	public boolean isMax(Constants.Job j)
	{
		return this.levels[j.index] >= 25;
	}
	
	public void gainXP(Constants.Job j, long xp, EntityPlayerMP p)
	{
		if(xp <= 0) return;

		int previousLVL = this.levels[j.index]; 
		addXP(j, xp);
		PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(p), p);
		int LVL = this.levels[j.index];
		PacketHandler.INSTANCE.sendTo(new PacketAddXP(j, xp), p);
		if(LVL > previousLVL)
		{
			PacketHandler.INSTANCE.sendTo(new PacketLevelUp(j), p);
			giveReward(p, j, LVL);
		}

		if(LVL == 25)
		{
			for(EntityPlayerMP mp : FMLServerHandler.instance().getServer().getPlayerList().getPlayers())
			{
				mp.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + p.getName() +
						TextFormatting.BLUE + " has reached level 25 for the job " + I18n.translateToLocal("jobs." + j.name) + " !"));
				FMLServerHandler.instance().getServer().sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + p.getName() +
						TextFormatting.BLUE + " has reached level 25 for the job " + I18n.translateToLocal("jobs." + j.name) + " !"));
			}
		}
	}

	private void giveReward(EntityPlayerMP p, Constants.Job j, int lvl)
	{
		List<ItemStack> list = new ArrayList<>();
		System.out.println(j + " : " + lvl);
		switch (j)
		{
			case HUNTER:
				for(ItemStack s : GainXPUtil.REWARDS_WARRIOR.get(lvl))
					list.add(s.copy());
				break;
			case MAGICIAN:
				for(ItemStack s : GainXPUtil.REWARDS_WIZARD.get(lvl))
					list.add(s.copy());
				break;
			case FARMER:
				for(ItemStack s : GainXPUtil.REWARDS_HERBALIST.get(lvl))
					list.add(s.copy());
				break;
			case MINER:
				for(ItemStack s : GainXPUtil.REWARDS_MINER.get(lvl))
					list.add(s.copy());
				break;
			default:
				break;
		}
		PacketHandler.INSTANCE.sendTo(new PacketSendRewardsClient(list), p);
		for(ItemStack s : list)
			p.addItemStackToInventory(s.copy());
		p.inventoryContainer.detectAndSendChanges();
	}
	
	public void fromNBT(NBTTagCompound nbt)
	{
		this.fromTotalXPs(new long[] {nbt.getLong("hunter"),
									  nbt.getLong("magician"),
									  nbt.getLong("farmer"),
									  nbt.getLong("miner")});
	}
	
	public NBTTagCompound toNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		long[] xps = this.toTotalXPs();
		nbt.setLong("hunter", xps[0]);
		nbt.setLong("magician", xps[1]);
		nbt.setLong("farmer", xps[2]);
		nbt.setLong("miner", xps[3]);
		return nbt;
	}
	
	public void copy(JobsInfo other)
	{
		this.fromTotalXPs(other.toTotalXPs());
	}

}
