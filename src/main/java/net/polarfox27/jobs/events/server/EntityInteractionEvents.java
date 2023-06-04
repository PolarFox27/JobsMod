package net.polarfox27.jobs.events.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
    	if(event.getEntityLiving().world.isRemote ||
                !(event.getSource().getTrueSource() instanceof EntityPlayerMP))
            return;
        if(event.getEntityLiving() instanceof EntityPlayer &&
                event.getSource().getTrueSource().equals(event.getEntityLiving()))
            return;
        EntityLivingBase type = event.getEntityLiving();

        EntityPlayerMP p = (EntityPlayerMP)event.getSource().getTrueSource();
        PlayerJobs jobs = PlayerData.getPlayerJobs(p);

        for(String job : jobs.getJobs()){
            int world = jobs.getLevelByJob(job);
            long xp = ServerJobsData.KILLING_ENTITY_XP.getXPByLevelAndJob(type, world, job);
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
        if (event.getCausedByPlayer() == null || event.getCausedByPlayer().world.isRemote
            || event.getChild() == null)
            return;

        EntityLivingBase type = event.getChild();
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getCausedByPlayer());

        for (String job : jobs.getJobs()) {
            int world = jobs.getLevelByJob(job);
            long xp = ServerJobsData.BREEDING_ENTITY_XP.getXPByLevelAndJob(type, world, job);
            jobs.gainXP(job, xp, (EntityPlayerMP) event.getCausedByPlayer());
            System.out.println("gaining " + xp + " xp");
        }
    }

    /**
     * Blocks the left click if the player is not allowed
     * @param event the Left Click Event on a block
     */
    @SubscribeEvent
    public void onLeftClickEntity(AttackEntityEvent event) {
        if(event.getEntityPlayer().world.isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
        if(ServerJobsData.BLOCKED_LEFT_CLICKS.isBlocked(jobs, event.getEntityPlayer().getActiveItemStack()))
            event.setCanceled(true);
    }
}
