package net.polarfox27.jobs.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.util.GuiUtil;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiGainXP implements Widget {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_gain_xp.png");

    private final String job;
    private final long xp;

    /**
     * Creates the GUI
     * @param job the job for which the player gained xp
     * @param xpAdded the amount of xp the player gained
     */
    public GuiGainXP(String job, long xpAdded) {
        this.job = job;
        this.xp = xpAdded;
    }

    /**
     * Renders the GUI on the in-game GUI
     * @param mStack the render stack
     * @param partialTicks the rendering ticks
     */
    public void render(PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        int render_width = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        long xp_progression = ClientJobsData.playerJobs.getXPByJob(job);
        long total = ClientJobsData.JOBS_LEVELS.getXPForLevel(job, ClientJobsData.playerJobs.getLevelByJob(job)+1);

        String title = ChatFormatting.WHITE + ClientJobsData.getJobName(job) + " (lvl " + ClientJobsData.playerJobs.getLevelByJob(job) + ") : " +
                ChatFormatting.AQUA + "+" + xp + ChatFormatting.WHITE + " xp";
        int titleWidth = Minecraft.getInstance().font.width(title);

        mStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Gui gui = Minecraft.getInstance().gui;
        gui.blit(mStack, render_width/2 - 90, 5, 0, 0, 180, 50);//background
        GuiUtil.renderProgressBar(mStack, gui, render_width/2 - 75, 35, 150, 12, xp_progression, total); //progress bar
        Minecraft.getInstance().font.draw(mStack, title, render_width/2.0F - titleWidth/2.0F, 15, Color.white.getRGB()); // text
        mStack.popPose();
    }


    public static class GuiAddXpInfos{

        public List<Pair<String, Pair<Long, Long>>> infos = new ArrayList<>(); // Job : xp : millis

        /**
         * Checks if the GUI Gain XP should be rendered on the screen
         * @return true if the GUI should be displayed
         */
        public boolean shouldShow(){
            return !infos.isEmpty();
        }

        /**
         * Gets the data about the xp gained for a specific job
         * @param j the job for which we want the data
         * @return a triplet of data : [Job Name, XP, time remaining]
         */
        private Optional<Pair<String, Pair<Long, Long>>> getJob(String j){
            return infos.stream().filter(p -> p.getFirst().equals(j)).findFirst();
        }

        /**
         * Removes jobs that have not received xp in the last 5 seconds
         */
        public void update(){
            infos = infos.stream()
                         .filter(p -> p.getSecond().getSecond() + 5000 > System.currentTimeMillis())
                         .collect(Collectors.toList());
        }

        /**
         * Receives xp for a job and adds it to the list of job to render in the GUI
         * @param j the job for which the player received xp
         * @param xp the amount of xp received
         */
        public void addXP(String j, long xp)
        {
            long xpToSet = xp;
            long millisToSet = System.currentTimeMillis();
            if(getJob(j).isPresent()){
                xpToSet += getJob(j).get().getSecond().getFirst();
            }
            getJob(j).ifPresent(x -> infos.remove(x));
            infos.add(0, new Pair<>(j, new Pair<>(xpToSet, millisToSet)));
        }

        /**
         * Calculates which of the jobs should be rendered based on the current system millis
         * It cycles through all the jobs to render in 5s
         * @return [Job Name, XP] for the job that should be rendered
         */
        public Pair<String, Long> showJobAtTime(){
            long length = 5000/this.infos.size();
            int i = (int)((System.currentTimeMillis() - infos.get(0).getSecond().getSecond())/length);
            return new Pair<>(infos.get(i).getFirst(), infos.get(i).getSecond().getFirst());
        }
    }

}
