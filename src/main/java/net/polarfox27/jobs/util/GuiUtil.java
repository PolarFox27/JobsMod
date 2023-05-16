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
import net.minecraft.world.entity.EntityType;
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
    public static void drawTexture(PoseStack mStack, Screen gui, int x, int y, int textX, int textY, int width, int height) {
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
    public static void drawScaledTexture(PoseStack mStack, int x, int y, float u, float v, int uWidth, int vHeight, int width, int height) {
        Screen.blit(mStack, x, y, u, v, width, height, uWidth, vHeight);
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
    public static void drawJobIcon(PoseStack mStack, String job, int centerX, int centerY, int size) {
        if (ClientJobsData.JOBS_ICONS.containsKey(job)) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, ClientJobsData.JOBS_ICONS.get(job).getId());
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, DEFAULT_ICON);
        }
        GuiUtil.drawScaledTexture(mStack, centerX - size / 2, centerY - size / 2, 0, 0, size, size, size, size);
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
    public static void renderEntityInGui(int posX, int posY, EntityType<?> base,
                                         float mouseX, float mouseY, LivingEntity entity) {
        float f = (float) Math.atan(mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(posX, posY, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack1 = new PoseStack();
        poseStack1.translate(0.0D, 0.0D, 1000.0D);

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

        poseStack1.scale(scale, scale, scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.mul(quaternion1);
        poseStack1.mulPose(quaternion);
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
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F,
                1.0F, poseStack1, buffersource, 15728880));
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
     *
     * @param mStack the render stack
     * @param text the text to render
     * @param color the color the text will be rendered in
     * @param x the x coordinate of the center of the text
     * @param y the y coordinate of the center of the text
     * @param scale the scale of the text
     */
    public static void renderCenteredString(PoseStack mStack, String text, int color, int x, int y, float scale) {
        mStack.pushPose();
        mStack.translate(x, y, 0);
        mStack.scale(scale, scale, scale);
        float xPos = Minecraft.getInstance().font.width(text) / -2.0f;
        float yPos = -4.5f;
        Minecraft.getInstance().font.draw(mStack, text, xPos, yPos, color);
        mStack.popPose();
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
    public static void renderProgressBar(PoseStack mStack, Screen gui, int x, int y, int width, int height, long progress, long total) {
        String info = progress < total ? progress + "/" + total : GuiUtil.translate("text.level.max");
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
    public static void renderProgressBarWithText(PoseStack mStack, Screen gui, int x, int y, int width, int height, long progress, long total, String text) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GRADIENT_TEXTURE);
        if (progress >= total)
            drawTexture(mStack, gui, x, y, 0, 74, width, height); //full
        else {
            int size = (int) (width * ((double) progress / (double) total));
            drawTexture(mStack, gui, x, y, 0, 50, width, height); //background
            drawTexture(mStack, gui, x, y, 0, 62, size, height);  // progress
        }
        renderCenteredString(mStack, text, Color.white.getRGB(), x + width / 2, y + height / 2 + 1, 0.6f);
    }

    public static String translate(String key) {
        return new TranslatableComponent(key).getString();
    }

}
