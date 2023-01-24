package net.polarfox27.jobs.util.config;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        if(!folder.exists())
            folder.mkdirs();
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
    public static void toBytes(PacketBuffer buf, Map<String, byte[]> map){
        buf.writeInt(map.size());
        for(Map.Entry<String, byte[]> e : map.entrySet()){
            buf.writeInt(e.getKey().getBytes(StandardCharsets.UTF_8).length);
            buf.writeUtf(e.getKey());
            buf.writeInt(e.getValue().length);
            buf.writeByteArray(e.getValue());
        }
    }

    /**
     * Reads the icons from a Byte Buffer
     * @param buf the buffer where to read
     * @return the map mapping the jobs name to the jobs icons
     */
    public static Map<String, byte[]> fromBytes(PacketBuffer buf){
        Map<String, byte[]> map = new HashMap<>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            int jobLength = buf.readInt();
            String job = buf.readUtf(jobLength);
            int arrayLength = buf.readInt();
            byte[] array = buf.readByteArray(arrayLength);
            map.put(job, array);
        }
        return map;
    }

    /**
     * Converts the map of images to a map of DynamicTexture usable in GUIs.
     * @param map the map mapping the jobs name to the jobs icons
     * @return the map mapping the jobs name to the dynamic textures
     */
    @OnlyIn(Dist.CLIENT)
    public static Map<String, DynamicTexture> convertToClient(Map<String, byte[]> map){
        Map<String, DynamicTexture> newMap = new HashMap<>();
        for(Map.Entry<String, byte[]> e : map.entrySet()){
            try {
                NativeImage image = NativeImage.read(new ByteArrayInputStream(e.getValue()));
                Validate.validState(image.getWidth() == 64, "Must be 64 pixels wide");
                Validate.validState(image.getHeight() == 64, "Must be 64 pixels high");
                newMap.put(e.getKey(), new DynamicTexture(image));
            }
            catch (IOException exception) {
                ModJobs.info("Error receiving icon from network ! (job: " + e.getKey() + ")", true);
            }
            catch (IllegalStateException exception) {
                ModJobs.info("Icon for job <" + e.getKey() + "> must be 64x64 !", true);
            }
        }
        return newMap;
    }
}
