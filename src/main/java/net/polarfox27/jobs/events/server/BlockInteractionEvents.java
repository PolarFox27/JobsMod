package net.polarfox27.jobs.events.server;

import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
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
    public void onBreakOreOrCrop(BlockEvent.BreakEvent event) {
    	if(event.getLevel().isClientSide() || !(event.getPlayer() instanceof ServerPlayer player))
            return;
        BlockState state = event.getState();
        PlayerJobs jobs = PlayerData.getPlayerJobs(player);

        if(ServerJobsData.BLOCKED_BLOCKS.isBlocked(PlayerData.getPlayerJobs(player), state)){
            event.setCanceled(true);
            return;
        }

        boolean isGrownCrop = false;
        if(state.getBlock() instanceof CropBlock){
            isGrownCrop = ((CropBlock)state.getBlock()).isMaxAge(state);
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
     * Fired when a player right-clicks a block. If the player can't right-click using the item, event is cancelled.
     * If the block usage is blocked, the event is cancelled too.
     * @param event the right click event.
     */
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        if(event.getLevel().isClientSide() || !(event.getEntity() instanceof ServerPlayer player))
            return;

        PlayerJobs jobs = PlayerData.getPlayerJobs(player);
        BlockState state = event.getLevel().getBlockState(event.getHitVec().getBlockPos());

        // Check block usage
        if (ServerJobsData.BLOCKED_BLOCK_USAGES.isBlocked(jobs, state)) {
            event.setCanceled(true);
            return;
        }

        ItemStack stack = player.getMainHandItem() == ItemStack.EMPTY ? player.getOffhandItem() :
            player.getMainHandItem();

        // Check item usage
        if(stack != ItemStack.EMPTY &&
            ServerJobsData.BLOCKED_RIGHT_CLICKS.isBlocked(jobs, stack))
            event.setCanceled(true);
    }

    /**
     * Fired when a player places a block. If the player can't place the block, the event is cancelled.
     *
     * @param event the placement event.
     */
    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.EntityPlaceEvent event){
        if(event.getLevel().isClientSide() || !(event.getEntity() instanceof ServerPlayer player))
            return;

        // Check block placement
        PlayerJobs jobs = PlayerData.getPlayerJobs(player);
        if (ServerJobsData.BLOCKED_PLACEMENTS.isBlocked(jobs, event.getPlacedBlock())) {
            event.setCanceled(true);
            player.connection.send(new ClientboundContainerSetContentPacket(
                player.inventoryMenu.containerId,
                player.inventoryMenu.getStateId(),
                player.inventoryMenu.getItems(),
                player.inventoryMenu.getCarried()));
        }
    }
}
