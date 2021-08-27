package com.dorian2712.jobs.events.server;

import com.dorian2712.jobs.data.GainXPUtil;
import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerData;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@EventBusSubscriber
public class CommonEvents {
    
    @SubscribeEvent
	public static void onEntityCreating(AttachCapabilitiesEvent<Entity> event)
	{
		if(!(event.getObject() instanceof EntityPlayer)) return;
		if(!((EntityPlayer)event.getObject()).hasCapability(PlayerData.JOBS, null))
			event.addCapability(new ResourceLocation(Reference.MOD_ID, "jobs"), new PlayerData.JobsDispatcher());
	}
	
	@SubscribeEvent
	public static void onEntityCloned(PlayerEvent.Clone event)
	{
		if(!event.isWasDeath()) return;
		if(event.getOriginal().hasCapability(PlayerData.JOBS, null))
		{
			JobsInfo old_jobs = event.getOriginal().getCapability(PlayerData.JOBS, null);
			JobsInfo new_jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
			new_jobs.copy(old_jobs);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoined(EntityJoinWorldEvent event)
	{
		if(!(event.getEntity() instanceof EntityPlayerMP)) return;
		GainXPUtil.sendDataToClient((EntityPlayerMP)event.getEntity());
	}
}
