package net.polarfox27.jobs.events.server;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

@EventBusSubscriber
public class ItemInteractionEvents {


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

    
}
