package net.polarfox27.jobs.util;

import com.google.common.primitives.Longs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JobsUtil {

    public static long clamp(long value, long min, long max) {
        if(min == max) return min;
        if(value < min) return min;
        if(value > max) return max;
        else return value;
    }
    public static int clamp(int value, int min, int max) {
        if(min == max) return min;
        if(value < min) return min;
        if(value > max) return max;
        else return value;
    }

    public static byte[] concat(byte[]... arrays){
        byte[] result = new byte[Arrays.stream(arrays).mapToInt((array) -> array.length).sum()];
        int i = 0;
        int index = 0;
        while(i < arrays.length){
            for(int j = 0; j < arrays[i].length; j++) {
                result[index] = arrays[i][j];
                index++;
            }
            i++;
        }
        return result;
    }

    public static long[] concat(long[]... arrays){
        long[] result = new long[Arrays.stream(arrays).mapToInt((array) -> array.length).sum()];
        int i = 0;
        int index = 0;
        while(i < arrays.length){
            for(int j = 0; j < arrays[i].length; j++) {
                result[index] = arrays[i][j];
                index++;
            }
            i++;
        }
        return result;
    }

    public static byte[] subarray(byte[] array, int index, int length){
        byte[] result = new byte[length];
        for(int i = 0; i < length; i++)
            result[i] = array[index+i];
        return result;
    }

    public static long[] subarray(long[] array, int index, int length){
        long[] result = new long[length];
        for(int i = 0; i < length; i++)
            result[i] = array[index+i];
        return result;
    }

    public static byte[] toBytes(long[] values){
        byte[] result = new byte[0];
        for(int i = 0; i < values.length; i++)
            result = concat(result, Longs.toByteArray(values[i]));
        return result;
    }

    public static long[] fromBytes(byte[] bytes){
        byte[] array = bytes;
        long[] result = new long[0];
        while(array.length % 8 != 0)
            array = concat(array, new byte[]{0});
        for(int i = 0; i < bytes.length; i += 8)
            result = concat(result, new long[]{Longs.fromByteArray(subarray(array, i, 8))});
        return result;
    }

    public static ItemStack createEntityItemStack(EntityType<? extends Entity> entity){
        ItemStack s = new ItemStack(Items.BARRIER);
        s.getOrCreateTag().putString("entity", EntityType.getKey(entity).toString());
        return s;
    }

    public static EntityType<? extends Entity> getEntityFromItemStack(ItemStack stack){
        if(stack.getItem() != Items.BARRIER)
            return null;
        if(!stack.getOrCreateTag().contains("entity"))
            return null;
        return EntityType.byString(stack.getOrCreateTag().getString("entity")).orElse(null);
    }

    public static ItemStack itemStack(Item item, int count, int metadata){
        ItemStack s = new ItemStack(item, count);
        if(metadata != 0)
            s.setDamageValue(metadata);
        return s;
    }

    public static void writeString(String s, PacketBuffer buf){
        buf.writeInt(s.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(s);
    }

    public static String readString(PacketBuffer buf){
        int length = buf.readInt();
        return buf.readUtf(length);
    }

    public static void writeByteArray(byte[] array, PacketBuffer buf){
        buf.writeInt(array.length);
        buf.writeByteArray(array);
    }

    public static byte[] readByteArray(PacketBuffer buf){
        int length = buf.readInt();
        return buf.readByteArray(length);
    }
}
