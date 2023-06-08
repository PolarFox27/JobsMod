package net.polarfox27.jobs.data.registry.unlock;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.BlockBlockedData;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.Type;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockBlockedRegistry {

    private final Map<String, List<BlockBlockedData>> DATA = new HashMap<>();
    private final Type type;


    /**
     * Creates a Block Blocked Registry
     * @param type the Registry type
     */
    public BlockBlockedRegistry(Type type) {
        this.type = type;
    }

    /**
     * Reads the Block Blocked Registry from a byte buffer
     * @param buf the buffer where to read
     */
    public BlockBlockedRegistry(FriendlyByteBuf buf){
        this.type = Type.byCode(buf.readInt());
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            List<BlockBlockedData> list = new ArrayList<>();
            int length = buf.readInt();
            for(int j = 0; j < length; j++)
                list.add(new BlockBlockedData(buf));
            DATA.put(job, list);
        }
    }

    public Type getType() {
        return type;
    }

    /**
     * Adds a blocked data for a job
     * @param job the job
     * @param data the data to add to the job
     */
    public void addBlockedData(String job, BlockBlockedData data){
        if(!DATA.containsKey(job))
            DATA.put(job, new ArrayList<>());
        DATA.get(job).add(data);
    }

    /**
     * @param job the job from which to get the blocked data
     * @return a list of the blocked data
     */
    public List<BlockBlockedData> getBlockedData(String job){
        return DATA.getOrDefault(job, new ArrayList<>());
    }

    /**
     * Clears the data
     */
    public void clear(){
        this.DATA.clear();
    }

    /**
     * Writes the Block Blocked Registry to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(FriendlyByteBuf buf){
        buf.writeInt(type.code);
        buf.writeInt(DATA.size());
        for(Map.Entry<String, List<BlockBlockedData>> e : DATA.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(BlockBlockedData data : e.getValue())
                data.writeToBytes(buf);
        }
    }

    /**
     * Checks if a player is allowed this block state based on its jobs levels
     * @param jobs the jobs data of the player
     * @param state the stack to check
     * @return true if the player is allowed the stack
     */
    public boolean isAllowed(PlayerJobs jobs, BlockState state){
        for(Map.Entry<String, List<BlockedData.BlockBlockedData>> e : this.DATA.entrySet())
            for(BlockedData.BlockBlockedData d : e.getValue()) {
                if (d.matches(state)) {
                    return d.getLevel() <= jobs.getLevelByJob(e.getKey());
                }
            }
        return true;
    }
}
