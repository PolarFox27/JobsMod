package net.polarfox27.jobs.data.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranslationData {

    private final Map<String, Map<String, String>> data = new HashMap<>();
    private static final String default_code = "en_us";

    public TranslationData(){
    }

    public TranslationData(PacketBuffer buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            data.put(job, new HashMap<>());
            int size2 = buf.readInt();
            for(int j = 0; j < size2; j++){
                String code = JobsUtil.readString(buf);
                String translation = JobsUtil.readString(buf);
                data.get(job).put(code, translation);
            }
        }
    }

    public void reset(Set<String> jobs){
        this.data.clear();
        for(String s : jobs)
            data.put("job." + s, new HashMap<>());
    }

    public String getDefaultTranslation(String job){
        if(!data.containsKey("job." + job))
            return "none";
        return data.get("job." + job).getOrDefault(default_code, "none");
    }

    public String getTranslation(String job, String code){
        if(!data.containsKey("job." + job))
            return "none";
        return data.get("job." + job).getOrDefault(code, getDefaultTranslation(job));
    }

    public void addTranslation(String key, String code, String translation){
        if(!this.data.containsKey(key))
            return;
        this.data.get(key).put(code, translation);
    }

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(this.data.size());
        for(Map.Entry<String, Map<String, String>> e : this.data.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(Map.Entry<String, String> e2 : e.getValue().entrySet()){
                JobsUtil.writeString(e2.getKey(), buf);
                JobsUtil.writeString(e2.getValue(), buf);
            }
        }
    }
}
