package net.polarfox27.jobs.data.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.registries.ForgeRegistries;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.*;
import java.util.stream.Collectors;

public abstract class XPRegistry <T extends XPData> {

    private final Map<String, List<T>> data = new HashMap<>();
    private final String name;
    private final Item icon;
    private final Type type;

    public XPRegistry(Type type, String name, Item icon) {
        this.name = name;
        this.icon = icon;
        this.type = type;
    }

    private XPRegistry(PacketBuffer buf, Type type) {
        this.type = type;
        this.name = JobsUtil.readString(buf);
        this.icon = Item.byId(buf.readInt());
    }

    public List<T> getXPDataByJob(String job){
        if(!data.containsKey(job))
            return new ArrayList<>();
        return data.get(job);
    }

    public Set<String> getJobs(){
        return data.keySet();
    }

    public String getFileName(){
        return "xp_" + name.toLowerCase() + ".json";
    }

    public void addDataForJob(String job, T xpdata){
        if(!data.containsKey(job))
            data.put(job, new ArrayList<>());
        data.get(job).add(xpdata);
    }

    public List<T> getXPForLevel(String job, int level){
        if(!data.containsKey(job))
            return new ArrayList<>();
        return data.get(job).stream().filter(x -> x.getXPByLevel(level) > 0).collect(Collectors.toList());
    }

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(type.id);
        JobsUtil.writeString(name, buf);
        buf.writeInt(Item.getId(icon));
        buf.writeInt(data.size());
        for(Map.Entry<String, List<T>> entry : data.entrySet()){
            JobsUtil.writeString(entry.getKey(), buf);
            buf.writeInt(entry.getValue().size());
            for(T xpdata : entry.getValue()){
                xpdata.writeToBytes(buf);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Item getIcon() {
        return icon;
    }

    public void clear(){
        this.data.clear();
    }

    public static XPRegistry<? extends XPData> fromBytes(PacketBuffer buf){
        Type type = Type.byID(buf.readInt());
        if(type == null)
            return null;
        switch (type){
            case ITEM:
                return new ItemXPRegistry(buf);
            case BLOCK:
                return new BlockXPRegistry(buf);
            case ENTITY:
                return new EntityXPRegistry(buf);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(name).append(" XP Registry (icon = ")
                                                 .append(ForgeRegistries.ITEMS.getKey(icon).toString())
                                                 .append(") :\n");
        for(Map.Entry<String, List<T>> e : data.entrySet()){
            s.append(" * ").append(e.getKey()).append(" ->\n");
            for(T xp : e.getValue()){
                s.append("    - ").append(xp.toString());
            }
        }
        return s.toString();
    }

    public static final class ItemXPRegistry extends XPRegistry<XPData.ItemXPData>{
        public ItemXPRegistry(String name, Item icon) {
            super(Type.ITEM, name, icon);
        }

        public ItemXPRegistry(PacketBuffer buf){
            super(buf, Type.ITEM);
            int size = buf.readInt();
            for(int i = 0; i < size; i++){
                String job = JobsUtil.readString(buf);
                int amount = buf.readInt();
                for(int j = 0; j < amount; j++)
                    this.addDataForJob(job, new XPData.ItemXPData(buf));
            }
        }

        public long getXPByLevelAndJob(ItemStack stack, int level, String job){
            return getXPDataByJob(job).stream()
                                      .filter(x -> x.matches(stack))
                                      .findFirst()
                                      .map(x -> x.getXPByLevel(level))
                                      .orElse(0L);
        }

        public void registerItemXP(String job, Item item, int metadata, long[] xps){
            this.addDataForJob(job, new XPData.ItemXPData(xps, item, metadata));
        }
    }
    public static final class BlockXPRegistry extends XPRegistry<XPData.BlockXPData>{
        public BlockXPRegistry(String name, Item icon) {
            super(Type.BLOCK, name, icon);
        }

        public BlockXPRegistry(PacketBuffer buf){
            super(buf, Type.BLOCK);
            int size = buf.readInt();
            for(int i = 0; i < size; i++){
                String job = JobsUtil.readString(buf);
                int amount = buf.readInt();
                for(int j = 0; j < amount; j++)
                    this.addDataForJob(job, new XPData.BlockXPData(buf));
            }
        }

        public long getXPByLevelAndJob(BlockState state, int level, String job){
            return getXPDataByJob(job).stream()
                    .filter(x -> x.matches(state))
                    .findFirst()
                    .map(x -> x.getXPByLevel(level))
                    .orElse(0L);
        }

        public void registerBlockXP(String job, Block block, long[] xps){
            this.addDataForJob(job, new XPData.BlockXPData(xps, block));
        }

        public void registerCropXP(String job, Block block, long[] xps){
            if(!(block instanceof CropsBlock))
                return;
            this.addDataForJob(job, new XPData.BlockXPData(xps, block, true));
        }
    }

    public static final class EntityXPRegistry extends XPRegistry<XPData.EntityXPData>{
        public EntityXPRegistry(String name, Item icon) {
            super(Type.ENTITY, name, icon);
        }

        public EntityXPRegistry(PacketBuffer buf){
            super(buf, Type.ENTITY);
            int size = buf.readInt();
            for(int i = 0; i < size; i++){
                String job = JobsUtil.readString(buf);
                int amount = buf.readInt();
                for(int j = 0; j < amount; j++)
                    this.addDataForJob(job, new XPData.EntityXPData(buf));
            }
        }

        public long getXPByLevelAndJob(EntityType<? extends Entity> entity, int level, String job){
            return getXPDataByJob(job).stream()
                    .filter(x -> x.matches(entity))
                    .findFirst()
                    .map(x -> x.getXPByLevel(level))
                    .orElse(0L);
        }

        public void registerEntityXP(String job, EntityType<? extends Entity> entity, long[] xps){
            this.addDataForJob(job, new XPData.EntityXPData(xps, entity));
        }
    }

    private static enum Type{
        ITEM(0),
        BLOCK(1),
        ENTITY(2);

        public final int id;
        Type(int id){
            this.id = id;
        }

        public static Type byID(int id){
            for(Type t : Type.values())
                if(t.id == id)
                    return t;
            return null;
        }
    }
}
