package com.dorian2712.jobs.events.server;

import com.dorian2712.jobs.data.GainXPUtil;
import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerData;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Entities;
import com.dorian2712.jobs.util.Constants.Job;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class KillEntityEvent {

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event)
    {
    	if(event.getEntityLiving().world.isRemote) return;
        if(event.getSource().getTrueSource() instanceof EntityPlayerMP)
        {
            Entities entity;
            if(event.getEntityLiving() instanceof EntityPlayerMP) entity = Entities.PLAYER;
            else entity = Constants.Entities.byClass(event.getEntityLiving().getClass());

            EntityPlayerMP p = (EntityPlayerMP)event.getSource().getTrueSource();
            if(!GainXPUtil.KILL_ENTITY_XP.containsKey(entity)) return;
            JobsInfo infos = PlayerData.getPlayerJobs(p);
            Job j = GainXPUtil.KILL_ENTITY_JOB.get(entity);
            long xp = GainXPUtil.KILL_ENTITY_XP.get(entity)[infos.getLevelByJob(j)];

            infos.gainXP(j, xp, p);
        }
    }


}
