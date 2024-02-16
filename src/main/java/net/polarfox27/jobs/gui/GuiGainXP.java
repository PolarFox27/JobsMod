package net.polarfox27.jobs.gui;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiGainXP extends AbstractWidget {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_gain_xp.png");

    private final String job;
    private final long xp;

    /**
     * Creates the GUI
     * @param job the job for which the player gained xp
     * @param xpAdded the amount of xp the player gained
     */
    public GuiGainXP(String job, long xpAdded) {
        super(Minecraft.getInstance().getWindow().getGuiScaledWidth()/2 - 90, 0, 180, 50,
                TextUtil.EMPTY);
        this.job = job;
        this.xp = xpAdded;
    }

    /**
     * Renders the GUI on the in-game GUI
     * @param gui the render object
     * @param partialTicks the rendering ticks
     */
    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
    	gui.pose().pushPose();
        int render_width = Minecraft.getInstance().getWindow().getGuiScaledWidth();

        long xp_progression = ClientJobsData.playerJobs.getXPByJob(job);
        long total = ClientJobsData.JOBS_LEVELS.getXPForLevel(job, ClientJobsData.playerJobs.getLevelByJob(job)+1);
        int width = (int)(150 * ((double)xp_progression /(double)total));

        String title = getTitle();
        String xpTotal = xp_progression + "/" + total;

        GuiUtil.drawTexture(gui, TEXTURE, render_width/2 - 90, 5, 0, 0, 180, 50);//background
        GuiUtil.drawTexture(gui, TEXTURE, render_width/2 - 75, 35, 0, 50, 150, 12);//progress background
        GuiUtil.drawTexture(gui, TEXTURE, render_width/2 - 75, 35, 0, 62, width, 12);//progressbar
        GuiUtil.renderCenteredString(gui, title, Color.white.getRGB(), render_width/2, 19, 1.0f);
        GuiUtil.renderCenteredString(gui, xpTotal, Color.black.getRGB(), render_width/2, 42, 1.0f);
        gui.pose().popPose();
    }

    /**
     * Updates the narration output by adding this widget's text.
     *
     * @param narrationElementOutput the narration output.
     */
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, getTitle());
    }

    /**
     * Gets the text shown in the UI.
     *
     * @return the title string.
     */
    private String getTitle(){
        return GuiUtil.translate("text.gain_xp", ClientJobsData.getJobName(job),
                ClientJobsData.playerJobs.getLevelByJob(job), xp);
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
