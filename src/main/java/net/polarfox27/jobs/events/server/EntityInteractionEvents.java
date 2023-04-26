package net.polarfox27.jobs.events.server;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

@EventBusSubscriber
public class EntityInteractionEvents {

    /**
     * Fired when an entity dies. Checks if it was killed by a player and checks if the player can gain xp.
     * If yes it gives it to the player.
     * @param event the Death Event
     */
    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
    	if(event.getEntityLiving().level.isClientSide() ||
                !(event.getSource().getEntity() instanceof ServerPlayer))
            return;
        if(event.getEntityLiving() instanceof Player &&
                event.getSource().getEntity().equals(event.getEntityLiving()))
            return;
        EntityType<? extends Entity> type = event.getEntityLiving().getType();

        ServerPlayer p = (ServerPlayer)event.getSource().getEntity();
        PlayerJobs jobs = PlayerData.getPlayerJobs(p);

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.KILLING_ENTITY_XP.getXPByLevelAndJob(type, level, job);
            jobs.gainXP(job, xp, p);
        }
    }

    /**
     * Fired when a baby mob is born. If the parents where bred by the player, checks if the player can gain xp.
     * If yes it gives it to the player.
     * @param event the Baby Spawn Event
     */
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
            jobs.gainXP(job, xp, (ServerPlayer) event.getCausedByPlayer());
            System.out.println("gaining " + xp + " xp");
        }
    }

    /**
     * Blocks the left click if the player is not allowed
     * @param event the Left Click Event on a block
     */
    @SubscribeEvent
    public void onLeftClickEntity(AttackEntityEvent event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        if(!ServerJobsData.BLOCKED_LEFT_CLICKS.isAllowed(jobs, event.getPlayer().getMainHandItem()))
            event.setCanceled(true);
    }
}
