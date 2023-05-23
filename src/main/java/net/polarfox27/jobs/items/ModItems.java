package net.polarfox27.jobs.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;

public class ModItems {
	
	public static final List<Item> ITEMS = new ArrayList<Item>();
	public static final Item JOBS_BOOK = new ItemJobsBook();
	
	
	
	public static interface IHasModel{
		public void registerModel();
	}

}
