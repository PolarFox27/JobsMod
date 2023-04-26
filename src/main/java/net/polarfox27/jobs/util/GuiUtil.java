package net.polarfox27.jobs.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;

import java.awt.Color;


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
	public static void drawTexture(PoseStack mStack, Screen gui, int x, int y, int textX, int textY, int width, int height) {
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
	public static void drawScaledTexture(PoseStack mStack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
		Screen.blit(mStack, x, y, u, v, width, height, uWidth, vHeight);
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
	public static void drawJobIcon(PoseStack mStack, Screen gui, String job, int centerX, int centerY, int size) {
		if(ClientJobsData.JOBS_ICONS.containsKey(job)) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			RenderSystem.setShaderTexture(0, ClientJobsData.JOBS_ICONS.get(job).getId());
		}
		else {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			RenderSystem.setShaderTexture(0, DEFAULT_ICON);
		}
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
		float f = (float)Math.atan((double)(mouseX / 40.0F));
		float f1 = (float)Math.atan((double)(mouseY / 40.0F));
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((double)posX, (double)posY, 1050.0D);
		posestack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		PoseStack posestack1 = new PoseStack();
		posestack1.translate(0.0D, 0.0D, 1000.0D);
		posestack1.scale((float)scale, (float)scale, (float)scale);
		Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
		quaternion.mul(quaternion1);
		posestack1.mulPose(quaternion);
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + f * 20.0F;
		entity.setYRot(180.0F + f * 40.0F);
		entity.setXRot(-f1 * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		Lighting.setupForEntityInInventory();
		EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		quaternion1.conj();
		entityrenderdispatcher.overrideCameraOrientation(quaternion1);
		entityrenderdispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> {
			entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F,
					1.0F, posestack1, buffersource, 15728880);
		});
		buffersource.endBatch();
		entityrenderdispatcher.setRenderShadow(true);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		Lighting.setupFor3DItems();
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
	public static void renderCenteredString(PoseStack mStack, String text, int color, int x, int y, float scale){
		mStack.pushPose();
		mStack.translate(x, y, 0);
		mStack.scale(scale, scale, scale);
		float xPos = Minecraft.getInstance().font.width(text)/-2.0f;
		float yPos = -4.5f;
		Minecraft.getInstance().font.draw(mStack, text, xPos, yPos, color);
		mStack.popPose();
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
	public static void renderProgressBar(PoseStack mStack, Screen gui, int x, int y, int width, int height, long progress, long total){
		String info = progress < total ? progress + "/" + total :  GuiUtil.translate("text.level.max");
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
	public static void renderProgressBarWithText(PoseStack mStack, Screen gui, int x, int y, int width, int height, long progress, long total, String text){
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.setShaderTexture(0, GRADIENT_TEXTURE);
		if(progress >= total)
			drawTexture(mStack, gui, x, y, 0, 74, width, height); //full
		else{
			int size = (int)(width*((double)progress / (double)total));
			drawTexture(mStack, gui, x, y, 0, 50, width, height); //background
			drawTexture(mStack, gui, x, y, 0, 62, size, height);  // progress
		}
		renderCenteredString(mStack, text, Color.white.getRGB(), x+width/2, y+height/2+1, 0.6f);
	}

	public static String translate(String key){
		return new TranslatableComponent(key).getString();
	}

}
