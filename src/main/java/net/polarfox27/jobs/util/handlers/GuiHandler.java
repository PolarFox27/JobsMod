package net.polarfox27.jobs.util.handlers;

import net.polarfox27.jobs.gui.containers.ContainerCraft;
import net.polarfox27.jobs.gui.containers.GuiCraft;
import net.polarfox27.jobs.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == Reference.GUI_CRAFTING)
        {
            return new ContainerCraft(player.inventory, world, pos);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        if(ID == Reference.GUI_CRAFTING)
        {
            return new GuiCraft(player.inventory, world, pos);
        }
        return null;
    }
}
