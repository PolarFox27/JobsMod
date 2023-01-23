package net.polarfox27.jobs.events.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

@EventBusSubscriber
public class EntityInteractionEvents {

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
    	if(event.getEntityLiving().level.isClientSide() ||
                !(event.getSource().getDirectEntity() instanceof ServerPlayerEntity))
            return;
        if(event.getEntityLiving() instanceof PlayerEntity &&
                event.getSource().getDirectEntity().equals(event.getEntityLiving()))
            return;
        EntityType<? extends Entity> type = event.getEntityLiving().getType();

        ServerPlayerEntity p = (ServerPlayerEntity)event.getSource().getDirectEntity();
        PlayerJobs jobs = PlayerData.getPlayerJobs(p);

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.KILLING_ENTITY_XP.getXPByLevelAndJob(type, level, job);
            jobs.gainXP(job, xp, p);
        }
    }

    @SubscribeEvent
    public void onBreed(BabyEntitySpawnEvent event) {
        if (event.getCausedByPlayer() == null || event.getCausedByPlayer().level.isClientSide())
            return;
        System.out.println("Breeding");
        EntityType<? extends Entity> type = event.getChild().getType();

        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getCausedByPlayer());

        for (String job : jobs.getJobs()) {
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.BREEDING_ENTITY_XP.getXPByLevelAndJob(type, level, job);
            jobs.gainXP(job, xp, (ServerPlayerEntity) event.getCausedByPlayer());
            System.out.println("gaining " + xp + " xp");
        }
    }
}
