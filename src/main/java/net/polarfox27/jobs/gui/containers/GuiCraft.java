package net.polarfox27.jobs.gui.containers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCraft extends ContainerScreen<ContainerCraft> implements IRecipeShownListener {

    private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui recipeBookComponent = new RecipeBookGui();
    private boolean widthTooNarrow;

    /**
     * Creates a modified crafting table GUI
     * @param container the item container
     * @param playerInventory the player opening the crafting table
     * @param title the name of the GUI
     */
    public GuiCraft(ContainerCraft container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    /**
     * Creates all the buttons and stacks
     */
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
        this.children.add(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
        this.addButton(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (p_214076_1_) -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            ((ImageButton)p_214076_1_).setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
        this.titleLabelX = 29;
    }

    /**
     * Ticks the recipe book
     */
    public void tick() {
        super.tick();
        this.recipeBookComponent.tick();
    }

    /**
     * Renders the GUI on the screen
     * @param mStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(mStack, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(mStack, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookComponent.render(mStack, mouseX, mouseY, partialTicks);
            super.render(mStack, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(mStack, this.leftPos, this.topPos, true, partialTicks);
        }

        this.renderTooltip(mStack, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(mStack, this.leftPos, this.topPos, mouseX, mouseY);
    }

    /**
     * Renders the background image
     * @param mStack
     * @param partialTicks
     * @param mouseX
     * @param mouseY
     */
    protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CRAFTING_TABLE_LOCATION);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(mStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    /**
     * Returns true if the mouse is hovering
     * @param xPos the box starting x position
     * @param yPos the box starting y position
     * @param width the box width
     * @param height the box height
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return
     */
    protected boolean isHovering(int xPos, int yPos, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(xPos, yPos, width, height, mouseX, mouseY);
    }

    /**
     * Handles a mouse click
     * @param mouseX the mouse x coordinate
     * @param mouseY the mouse y coordinate
     * @param clickType the click type
     * @return true if the click has been handled successfully
     */
    public boolean mouseClicked(double mouseX, double mouseY, int clickType) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, clickType)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, clickType);
        }
    }


    protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
        boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
        return this.recipeBookComponent.hasClickedOutside(p_195361_1_, p_195361_3_, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, p_195361_7_) && flag;
    }

    /**
     * Clicks on a slot
     * @param slot the slot clicked
     * @param mouseX the mouse x coordinate
     * @param mouseY the mouse y coordinate
     * @param clickType
     */
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    /**
     * Updates the Recipe Book
     */
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    /**
     * Executes when the GUI is removed from the screen
     */
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }

    /**
     * @return the Recipe Book
     */
    public RecipeBookGui getRecipeBookComponent() {
        return this.recipeBookComponent;
    }

}