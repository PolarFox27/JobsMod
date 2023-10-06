package net.polarfox27.jobs.events.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.polarfox27.jobs.containers.BlockingCraftingResultContainer;

public class ContainerEvents {

    /**
     * Modify the crafting container when opened to block certain crafts.
     * @param e the Container opened event
     */
    @SubscribeEvent
    public void onCraftingMenuOpened(PlayerContainerEvent.Open e) {
        if(e.getContainer() instanceof WorkbenchContainer){
            WorkbenchContainer container = (WorkbenchContainer)e.getContainer();
            container.resultSlots = new BlockingCraftingResultContainer();
            container.slots.set(0, new CraftingResultSlot(e.getPlayer(), container.craftSlots, container.resultSlots, 0, 124, 35));
        }
    }

    /**
     * When the player is in Survival, modify the inventory to block certain crafts.
     * @param e the Click Event
     */
    @SubscribeEvent
    public void onPlayerInventoryOpened(PlayerEvent.PlayerChangeGameModeEvent e) {
        if(e.getNewGameMode().isSurvival()){
            changePlayerInventoryCraftingSlot(e.getPlayer());
        }
    }

    /**
     * Modifies the player inventory to block crafts according to the Jobs.
     * @param player the player from whom the inventory is modified.
     */
    public static void changePlayerInventoryCraftingSlot(PlayerEntity player){
        PlayerContainer container = player.inventoryMenu;
        container.resultSlots = new BlockingCraftingResultContainer();
        container.slots.set(0, new CraftingResultSlot(player, container.getCraftSlots(), container.resultSlots, 0, 124, 35));
    }
}
