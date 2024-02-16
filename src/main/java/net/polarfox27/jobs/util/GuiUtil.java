package net.polarfox27.jobs.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import org.joml.Matrix4f;

import java.awt.*;


@OnlyIn(Dist.CLIENT)
public class GuiUtil {

    private static final ResourceLocation GRADIENT_TEXTURE = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_gain_xp.png");
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/default_icon.png");

    /**
     * Renders a texture on the screen
     *
     * @param gui the render object
     * @param texture the texture location
     * @param x the x position of the texture on the screen
     * @param y the y position of the texture on the screen
     * @param textX the start x position on the texture
     * @param textY the start y position on the texture
     * @param width the width of the texture to render
     * @param height the height of the texture to render
     */
    public static void drawTexture(GuiGraphics gui, ResourceLocation texture, int x, int y, int textX, int textY, int width, int height) {
        gui.blit(texture, x, y, textX, textY, width, height);
    }

    /**
     * Renders a scaled texture on the screen
     *
     * @param gui the render object
     * @param texture the texture to render
     * @param x the x position of the texture on the screen
     * @param y the y position of the texture on the screen
     * @param u the start x position of the texture
     * @param v the start y position of the texture
     * @param uWidth the original width of the texture
     * @param vHeight the original height of the texture
     * @param width the width of the texture that will be rendered
     * @param height the height of the texture that will be rendered
     */
    public static void drawScaledTexture(GuiGraphics gui, ResourceLocation texture, int x, int y,
                                         float u, float v, int uWidth, int vHeight, int width, int height) {
        gui.blit(texture, x, y, u, v, width, height, uWidth, vHeight);
    }

    /**
     * Renders a scaled texture on the screen
     *
     * @param gui the render object
     * @param textureId the dynamic texture to render
     * @param x the x position of the texture on the screen
     * @param y the y position of the texture on the screen
     * @param u the start x position of the texture
     * @param v the start y position of the texture
     * @param uWidth the original width of the texture
     * @param vHeight the original height of the texture
     * @param width the width of the texture that will be rendered
     * @param height the height of the texture that will be rendered
     */
    public static void drawScaledCustomTexture(GuiGraphics gui, int textureId, int x, int y,
                                 float u, float v, int uWidth, int vHeight, int width, int height) {
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = gui.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float x1 = (float)x;
        float x2 = (float)(x + width);
        float y1 = (float)y;
        float y2 = (float)(y + height);
        float z = 0;
        float u1 = u/((float)uWidth);
        float u2 = (u + width)/((float)uWidth);
        float v1 = v/((float)vHeight);
        float v2 = (v + height)/((float)vHeight);


        bufferbuilder.vertex(matrix4f, x1, y1, z).uv(u1, v1).endVertex();
        bufferbuilder.vertex(matrix4f, x1, y2, z).uv(u1, v2).endVertex();
        bufferbuilder.vertex(matrix4f, x2, y2, z).uv(u2, v2).endVertex();
        bufferbuilder.vertex(matrix4f, x2, y1, z).uv(u2, v1).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    /**
     * Renders the job icon on the screen
     *
     * @param gui the render object
     * @param job the job of which the icon will be rendered
     * @param centerX the x coordinate of the center of the icon
     * @param centerY the y coordinate of the center of the icon
     * @param size the size in pixel of the icon
     */
    public static void drawJobIcon(GuiGraphics gui, String job, int centerX, int centerY, int size) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (ClientJobsData.JOBS_ICONS.containsKey(job)) {
            int textureId = ClientJobsData.JOBS_ICONS.get(job).getId();
            drawScaledCustomTexture(gui, textureId, centerX - size / 2, centerY - size / 2,
                    0, 0, size, size, size, size);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawScaledTexture(gui, DEFAULT_ICON, centerX - size / 2, centerY - size / 2,
                    0, 0, size, size, size, size);
        }
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
    public static void renderEntityInGui(GuiGraphics gui, int posX, int posY, EntityType<?> base,
                                         float mouseX, float mouseY, LivingEntity entity) {
        EntityType<?> baseType = base != null ? base : EntityType.COW;
        LivingEntity baseEntity = (LivingEntity) baseType.create(entity.level());
        if (baseEntity == null)
            return;
        float scale;
        if(baseEntity.getBbHeight() >= entity.getBbHeight()*2.0f)
            scale = 20.0f * entity.getBbHeight();
        else if(baseEntity.getBbHeight() > entity.getBbHeight())
            scale = 10.0f * entity.getBbHeight();
        else
            scale = 8.5f * baseEntity.getBbHeight() / entity.getBbHeight();

        InventoryScreen.renderEntityInInventoryFollowsMouse(gui, posX, posY, (int)scale, mouseX, mouseY, entity);
    }

    /**
     * Renders a centered string on the screen
     *
     * @param gui the render object
     * @param text the text to render
     * @param color the color the text will be rendered in
     * @param x the x coordinate of the center of the text
     * @param y the y coordinate of the center of the text
     * @param scale the scale of the text
     */
    public static void renderCenteredString(GuiGraphics gui, String text, int color, int x, int y, float scale) {
        gui.pose().pushPose();
        gui.pose().translate(x, y, 0);
        gui.pose().scale(scale, scale, scale);
        int xPos = Minecraft.getInstance().font.width(text) / -2;
        int yPos = -4;
        gui.drawString(Minecraft.getInstance().font, text, xPos, yPos, color, false);
        gui.pose().popPose();
    }

    /**
     * Draws a string at a position.
     *
     * @param gui the render object
     * @param text the text to draw
     * @param x the x position
     * @param y the y position
     * @param color the text color
     */
    public static void renderString(GuiGraphics gui, String text, int x, int y, int color){
        gui.drawString(Minecraft.getInstance().font, text, x, y, color, false);
    }

    /**
     * Renders a progress bar with the progress written inside in the format "progress/total"
     *
     * @param gui the render object
     * @param x the x coordinate of the progress bar
     * @param y the y coordinate of the progress bar
     * @param width the width of the progress bar
     * @param height the height of the progress bar
     * @param progress the progression that the progress bar must render
     * @param total the maximum progression that the progress bar could have
     */
    public static void renderProgressBar(GuiGraphics gui, int x, int y, int width, int height, long progress, long total) {
        String info = progress < total ? progress + "/" + total : GuiUtil.translate("text.level.max");
        renderProgressBarWithText(gui, x, y, width, height, progress, total, info);
    }

    /**
     * Renders a progress bar with a custom text inside
     *
     * @param gui the render object
     * @param x the x coordinate of the progress bar
     * @param y the y coordinate of the progress bar
     * @param width the width of the progress bar
     * @param height the height of the progress bar
     * @param progress the progression that the progress bar must render
     * @param total the maximum progression that the progress bar could have
     * @param text the custom text that will be rendered in the progress bar
     */
    public static void renderProgressBarWithText(GuiGraphics gui, int x, int y, int width, int height, long progress, long total, String text) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (progress >= total)
            drawTexture(gui, GRADIENT_TEXTURE, x, y, 0, 74, width, height); //full
        else {
            int size = (int) (width * ((double) progress / (double) total));
            drawTexture(gui, GRADIENT_TEXTURE, x, y, 0, 50, width, height); //background
            drawTexture(gui, GRADIENT_TEXTURE, x, y, 0, 62, size, height);  // progress
        }
        renderCenteredString(gui, text, Color.white.getRGB(), x + width / 2, y + height / 2 + 1, 0.6f);
    }

    /**
     * Translates a string with parameters.
     *
     * @param key the translation key
     * @param params the parameters
     * @return the translated string
     */
    public static String translate(String key, Object... params) {
        return I18n.get(key, params);
    }

}
