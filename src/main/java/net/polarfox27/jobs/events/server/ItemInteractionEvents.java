package net.polarfox27.jobs.events.server;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
    	if(event.player.world.isRemote || !(event.player instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.player);
        ItemStack stack = event.crafting;

        for(String job : jobs.getJobs()){
            int world = jobs.getLevelByJob(job);
            long xp = ServerJobsData.CRAFTING_ITEMS_XP.getXPByLevelAndJob(stack, world, job);
            jobs.gainXP(job, xp*stack.getCount(), (EntityPlayerMP) event.player);
        }
    }

    /**
     * Fired when an item is smelted. Checks if the player can gain xp and gives it to the player.
     * @param event the Smelt Event
     */
    @SubscribeEvent
    public void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
        if(event.player.world.isRemote || !(event.player instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.player);
        ItemStack stack = event.smelting;

        for(String job : jobs.getJobs()){
            int world = jobs.getLevelByJob(job);
            long xp = ServerJobsData.SMELTING_ITEMS_XP.getXPByLevelAndJob(stack, world, job);
            jobs.gainXP(job, xp*stack.getCount(), (EntityPlayerMP) event.player);
        }
    }

    /**
     * Gives xp to the players when they fish items
     * @param event the Fish Event
     */
    @SubscribeEvent
    public void onFished(ItemFishedEvent event) {
        if(event.getEntityPlayer().world.isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
        for(ItemStack stack : event.getDrops()){
            for(String job : jobs.getJobs()){
                int world = jobs.getLevelByJob(job);
                long xp = ServerJobsData.FISHING_ITEMS_XP.getXPByLevelAndJob(stack, world, job);
                jobs.gainXP(job, xp*stack.getCount(), (EntityPlayerMP) event.getEntityPlayer());
            }
        }
    }

    /**
     * Blocks the right click if the player is not allowed
     * @param event the Right Click Event
     */
    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if(event.getEntityPlayer().world.isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
        if(ServerJobsData.BLOCKED_RIGHT_CLICKS.isBlocked(jobs, event.getItemStack()))
            event.setCanceled(true);
    }

    /**
     * Blocks the left click if the player is not allowed
     * @param event the Left Click Event on a block
     */
    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getEntityPlayer().world.isRemote || !(event.getEntityPlayer() instanceof EntityPlayerMP))
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.getEntityPlayer());
        if(ServerJobsData.BLOCKED_LEFT_CLICKS.isBlocked(jobs, event.getItemStack()))
            event.setCanceled(true);
    }

    /**
     * Checks if the player is allowed to wear armor every tick. If not, the armor is dropped
     * @param event the armor tick event
     */
    @SubscribeEvent
    public void checkArmor(TickEvent.PlayerTickEvent event) {
        if(event.player.world.isRemote)
            return;
        PlayerJobs jobs = PlayerData.getPlayerJobs(event.player);
        for(int i = 0; i < event.player.inventory.armorInventory.size(); i++) {
            ItemStack stack = event.player.inventory.armorInventory.get(i);
            if (ServerJobsData.BLOCKED_EQUIPMENTS.isBlocked(jobs, stack)) {
                event.player.dropItem(stack, true);
                event.player.inventory.armorInventory.set(i, ItemStack.EMPTY);
                return;
            }
        }

    }
    
}
