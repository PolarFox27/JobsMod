package net.polarfox27.jobs.events.server;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
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
        if(e.getContainer() instanceof CraftingMenu menu){
            menu.resultSlots = new BlockingCraftingResultContainer();
            menu.slots.set(0, new ResultSlot(e.getPlayer(), menu.craftSlots, menu.resultSlots, 0, 124, 35));
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
    public static void changePlayerInventoryCraftingSlot(Player player){
        InventoryMenu menu = player.inventoryMenu;
        menu.resultSlots = new BlockingCraftingResultContainer();
        menu.slots.set(0, new ResultSlot(player, menu.getCraftSlots(), menu.resultSlots, 0, 124, 35));
    }
}
