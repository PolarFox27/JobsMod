package net.polarfox27.jobs.util.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.util.JobsUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JobsIconUtil {

    /**
     * Loads the Jobs Icons from the config folder into ServerJobsData
     * @param server the server in which the config is located
     */
    public static void loadJobsIcon(MinecraftServer server){
        Map<String, byte[]> map = ServerJobsData.JOBS_ICONS;
        File folder = new File(FileUtil.getIconFolder(server));
        if(folder.mkdirs())
            ModJobs.warning("Icons folder created");
        if(folder.isDirectory()){
            for(String job : ServerJobsData.JOBS_LEVELS.getJobs()){
                File file = FileUtil.join(folder.getAbsolutePath(), job + ".png");
                try {
                    map.put(job, IOUtils.toByteArray(new FileInputStream(file)));
                }
                catch (FileNotFoundException e) {
                    ModJobs.info("Error loading icons : " + file.getName() + " does not exist !", true);
                } catch (IOException e) {
                    ModJobs.info("Error loading icon file <" + file.getName() + "> !", true);
                }
            }
        }
    }

    /**
     * Writes the icons in a Byte Buffer
     * @param buf the buffer where to write
     * @param map the map mapping the jobs name to the jobs icons
     */
    public static void toBytes(ByteBuf buf, Map<String, byte[]> map){
        buf.writeInt(map.size());
        for(Map.Entry<String, byte[]> e : map.entrySet()){
            ByteBufUtils.writeUTF8String(buf, e.getKey());
            JobsUtil.writeByteArray(e.getValue(), buf);
        }
    }

    /**
     * Reads the icons from a Byte Buffer
     * @param buf the buffer where to read
     * @return the map mapping the jobs name to the jobs icons
     */
    public static Map<String, byte[]> fromBytes(ByteBuf buf){
        Map<String, byte[]> map = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = ByteBufUtils.readUTF8String(buf);
            byte[] array = JobsUtil.readByteArray(buf);
            map.put(job, array);
        }
        return map;
    }

    /**
     * Converts the map of images to a map of DynamicTexture usable in GUIs and loads them in TextureManager.
     * @param map the map mapping the jobs name to the jobs icons
     */
    @SideOnly(Side.CLIENT)
    public static void loadJobsIconTextures(Map<String, byte[]> map){
        for(Map.Entry<String, byte[]> e : map.entrySet()){
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(e.getValue()));
                Validate.validState(image.getWidth() == 64, "Must be 64 pixels wide");
                Validate.validState(image.getHeight() == 64, "Must be 64 pixels high");
                DynamicTexture icon = new DynamicTexture(image.getWidth(), image.getHeight());
                Minecraft.getMinecraft().getTextureManager().loadTexture(
                        new ResourceLocation(ModJobs.MOD_ID, "jobs/" + e.getKey()), icon);
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), icon.getTextureData(), 0, image.getWidth());
                icon.updateDynamicTexture();
            }
            catch (IOException exception) {
                ModJobs.info("Error receiving icon from network ! (job: " + e.getKey() + ")", true);
            }
            catch (IllegalStateException exception) {
                ModJobs.info("Icon for job <" + e.getKey() + "> must be 64x64 !", true);
            }
        }
    }
}
