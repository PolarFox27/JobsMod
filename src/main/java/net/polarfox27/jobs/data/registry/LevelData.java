package net.polarfox27.jobs.data.registry;

import io.netty.buffer.ByteBuf;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LevelData {

    private final Map<String, Levels> LEVELS_BY_JOB = new HashMap<>();

    /**
     * Empty Constructor
     */
    public LevelData(){
    }

    /**
     * Reads the level data from a byte buffer
     * @param buf the buffer where to read
     */
    public LevelData(ByteBuf buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readFromBuf(buf);
            Levels levels = new Levels(buf);
            this.LEVELS_BY_JOB.put(job, levels);
        }
    }

    /**
     * Checks if a job exists
     * @param job the job to check
     * @return true if the job exists, false otherwise
     */
    public boolean exists(String job){
        return this.LEVELS_BY_JOB.containsKey(job);
    }

    /**
     * @return a set of the jobs name
     */
    public Set<String> getJobs(){
        return this.LEVELS_BY_JOB.keySet();
    }

    /**
     * Adds a job to the level data
     * @param job the job to add
     * @param levels the xp required for each level for that job
     */
    public void addJobLevel(String job, long[] levels) {
        this.LEVELS_BY_JOB.put(job, new Levels(levels));
    }

    /**
     * Gets the maximum level a player can reach in a certain job
     * @param job the job
     * @return the maximum possible level of that job
     */
    public int getMaxLevel(String job){
        if(!exists(job))
            return 0;
        return this.LEVELS_BY_JOB.get(job).getMaxLevel();
    }

    /**
     * Returns the xp needed to reach a level from the level below
     * @param job the job
     * @param level the current level
     * @return the xp needed to go from the level below to that level
     */
    public long getXPForLevel(String job, int level){
        if(!exists(job))
            return 0;
        Levels levelsData = this.LEVELS_BY_JOB.get(job);
        if(level > levelsData.XP_BY_LEVEL.length || level < 1)
            return 0;
        return levelsData.getXPForLevel(level);
    }

    /**
     * Gets the total xp needed to reach a level from level 0
     * @param job the job
     * @param level the current level
     * @return the total xp to reach that level
     */
    public long getTotalXPForLevel(String job, int level){
        if(!exists(job) || level < 1)
            return 0;
        Levels levelsData = this.LEVELS_BY_JOB.get(job);
        int lvl = JobsUtil.clamp(level, 1, levelsData.getMaxLevel());
        return levelsData.getTotalXPForLevel(lvl);
    }

    /**
     * Gets the total xp needed to reach the maximum level of a job
     * @param job the job
     * @return the xp required to max out the job
     */
    public long getTotalXPForJob(String job){
        return this.getTotalXPForLevel(job, getMaxLevel(job));
    }

    /**
     * Gets the player level for a job from its total xp for that job
     * @param job the current job
     * @param total the total xp of the player
     * @return the level the player has reached
     */
    public int getLevelFromTotal(String job, long total){
        if(!exists(job) || total < 1)
            return 0;
        return this.LEVELS_BY_JOB.get(job).getLevelFromTotal(total);
    }

    /**
     * Gets the player progression from its level to  the next for a job from its total xp for that job
     * @param job the current job
     * @param total the total xp of the player
     * @return the xp the player has form its current level
     */
    public long getCurrentXPFromTotal(String job, long total){
        if(!exists(job) || total < 1)
            return 0;
        return this.LEVELS_BY_JOB.get(job).getCurrentXPFromTotal(total);
    }

    /**
     * Writes the levels data to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(ByteBuf buf){
        buf.writeInt(this.LEVELS_BY_JOB.size());
        for(Map.Entry<String, Levels> e : this.LEVELS_BY_JOB.entrySet()){
            JobsUtil.writeToBuf(e.getKey(), buf);
            e.getValue().writeToBytes(buf);
        }
    }

    /**
     * Clears all the levels data
     */
    public void clear(){
        this.LEVELS_BY_JOB.clear();
    }

    /**
     * @return a string representation of the levels data
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Levels & Jobs : \n");
        for(Map.Entry<String, Levels> e : LEVELS_BY_JOB.entrySet())
            b.append("  -  ").append(e.getKey()).append(" : ").append(Arrays.toString(e.getValue().XP_BY_LEVEL)).append("\n");
        return b.toString();
    }

    private static class Levels{
        private final long[] XP_BY_LEVEL;

        /**
         * Creates a new level data for a job
         * @param XP_BY_LEVEL the xp needed for each level of the job
         */
        private Levels(long[] XP_BY_LEVEL) {
            this.XP_BY_LEVEL = XP_BY_LEVEL;
        }

        /**
         * Reads a level data from a byte buffer
         * @param buf the byte buffer
         */
        public Levels(ByteBuf buf) {
            this.XP_BY_LEVEL = JobsUtil.fromBytes(JobsUtil.readByteArray(buf));
        }

        /**
         * @return the max level stored in this level data
         */
        private int getMaxLevel(){
            return this.XP_BY_LEVEL.length;
        }

        /**
         * Gets the xp needed for a level
         * @param level the level to reach
         * @return the xp needed to reach that level
         */
        private long getXPForLevel(int level){
            if(level > this.XP_BY_LEVEL.length || level < 1)
                return 0;
            return this.XP_BY_LEVEL[level-1];
        }

        /**
         * Gets the total xp needed to reach a level from level 0
         * @param level the level to reach
         * @return the total xp needed to reach that level
         */
        private long getTotalXPForLevel(int level){
            if(level < 1)
                return 0;
            int lvl = JobsUtil.clamp(level, 1, this.getMaxLevel());
            return Arrays.stream(JobsUtil.subArray(XP_BY_LEVEL, 0, lvl)).sum();
        }

        /**
         * Gets the level a player has from its total xp
         * @param total the total xp of the player
         * @return the level they are currently at
         */
        private int getLevelFromTotal(long total){
            for(int i = getMaxLevel(); i >= 0; i--)
                if(getTotalXPForLevel(i) <= total)
                    return i;
            return 0;
        }

        /**
         * Gets the xp a player has at their level from its total xp
         * @param total the total xp of the player
         * @return the xp they have at their current level
         */
        private long getCurrentXPFromTotal(long total){
            for(int i = getMaxLevel(); i >= 0; i--)
                if(getTotalXPForLevel(i) <= total)
                    return total - getTotalXPForLevel(i);
            return 0;
        }

        /**
         * Writes the level data to a byte buffer
         * @param buf the buffer where to write
         */
        private void writeToBytes(ByteBuf buf){
            byte[] array = JobsUtil.toBytes(XP_BY_LEVEL);
            JobsUtil.writeByteArray(array, buf);
        }
    }

}
