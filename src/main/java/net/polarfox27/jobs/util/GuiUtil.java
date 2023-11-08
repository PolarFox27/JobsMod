package net.polarfox27.jobs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

@OnlyIn(Dist.CLIENT)
public class GuiUtil {

	private static final ResourceLocation GRADIENT_TEXTURE = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_gain_xp.png");
	private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/default_icon.png");

	/**
	 * Renders a texture on the screen
	 *
	 * @param mStack the render stack
	 * @param gui the GUI that will render the texture
	 * @param x the x position of the texture on the screen
	 * @param y the y position of the texture on the screen
	 * @param textX the start x position on the texture
	 * @param textY the start y position on the texture
	 * @param width the width of the texture to render
	 * @param height the height of the texture to render
	 */
	public static void drawTexture(MatrixStack mStack, AbstractGui gui, int x, int y, int textX, int textY, int width, int height) {
		gui.blit(mStack, x, y, textX, textY, width, height);
	}

	/**
	 * Renders a scaled texture on the screen
	 *
	 * @param mStack the render stack
	 * @param x the x position of the texture on the screen
	 * @param y the y position of the texture on the screen
	 * @param u the start x position of the texture
	 * @param v the start y position of the texture
	 * @param uWidth the original width of the texture
	 * @param vHeight the original height of the texture
	 * @param width the width of the texture that will be rendered
	 * @param height the height of the texture that will be rendered
	 */
	public static void drawScaledTexture(MatrixStack mStack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
		AbstractGui.blit(mStack, x, y, u, v, width, height, uWidth, vHeight);
    }

	/**
	 * Renders the job icon on the screen
	 *
	 * @param mStack the render stack
	 * @param job the job of which the icon will be rendered
	 * @param centerX the x coordinate of the center of the icon
	 * @param centerY the y coordinate of the center of the icon
	 * @param size the size in pixel of the icon
	 */
	public static void drawJobIcon(MatrixStack mStack, String job, int centerX, int centerY, int size) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(ClientJobsData.JOBS_ICONS.containsKey(job))
			ClientJobsData.JOBS_ICONS.get(job).bind();
		else
			Minecraft.getInstance().getTextureManager().bind(DEFAULT_ICON);
		GuiUtil.drawScaledTexture(mStack, centerX-size/2, centerY-size/2, 0, 0, size, size, size, size);
	}

	/**
	 * Renders an entity on the screen. The entity will look at the mouse cursor
	 *
	 * @param posX the x coordinate on the screen
	 * @param posY the y coordinate on the screen
	 * @param base the base entity to adjust the size to
	 * @param mouseX the x coordinate of the mouse
	 * @param mouseY the y coordinate of the mouse
	 * @param entity the entity to render
	 */
	public static void renderEntityInGui(int posX, int posY, EntityType<?> base, float mouseX, float mouseY, LivingEntity entity) {
		float f = (float)Math.atan((mouseX / 40.0F));
		float f1 = (float)Math.atan((mouseY / 40.0F));
		GL11.glPushMatrix();
		RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		MatrixStack matrixstack = new MatrixStack();
		matrixstack.translate(0.0D, 0.0D, 1000.0D);

		EntityType<?> baseType = base != null ? base : EntityType.COW;
		LivingEntity baseEntity = (LivingEntity) baseType.create(entity.level);
		if (baseEntity == null)
			return;
		float scale;
		if(baseEntity.getBbHeight() >= entity.getBbHeight()*2.0f)
			scale = 20.0f * entity.getBbHeight();
		else if(baseEntity.getBbHeight() > entity.getBbHeight())
			scale = 10.0f * entity.getBbHeight();
		else
			scale = 10.0f * baseEntity.getBbHeight() / entity.getBbHeight();


		matrixstack.scale(scale, scale, scale);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		matrixstack.mulPose(quaternion);
		float f2 = entity.yBodyRot;
		float f3 = entity.yRot;
		float f4 = entity.xRot;
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + f * 20.0F;
		entity.yRot = 180.0F + f * 40.0F;
		entity.xRot = -f1 * 20.0F;
		entity.yHeadRot = entity.yRot;
		entity.yHeadRotO = entity.yRot;
		EntityRendererManager entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternion1.conj();
		entityrenderermanager.overrideCameraOrientation(quaternion1);
		entityrenderermanager.setRenderShadow(false);
		IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D,
				0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880));
		irendertypebuffer$impl.endBatch();
		entityrenderermanager.setRenderShadow(true);
		entity.yBodyRot = f2;
		entity.yRot = f3;
		entity.xRot = f4;
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
		GL11.glPopMatrix();
	}

	/**
	 * Renders a centered string on the screen
	 *
	 * @param mStack the render stack
	 * @param text the text to render
	 * @param color the color the text will be rendered in
	 * @param x the x coordinate of the center of the text
	 * @param y the y coordinate of the center of the text
	 * @param scale the scale of the text
	 */
	public static void renderCenteredString(MatrixStack mStack, String text, int color, int x, int y, float scale){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glScalef(scale, scale, scale);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float xPos = Minecraft.getInstance().font.width(text)/-2.0f;
		float yPos = -4.5f;
		Minecraft.getInstance().font.draw(mStack, text, xPos, yPos, color);
		GL11.glPopMatrix();
	}

	/**
	 * Renders a progress bar with the progress written inside in the format "progress/total"
	 *
	 * @param mStack the render stack
	 * @param gui the GUI that will render the progress bar
	 * @param x the x coordinate of the progress bar
	 * @param y the y coordinate of the progress bar
	 * @param width the width of the progress bar
	 * @param height the height of the progress bar
	 * @param progress the progression that the progress bar must render
	 * @param total the maximum progression that the progress bar could have
	 */
	public static void renderProgressBar(MatrixStack mStack, AbstractGui gui, int x, int y, int width, int height, long progress, long total){
		String info = progress < total ? progress + "/" + total :  I18n.get("text.level.max");
		renderProgressBarWithText(mStack, gui, x, y, width, height, progress, total, info);
	}

	/**
	 * Renders a progress bar with a custom text inside
	 *
	 * @param mStack the render stack
	 * @param gui the GUI that will render the progress bar
	 * @param x the x coordinate of the progress bar
	 * @param y the y coordinate of the progress bar
	 * @param width the width of the progress bar
	 * @param height the height of the progress bar
	 * @param progress the progression that the progress bar must render
	 * @param total the maximum progression that the progress bar could have
	 * @param text the custom text that will be rendered in the progress bar
	 */
	public static void renderProgressBarWithText(MatrixStack mStack, AbstractGui gui, int x, int y, int width, int height, long progress, long total, String text){
		Minecraft.getInstance().getTextureManager().bind(GRADIENT_TEXTURE);
		if(progress >= total)
			drawTexture(mStack, gui, x, y, 0, 74, width, height); //full
		else{
			int size = (int)(width*((double)progress / (double)total));
			drawTexture(mStack, gui, x, y, 0, 50, width, height); //background
			drawTexture(mStack, gui, x, y, 0, 62, size, height);  // progress
		}
		renderCenteredString(mStack, text, Color.white.getRGB(), x+width/2, y+height/2+1, 0.6f);
	}

	/**
	 * Colors a component using the specified formatting.
	 * @param formatting the formatting to apply to the component
	 * @param component the text component to modify
	 * @return the formatted text component
	 */
	public static IFormattableTextComponent coloredComponent(TextFormatting formatting, TextComponent component){
		return component.setStyle(Style.EMPTY.withColor(formatting));
	}

}
