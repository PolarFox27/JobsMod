package net.polarfox27.jobs.util.handlers;

import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.containers.ContainerCraft;
import net.polarfox27.jobs.gui.containers.GuiCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    /**
     * Gets the container to opened when opening the jobs crafting menu
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return the container to open
     */
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == ModJobs.ID_GUI_CRAFTING) {
            return new ContainerCraft(player.inventory, world, pos);
        }
        return null;
    }

    /**
     * Gets the container to opened when opening the jobs crafting menu
     * @param ID The Gui ID Number
     * @param player The player viewing the Gui
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return the gui to open
     */
    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == ModJobs.ID_GUI_CRAFTING) {
            return new GuiCraft(player.inventory, world, pos);
        }
        return null;
    }
}
