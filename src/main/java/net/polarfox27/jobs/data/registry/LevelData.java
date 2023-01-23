package net.polarfox27.jobs.data.registry;

import net.minecraft.network.PacketBuffer;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LevelData {

    private final Map<String, Levels> LEVELS_BY_JOB = new HashMap<>();

    public LevelData(){
    }

    public LevelData(PacketBuffer buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            int length = buf.readInt();
            String job = buf.readUtf(length);
            Levels levels = new Levels(buf);
            this.LEVELS_BY_JOB.put(job, levels);
        }
    }

    public boolean exists(String job){
        return this.LEVELS_BY_JOB.containsKey(job);
    }

    public Set<String> getJobs(){
        return this.LEVELS_BY_JOB.keySet();
    }

    public void addJobLevel(String job, long[] levels) {
        this.LEVELS_BY_JOB.put(job, new Levels(levels));
    }

    public int getMaxLevel(String job){
        if(!exists(job))
            return 0;
        return this.LEVELS_BY_JOB.get(job).getMaxLevel();
    }

    public long getXPForLevel(String job, int level){
        if(!exists(job))
            return 0;
        Levels levelsData = this.LEVELS_BY_JOB.get(job);
        if(level > levelsData.XP_BY_LEVEL.length || level < 1)
            return 0;
        return levelsData.getXPForLevel(level);
    }

    public long getTotalXPForLevel(String job, int level){
        if(!exists(job) || level < 1)
            return 0;
        Levels levelsData = this.LEVELS_BY_JOB.get(job);
        int lvl = JobsUtil.clamp(level, 1, levelsData.getMaxLevel());
        return levelsData.getTotalXPForLevel(lvl);
    }

    public long getTotalXPForJob(String job){
        return this.getTotalXPForLevel(job, getMaxLevel(job));
    }

    public int getLevelFromTotal(String job, long total){
        if(!exists(job) || total < 1)
            return 0;
        return this.LEVELS_BY_JOB.get(job).getLevelFromTotal(total);
    }

    public long getCurrentXPFromTotal(String job, long total){
        if(!exists(job) || total < 1)
            return 0;
        return this.LEVELS_BY_JOB.get(job).getCurrentXPFromTotal(total);
    }

    public long getMissingXPFromTotal(String job, long total){
        if(!exists(job) || total < 1)
            return 0;
        return this.LEVELS_BY_JOB.get(job).getMissingXPFromTotal(total);
    }

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(this.LEVELS_BY_JOB.size());
        for(Map.Entry<String, Levels> e : this.LEVELS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            e.getValue().writeToBytes(buf);
        }
    }

    public void clear(){
        this.LEVELS_BY_JOB.clear();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Levels & Jobs : \n");
        for(Map.Entry<String, Levels> e : LEVELS_BY_JOB.entrySet())
            b.append("  -  ").append(e.getKey()).append(" : ").append(Arrays.toString(e.getValue().XP_BY_LEVEL)).append("\n");
        return b.toString();
    }

    private static class Levels{
        private final long[] XP_BY_LEVEL;

        private Levels(long[] XP_BY_LEVEL) {
            this.XP_BY_LEVEL = XP_BY_LEVEL;
        }

        public Levels(PacketBuffer buf) {
            this.XP_BY_LEVEL = JobsUtil.fromBytes(JobsUtil.readByteArray(buf));
        }

        private int getMaxLevel(){
            return this.XP_BY_LEVEL.length;
        }

        private long getXPForLevel(int level){
            if(level > this.XP_BY_LEVEL.length || level < 1)
                return 0;
            return this.XP_BY_LEVEL[level-1];
        }

        private long getTotalXPForLevel(int level){
            if(level < 1)
                return 0;
            int lvl = JobsUtil.clamp(level, 1, this.getMaxLevel());
            return Arrays.stream(JobsUtil.subarray(XP_BY_LEVEL, 0, level)).sum();
        }

        private int getLevelFromTotal(long total){
            for(int i = getMaxLevel(); i >= 0; i--)
                if(getTotalXPForLevel(i) <= total)
                    return i;
            return 0;
        }

        private long getCurrentXPFromTotal(long total){
            for(int i = getMaxLevel(); i >= 0; i--)
                if(getTotalXPForLevel(i) <= total)
                    return total - getTotalXPForLevel(i);
            return 0;
        }

        private long getMissingXPFromTotal(long total){
            for(int i = getMaxLevel()-1; i >= 0; i--)
                if(getTotalXPForLevel(i) <= total)
                    return getTotalXPForLevel(i+1) - total;
            return 0;
        }

        private void writeToBytes(PacketBuffer buf){
            byte[] array = JobsUtil.toBytes(XP_BY_LEVEL);
            JobsUtil.writeByteArray(array, buf);
        }
    }

}
