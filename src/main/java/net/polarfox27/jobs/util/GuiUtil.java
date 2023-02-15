package net.polarfox27.jobs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class GuiUtil {

	private static final ResourceLocation GRADIENT_TEXTURE = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_gain_xp.png");
	private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/default_icon.png");

	/**
	 * Renders a texture on the screen
	 * @param mStack
	 * @param gui
	 * @param x
	 * @param y
	 * @param textX
	 * @param textY
	 * @param width
	 * @param height
	 */
	public static void drawTexture(MatrixStack mStack, AbstractGui gui, int x, int y, int textX, int textY, int width, int height) {
		gui.blit(mStack, x, y, textX, textY, width, height);
	}

	/**
	 * Renders a scaled texture on the screen
	 * @param mStack
	 * @param x
	 * @param y
	 * @param u
	 * @param v
	 * @param uWidth
	 * @param vHeight
	 * @param width
	 * @param height
	 */
	public static void drawScaledTexture(MatrixStack mStack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
		AbstractGui.blit(mStack, x, y, u, v, width, height, uWidth, vHeight);
    }

	/**
	 * Renders the job icon on the screen
	 * @param mStack
	 * @param gui
	 * @param job
	 * @param centerX
	 * @param centerY
	 * @param size
	 */
	public static void drawJobIcon(MatrixStack mStack, AbstractGui gui, String job, int centerX, int centerY, int size) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(ClientJobsData.JOBS_ICONS.containsKey(job))
			ClientJobsData.JOBS_ICONS.get(job).bind();
		else
			Minecraft.getInstance().getTextureManager().bind(DEFAULT_ICON);
		GuiUtil.drawScaledTexture(mStack, centerX-size/2, centerY-size/2, 0, 0, size, size, size, size);
	}

	/**
	 * Renders an entity on the screen
	 * @param posX
	 * @param posY
	 * @param scale
	 * @param mouseX
	 * @param mouseY
	 * @param entity
	 */
	public static void renderEntityInGui(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity) {
		float size = scale*1.1f/entity.getBbHeight();
		float f = (float)Math.atan((double)(mouseX / 40.0F));
		float f1 = (float)Math.atan((double)(mouseY / 40.0F));
		GL11.glPushMatrix();
		RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
		RenderSystem.scalef(1.0F, 1.0F, -1.0F);
		MatrixStack matrixstack = new MatrixStack();
		matrixstack.translate(0.0D, 0.0D, 1000.0D);
		matrixstack.scale(size, size, size);
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
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
		});
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
	 * @param mStack
	 * @param text
	 * @param color
	 * @param x
	 * @param y
	 * @param scale
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
	 * @param mStack
	 * @param gui
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param progress
	 * @param total
	 */
	public static void renderProgressBar(MatrixStack mStack, AbstractGui gui, int x, int y, int width, int height, long progress, long total){
		String info = progress < total ? progress + "/" + total :  I18n.get("text.level.max");
		renderProgressBarWithText(mStack, gui, x, y, width, height, progress, total, info);
	}

	/**
	 * Renders a progress bar with a custom text inside
	 * @param mStack
	 * @param gui
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param progress
	 * @param total
	 * @param text
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

}
