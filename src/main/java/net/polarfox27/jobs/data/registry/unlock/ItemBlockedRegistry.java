package net.polarfox27.jobs.data.registry.unlock;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import io.netty.buffer.ByteBuf;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.ItemBlockedData;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.Type;
import net.polarfox27.jobs.util.JobsUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBlockedRegistry {

    private final Map<String, List<ItemBlockedData>> DATA = new HashMap<>();
    private final Type type;


    /**
     * Creates an Item Blocked Registry
     * @param type the Registry type
     */
    public ItemBlockedRegistry(Type type) {
        this.type = type;
    }

    /**
     * Reads the Item Blocked Registry from a byte buffer
     * @param buf the buffer where to read
     */
    public ItemBlockedRegistry(ByteBuf buf){
        this.type = Type.byCode(buf.readInt());
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readFromBuf(buf);
            List<ItemBlockedData> list = new ArrayList<>();
            int length = buf.readInt();
            for(int j = 0; j < length; j++)
                list.add(new ItemBlockedData(buf));
            DATA.put(job, list);
        }
    }

    public Type getType() {
        return type;
    }

    /**
     * Adds a blocked data for a job
     * @param job the job
     * @param data the data to add ti the job
     */
    public void addBlockedData(String job, ItemBlockedData data){
        if(!DATA.containsKey(job))
            DATA.put(job, new ArrayList<>());
        DATA.get(job).add(data);
    }

    /**
     * @param job the job from which to get the blocked data
     * @return a list of the blocked data
     */
    public List<ItemBlockedData> getBlockedData(String job){
        return DATA.getOrDefault(job, new ArrayList<>());
    }

    /**
     * Clears the data
     */
    public void clear(){
        this.DATA.clear();
    }

    /**
     * Writes the Item Blocked Registry to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(ByteBuf buf){
        buf.writeInt(type.code);
        buf.writeInt(DATA.size());
        for(Map.Entry<String, List<ItemBlockedData>> e : DATA.entrySet()){
            JobsUtil.writeToBuf(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(ItemBlockedData data : e.getValue())
                data.writeToBytes(buf);
        }
    }

    /**
     * Checks if a player is allowed this stack based on its jobs levels
     * @param jobs the jobs data of the player
     * @param stack the stack to check
     * @return true if the player is allowed the stack
     */
    public boolean isBlocked(PlayerJobs jobs, ItemStack stack){
        for(Map.Entry<String, List<ItemBlockedData>> e : this.DATA.entrySet())
            for(ItemBlockedData d : e.getValue()) {
                if (d.matches(stack)) {
                    return d.getLevel() > jobs.getLevelByJob(e.getKey());
                }
            }
        return false;
    }
}
