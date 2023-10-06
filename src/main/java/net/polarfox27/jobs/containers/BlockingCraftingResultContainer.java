package net.polarfox27.jobs.containers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

public class BlockingCraftingResultContainer extends CraftResultInventory {


    @Override
    public boolean setRecipeUsed(World world, ServerPlayerEntity player, IRecipe<?> recipe) {
        if(!super.setRecipeUsed(world, player, recipe))
            return false;

        PlayerJobs jobs = PlayerData.getPlayerJobs(player);
        if(ServerJobsData.BLOCKED_CRAFTS.isBlocked(jobs, recipe.getResultItem())){
            this.setRecipeUsed(null);
            return false;
        }
        return true;
    }
}
