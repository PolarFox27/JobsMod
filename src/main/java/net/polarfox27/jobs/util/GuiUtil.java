package net.polarfox27.jobs.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiUtil {

	private static final ResourceLocation GRADIENT_TEXTURE = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_gain_xp.png");
	private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/default_icon.png");


	/**
	 * Renders a scaled texture on the screen
	 *
	 * @param x the x position of the texture on the screen
	 * @param y the y position of the texture on the screen
	 * @param u the start x position of the texture
	 * @param v the start y position of the texture
	 * @param uWidth the original width of the texture
	 * @param vHeight the original height of the texture
	 * @param width the width of the texture that will be rendered
	 * @param height the height of the texture that will be rendered
	 */
	public static void drawScaledTexture(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
		GuiScreen.drawScaledCustomSizeModalRect(x, y, u, v, width, height, uWidth, vHeight, 256, 256);
    }

	/**
	 * Renders the job icon on the screen
	 *
	 * @param job the job of which the icon will be rendered
	 * @param centerX the x coordinate of the center of the icon
	 * @param centerY the y coordinate of the center of the icon
	 * @param size the size in pixel of the icon
	 */
	public static void drawJobIcon(String job, int centerX, int centerY, int size) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(ClientJobsData.JOBS_ICONS.containsKey(job))
			ClientJobsData.JOBS_ICONS.get(job).getTextureData();
		else
			Minecraft.getMinecraft().getTextureManager().bindTexture(DEFAULT_ICON);
		GuiUtil.drawScaledTexture(centerX-size/2, centerY-size/2, 0, 0, size, size, size, size);
	}

	/**
	 * Renders an entity on the screen. The entity will look at the mouse cursor
	 *
	 * @param posX the x coordinate on the screen
	 * @param posY the y coordinate on the screen
	 * @param base the base entity to adjust the size to
	 * @param mouseX the x coordinate of the mouse
	 * @param mouseY the y coordinate of the mouse
	 */
	public static void renderEntityInGui(int posX, int posY, EntityLivingBase base, float mouseX, float mouseY, EntityLivingBase ent) {
		if (base == null || ent == null)
			return;

		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)posX, (float)posY, 50.0F);

		double scale;
		if(JobsUtil.getHeight(base) >= JobsUtil.getHeight(ent)*2.0D)
			scale = 20.0D * JobsUtil.getHeight(ent);
		else if(JobsUtil.getHeight(base) > JobsUtil.getHeight(ent))
			scale = 10.0D * JobsUtil.getHeight(ent);
		else
			scale = 10.0D * JobsUtil.getHeight(base) / JobsUtil.getHeight(ent);

		GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
		ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
		ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		rendermanager.setPlayerViewY(180.0F);
		rendermanager.setRenderShadow(false);
		rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		rendermanager.setRenderShadow(true);
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Renders a centered string on the screen
	 *
	 * @param text the text to render
	 * @param color the color the text will be rendered in
	 * @param x the x coordinate of the center of the text
	 * @param y the y coordinate of the center of the text
	 * @param scale the scale of the text
	 */
	public static void renderCenteredString(String text, int color, int x, int y, float scale){
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glScalef(scale, scale, scale);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float xPos = Minecraft.getMinecraft().fontRenderer.getStringWidth(text)/-2.0f;
		float yPos = -4.5f;
		Minecraft.getMinecraft().fontRenderer.drawString(text, (int)xPos, (int)yPos, color);
		GL11.glPopMatrix();
	}

	/**
	 * Renders a progress bar with the progress written inside in the format "progress/total"
	 *
	 * @param gui the GUI that will render the progress bar
	 * @param x the x coordinate of the progress bar
	 * @param y the y coordinate of the progress bar
	 * @param width the width of the progress bar
	 * @param height the height of the progress bar
	 * @param progress the progression that the progress bar must render
	 * @param total the maximum progression that the progress bar could have
	 */
	public static void renderProgressBar(Gui gui, int x, int y, int width, int height, long progress, long total){
		String info = progress < total ? progress + "/" + total :  I18n.format("text.level.max");
		renderProgressBarWithText(gui, x, y, width, height, progress, total, info);
	}

	/**
	 * Renders a progress bar with a custom text inside
	 *
	 * @param gui the GUI that will render the progress bar
	 * @param x the x coordinate of the progress bar
	 * @param y the y coordinate of the progress bar
	 * @param width the width of the progress bar
	 * @param height the height of the progress bar
	 * @param progress the progression that the progress bar must render
	 * @param total the maximum progression that the progress bar could have
	 * @param text the custom text that will be rendered in the progress bar
	 */
	public static void renderProgressBarWithText(Gui gui, int x, int y, int width, int height, long progress, long total, String text){
		Minecraft.getMinecraft().getTextureManager().bindTexture(GRADIENT_TEXTURE);
		if(progress >= total)
			gui.drawTexturedModalRect(x, y, 0, 74, width, height); //full
		else{
			int size = (int)(width*((double)progress / (double)total));
			gui.drawTexturedModalRect(x, y, 0, 50, width, height); //background
			gui.drawTexturedModalRect(x, y, 0, 62, size, height);  // progress
		}
		renderCenteredString(text, Color.white.getRGB(), x+width/2, y+height/2+1, 0.6f);
	}

}
