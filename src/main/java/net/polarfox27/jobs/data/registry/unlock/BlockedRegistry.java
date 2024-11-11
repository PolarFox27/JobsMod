package net.polarfox27.jobs.data.registry.unlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.JobsUtil;

/**
 * Blocked Data Registry
 *
 * @param <T> The blocked data type (Blocks or Items)
 * @param <S> The type that can be matched against the blocked data (Blockstates for blocks, Itemstacks for items).
 */
public abstract class BlockedRegistry<T extends BlockedData<S>, S> {
    private final Map<String, List<T>> DATA = new HashMap<>();
    private final BlockedData.Type type;

    /**
     * Creates a Blocked Registry
     * @param type the Registry type
     */
    public BlockedRegistry(BlockedData.Type type) {
        this.type = type;
    }

    /**
     * Reads the Blocked Registry from a byte buffer
     * @param buf the buffer where to read
     */
    public BlockedRegistry(FriendlyByteBuf buf){
        this.type = BlockedData.Type.byCode(buf.readInt());
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            List<T> list = new ArrayList<>();
            int length = buf.readInt();
            for(int j = 0; j < length; j++)
                list.add(readData(buf));
            DATA.put(job, list);
        }
    }

    /**
     * Reads the blocked data from the buffer.
     *
     * @param buf The buffer where to read
     * @return the read blocked data
     */
    public abstract T readData(FriendlyByteBuf buf);

    public BlockedData.Type getType() {
        return type;
    }

    /**
     * Adds a blocked data for a job
     * @param job the job
     * @param data the data to add to the job
     */
    public void addBlockedData(String job, T data){
        if(!DATA.containsKey(job))
            DATA.put(job, new ArrayList<>());
        DATA.get(job).add(data);
    }

    /**
     * @param job the job from which to get the blocked data
     * @return a list of the blocked data
     */
    public List<T> getBlockedData(String job){
        return DATA.getOrDefault(job, new ArrayList<>());
    }

    /**
     * Clears the data.
     */
    public void clear(){
        this.DATA.clear();
    }

    /**
     * Writes the Blocked Registry to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(FriendlyByteBuf buf){
        buf.writeInt(type.code);
        buf.writeInt(DATA.size());
        for(Map.Entry<String, List<T>> e : DATA.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(T data : e.getValue())
                data.writeToBytes(buf);
        }
    }

    /**
     * Getter for all the blocked data entries.
     *
     * @return the set of all blocked data entries, per job.
     */
    protected Set<Map.Entry<String, List<T>>> getAllEntries(){
        return this.DATA.entrySet();
    }

    /**
     * Checks if a player is allowed this object based on its jobs levels
     * @param jobs the jobs data of the player
     * @param object the object to check
     * @return true if the player is allowed the object
     */
    public boolean isBlocked(PlayerJobs jobs, S object){
        if(jobs == null || object == null)
            return false;
        boolean found = false;
        for(Map.Entry<String, List<T>> e : this.getAllEntries())
            for(T d : e.getValue()) {
                if (d.matches(object)) {
                    found = true;
                    if(d.getLevel() <= jobs.getLevelByJob(e.getKey()))
                        return false;
                }
            }
        return found;
    }
}
