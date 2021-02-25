package com.dorian2712.jobs.events.server;

import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerList;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.data.GainXPUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class KillEntityEvent {

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event)
    {
    	if(event.getEntityLiving().world.isRemote) return;
        if(event.getSource().getTrueSource() instanceof EntityPlayerMP)
        {
            String name = "";
            if(event.getEntityLiving() instanceof EntityPlayerMP) name = "Player";
            else name = event.getEntityLiving().getName();

            EntityPlayerMP p = (EntityPlayerMP)event.getSource().getTrueSource();
            if(!GainXPUtil.KILL_ENTITY_XP.containsKey(name)) return;
            JobsInfo infos = PlayerList.getJobsOfPlayer(p.getGameProfile().getId().toString());
            Job j = GainXPUtil.KILL_ENTITY_JOB.get(name);
            long xp = GainXPUtil.KILL_ENTITY_XP.get(name)[infos.getLevelByJob(j)];

            infos.gainXP(j, xp, p);
        }
    }


}
