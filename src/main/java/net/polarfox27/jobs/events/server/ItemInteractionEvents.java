package net.polarfox27.jobs.events.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

@EventBusSubscriber
public class ItemInteractionEvents {


    /**
     * Fired when an item is crafted. Checks if the player can gain xp and gives it to the player.
     * @param event the Craft Event
     */
    @SubscribeEvent
    public void onCraft(ItemCraftedEvent event) {
    	if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        ItemStack stack = event.getCrafting();

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.CRAFTING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
            jobs.gainXP(job, xp*stack.getCount(), (ServerPlayer) event.getPlayer());
        }
    }

    /**
     * Fired when an item is smelted. Checks if the player can gain xp and gives it to the player.
     * @param event the Smelt Event
     */
    @SubscribeEvent
    public void onSmelt(ItemSmeltedEvent event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        ItemStack stack = event.getSmelting();

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.SMELTING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
            jobs.gainXP(job, xp*stack.getCount(), (ServerPlayer) event.getPlayer());
        }
    }

    /**
     * Gives xp to the players when they fish items
     * @param event the Fish Event
     */
    @SubscribeEvent
    public void onFished(ItemFishedEvent event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        for(ItemStack stack : event.getDrops()){
            for(String job : jobs.getJobs()){
                int level = jobs.getLevelByJob(job);
                long xp = ServerJobsData.FISHING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
                jobs.gainXP(job, xp*stack.getCount(), (ServerPlayer) event.getPlayer());
            }
        }
    }

    /**
     * Blocks the right click if the player is not allowed
     * @param event the Right Click Event
     */
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        if(!ServerJobsData.BLOCKED_RIGHT_CLICKS.isAllowed(jobs, event.getItemStack()))
            event.setCanceled(true);
    }

    /**
     * Blocks the left click if the player is not allowed
     * @param event the Left Click Event on a block
     */
    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayer))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        if(!ServerJobsData.BLOCKED_LEFT_CLICKS.isAllowed(jobs, event.getItemStack()))
            event.setCanceled(true);
    }

    /**
     * Checks if the player is allowed to wear armor every tick. If not, the armor is dropped
     * @param event the armor tick event
     */
    @SubscribeEvent
    public void checkArmor(TickEvent.PlayerTickEvent event) {
        if(event.player.level.isClientSide())
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.player);
        for(int i = 0; i < event.player.getInventory().armor.size(); i++) {
            ItemStack stack = event.player.getInventory().armor.get(i);
            if (!ServerJobsData.BLOCKED_EQUIPMENTS.isAllowed(jobs, stack)) {
                event.player.drop(stack, true);
                event.player.getInventory().armor.set(i, ItemStack.EMPTY);
                return;
            }
        }

    }
    
}
