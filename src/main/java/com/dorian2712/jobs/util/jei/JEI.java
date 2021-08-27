/*package com.dorian2712.jobs.util.jei;

import com.dorian2712.jobs.gui.containers.ContainerCraft;
import com.dorian2712.jobs.gui.containers.GuiCraft;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;

@JEIPlugin
public class JEI implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {}

    @Override
    public void register(IModRegistry registry)
    {
        IRecipeTransferRegistry transfer = registry.getRecipeTransferRegistry();

        transfer.addRecipeTransferHandler(ContainerCraft.class, VanillaRecipeCategoryUid.CRAFTING, 0, 9, 10, 36);
        registry.addRecipeClickArea(GuiCraft.class, 90, 35, 22, 15, VanillaRecipeCategoryUid.CRAFTING);
    }
}*/
