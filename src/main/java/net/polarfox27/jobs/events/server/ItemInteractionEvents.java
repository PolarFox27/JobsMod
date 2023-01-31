package net.polarfox27.jobs.events.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
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
    	if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        ItemStack stack = event.getCrafting();

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.CRAFTING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
            jobs.gainXP(job, xp*stack.getCount(), (ServerPlayerEntity) event.getPlayer());
        }
    }

    /**
     * Fired when an item is smelted. Checks if the player can gain xp and gives it to the player.
     * @param event the Smelt Event
     */
    @SubscribeEvent
    public void onSmelt(ItemSmeltedEvent event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        ItemStack stack = event.getSmelting();

        for(String job : jobs.getJobs()){
            int level = jobs.getLevelByJob(job);
            long xp = ServerJobsData.SMELTING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
            jobs.gainXP(job, xp*stack.getCount(), (ServerPlayerEntity) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onFished(ItemFishedEvent event) {
        if(event.getPlayer().level.isClientSide() || !(event.getPlayer() instanceof ServerPlayerEntity))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getPlayer());
        for(ItemStack stack : event.getDrops()){
            for(String job : jobs.getJobs()){
                int level = jobs.getLevelByJob(job);
                long xp = ServerJobsData.FISHING_ITEMS_XP.getXPByLevelAndJob(stack, level, job);
                jobs.gainXP(job, xp*stack.getCount(), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }

    
}
