package com.dorian2712.jobs.items;

import com.dorian2712.jobs.ModJobs;
import com.dorian2712.jobs.gui.screens.MainJobsMenu;
import com.dorian2712.jobs.items.ModItems.IHasModel;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemJobsBook extends Item implements IHasModel{
	
	
	public ItemJobsBook() 
	{
		this.setRegistryName("jobs_book");
		this.setUnlocalizedName("jobs_book");
		this.setCreativeTab(CreativeTabs.MISC);
		ModItems.ITEMS.add(this);
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) 
	{
		if(world.isRemote)
		{
			Minecraft.getMinecraft().displayGuiScreen(new MainJobsMenu());
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
		}
		else return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
	}


	@Override
	public void registerModel() 
	{
		ModJobs.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
