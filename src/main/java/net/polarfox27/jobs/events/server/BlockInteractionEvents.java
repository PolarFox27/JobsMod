package net.polarfox27.jobs.events.server;


import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

@EventBusSubscriber
public class BlockInteractionEvents {

    /**
     * Fired when a block is broken by a player : checks if the player can gain any xp and gives it to the player.
     * @param event the Break Block Event
     */
    @SubscribeEvent
    public static void onBreakOreOrCrop(BreakEvent event) {
    	if(event.getWorld().isRemote || !(event.getPlayer() instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        IBlockState state = event.getState();
        PlayerJobs jobs = PlayerData.getPlayerJobs(player);

        if(ServerJobsData.BLOCKED_BLOCKS.isBlocked(PlayerData.getPlayerJobs(player), state)){
            event.setCanceled(true);
            return;
        }

        boolean isGrownCrop = false;
        if(state.getBlock() instanceof BlockCrops){
            isGrownCrop = ((BlockCrops)state.getBlock()).isMaxAge(state);
        }

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.BREAKING_BLOCKS_XP.getXPByLevelAndJob(state, level, job);
            if(xp > 0)
                jobs.gainXP(job, xp, player);
            if(!isGrownCrop)
                continue;

            long xp2 = ServerJobsData.HARVESTING_CROPS_XP.getXPByLevelAndJob(state, level, job);
            if(xp2 > 0)
                jobs.gainXP(job, xp2, player);
        }

    }
}
