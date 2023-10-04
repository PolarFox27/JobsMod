package net.polarfox27.jobs.containers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import org.jetbrains.annotations.NotNull;

public class BlockingCraftingResultContainer extends ResultContainer {

    @Override
    public boolean setRecipeUsed(@NotNull Level world, @NotNull ServerPlayer player, @NotNull Recipe<?> recipe) {
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
