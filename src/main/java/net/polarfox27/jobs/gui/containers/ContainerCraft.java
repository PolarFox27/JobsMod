package net.polarfox27.jobs.gui.containers;


import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;

import java.util.Optional;

public class ContainerCraft extends RecipeBookMenu<CraftingContainer> {
	// RESULT_SLOT = 0;
	// CRAFT_SLOT_START = 1;
	// CRAFT_SLOT_END = 10;
	// INV_SLOT_START = 10;
	// INV_SLOT_END = 37;
	// USE_ROW_SLOT_START = 37;
	// USE_ROW_SLOT_END = 46;
	private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
	private final ResultContainer resultSlots = new ResultContainer();
	private final ContainerLevelAccess access;
	private final Player player;

	public ContainerCraft(int id, Inventory inventory) {
		this(id, inventory, ContainerLevelAccess.NULL);
	}

	public ContainerCraft(int id, Inventory inventory, ContainerLevelAccess levelAccess) {
		super(MenuType.CRAFTING, id);
		this.access = levelAccess;
		this.player = inventory.player;
		this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}

		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
		}

	}

	protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingGrid, ResultContainer result) {
		if (!level.isClientSide) {
			ServerPlayer serverplayer = (ServerPlayer)player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingGrid, level);
			if (optional.isPresent()) {
				CraftingRecipe craftingrecipe = optional.get();
				if (result.setRecipeUsed(level, serverplayer, craftingrecipe)) {
					itemstack = craftingrecipe.assemble(craftingGrid);
					if(!ServerJobsData.BLOCKED_CRAFTS.isAllowed(PlayerData.getPlayerJobs(serverplayer), itemstack))
						itemstack = ItemStack.EMPTY;
				}
			}

			result.setItem(0, itemstack);
			menu.setRemoteSlot(0, itemstack);
			serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack));
		}
	}

	public void slotsChanged(Container container) {
		this.access.execute((world, pos) -> {
			slotChangedCraftingGrid(this, world, this.player, this.craftSlots, this.resultSlots);
		});
	}

	public void fillCraftSlotsStackedContents(StackedContents contents) {
		this.craftSlots.fillStackedContents(contents);
	}

	public void clearCraftingContent() {
		this.craftSlots.clearContent();
		this.resultSlots.clearContent();
	}

	public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
		return recipe.matches(this.craftSlots, this.player.level);
	}

	public void removed(Player player) {
		super.removed(player);
		this.access.execute((p_39371_, p_39372_) -> {
			this.clearContainer(player, this.craftSlots);
		});
	}

	public boolean stillValid(Player player) {
		return stillValid(this.access, player, Blocks.CRAFTING_TABLE);
	}

	public ItemStack quickMoveStack(Player player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(slotIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (slotIndex == 0) {
				this.access.execute((level, pos) -> {
					itemstack1.getItem().onCraftedBy(itemstack1, level, player);
				});
				if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (slotIndex >= 10 && slotIndex < 46) {
				if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
					if (slotIndex < 37) {
						if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
			if (slotIndex == 0) {
				player.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
	}

	public int getResultSlotIndex() {
		return 0;
	}

	public int getGridWidth() {
		return this.craftSlots.getWidth();
	}

	public int getGridHeight() {
		return this.craftSlots.getHeight();
	}

	public int getSize() {
		return 10;
	}

	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	public boolean shouldMoveToInventory(int slot) {
		return slot != this.getResultSlotIndex();
	}
}
