package net.polarfox27.jobs.data.registry;

import io.netty.buffer.ByteBuf;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TranslationData {

    private final Map<String, Map<String, String>> data = new HashMap<>();
    private static final String default_code = "en_us";

    /**
     * Empty Constructor
     */
    public TranslationData(){}

    /**
     * Reads Translations from a byte buffer
     * @param buf the buffer where to read
     */
    public TranslationData(ByteBuf buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readFromBuf(buf);
            data.put(job, new HashMap<>());
            int size2 = buf.readInt();
            for(int j = 0; j < size2; j++){
                String code = JobsUtil.readFromBuf(buf);
                String translation = JobsUtil.readFromBuf(buf);
                data.get(job).put(code, translation);
            }
        }
    }

    /**
     * Delete all the translations and initialize with the jobs given
     * @param jobs the new jobs that will be translated
     */
    public void reset(Set<String> jobs){
        this.data.clear();
        for(String s : jobs)
            data.put("job." + s, new HashMap<>());
    }

    /**
     * @param job the name to translate
     * @return the default translation
     */
    public String getDefaultTranslation(String job){
        if(!data.containsKey("job." + job))
            return "none";
        return data.get("job." + job).getOrDefault(default_code, "none");
    }

    /**
     * @param job the name to translate
     * @param code the language code to translate to
     * @return the translated name of the job
     */
    public String getTranslation(String job, String code){
        if(!data.containsKey("job." + job))
            return "none";
        return data.get("job." + job).getOrDefault(code, getDefaultTranslation(job));
    }

    /**
     * Adds a new translation
     * @param key the job name
     * @param code the language code
     * @param translation the actual translation
     */
    public void addTranslation(String key, String code, String translation){
        if(!this.data.containsKey(key))
            return;
        this.data.get(key).put(code, translation);
    }

    /**
     * Writes the translations to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(ByteBuf buf){
        buf.writeInt(this.data.size());
        for(Map.Entry<String, Map<String, String>> e : this.data.entrySet()){
            JobsUtil.writeToBuf(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(Map.Entry<String, String> e2 : e.getValue().entrySet()){
                JobsUtil.writeToBuf(e2.getKey(), buf);
                JobsUtil.writeToBuf(e2.getValue(), buf);
            }
        }
    }
}
