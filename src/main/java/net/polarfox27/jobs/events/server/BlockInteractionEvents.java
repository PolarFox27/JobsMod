package net.polarfox27.jobs.events.server;


import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
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
    public void onBreakOreOrCrop(BreakEvent event) {
    	if(event.getWorld().isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        BlockState state = event.getState();
        PlayerJobs jobs = PlayerData.getPlayerJobs(player);

        if(ServerJobsData.BLOCKED_BLOCKS.isBlocked(PlayerData.getPlayerJobs(player), state)){
            event.setCanceled(true);
            return;
        }

        boolean isGrownCrop = false;
        if(state.getBlock() instanceof CropsBlock){
            isGrownCrop = ((CropsBlock)state.getBlock()).isMaxAge(state);
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

    /**
     * Fired when a player right-clicks a block. If the player can't right-click using the item, event is canceled.
     * @param event the right click event.
     */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if(event.getWorld().isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
        PlayerJobs jobs = PlayerData.getPlayerJobs(player);

        ItemStack stack = player.getMainHandItem() == ItemStack.EMPTY ? player.getOffhandItem() :
                player.getMainHandItem();

        if(stack != ItemStack.EMPTY && ServerJobsData.BLOCKED_RIGHT_CLICKS.isBlocked(PlayerData.getPlayerJobs(player), stack))
            event.setCanceled(true);
    }
}
