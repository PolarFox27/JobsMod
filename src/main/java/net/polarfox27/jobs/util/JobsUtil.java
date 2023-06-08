package net.polarfox27.jobs.util;

import com.google.common.primitives.Longs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JobsUtil {

    /**
     * Clamps the value between the min and max and returns it.
     * @param value the value to clamp
     * @param min the minimum allowed value (included)
     * @param max the minimum allowed value (included)
     * @return the clamped value
     */
    public static long clamp(long value, long min, long max) {
        if(min == max) return min;
        if(value < min) return min;
        return Math.min(value, max);
    }

    /**
     * Clamps the value between the min and max and returns it.
     * @param value the value to clamp
     * @param min the minimum allowed value (included)
     * @param max the minimum allowed value (included)
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        if(min == max) return min;
        if(value < min) return min;
        return Math.min(value, max);
    }

    /**
     * Concatenates the byte arrays into a single array
     * @param arrays the arrays to concatenate
     * @return the concatenated array
     */
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

    /**
     * Concatenates the long arrays into a single array
     * @param arrays the arrays to concatenate
     * @return the concatenated array
     */
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

    /**
     * Creates a sub array of the given array
     * @param array the array
     * @param index the starting index
     * @param length the length of the sub-array
     * @return the sub-array
     */
    public static byte[] subArray(byte[] array, int index, int length){
        byte[] result = new byte[length];
        System.arraycopy(array, index, result, 0, length);
        return result;
    }

    /**
     * Creates a sub array of the given array
     * @param array the array
     * @param index the starting index
     * @param length the length of the sub-array
     * @return the sub-array
     */
    public static long[] subArray(long[] array, int index, int length){
        long[] result = new long[length];
        System.arraycopy(array, index, result, 0, length);
        return result;
    }

    /**
     * converts a long array to an equivalent byte array
     * @param values the array to convert
     * @return the converted array
     */
    public static byte[] toBytes(long[] values){
        byte[] result = new byte[0];
        for (long value : values)
            result = concat(result, Longs.toByteArray(value));
        return result;
    }

    /**
     * converts a byte array back to an equivalent long array
     * @param bytes the array to convert back
     * @return the converted array
     */public static long[] fromBytes(byte[] bytes){
        byte[] array = bytes;
        long[] result = new long[0];
        while(array.length % 8 != 0)
            array = concat(array, new byte[]{0});
        for(int i = 0; i < bytes.length; i += 8)
            result = concat(result, new long[]{Longs.fromByteArray(subArray(array, i, 8))});
        return result;
    }

    /**
     * Creates an item stack representing the entity (used for Jobs interface)
     * @param entity the entity to represent
     * @return the item stack representing the entity
     */
    public static ItemStack createEntityItemStack(EntityType<? extends Entity> entity){
        ItemStack s = new ItemStack(Items.BARRIER);
        s.getOrCreateTag().putString("entity", EntityType.getKey(entity).toString());
        return s;
    }

    /**
     * Constructs an item stack.
     * @param item the item in the itemStack
     * @param count the amount of item
     * @param metadata the metadata of the stack (e.g. item durability)
     * @return the item stack
     */
    public static ItemStack itemStack(Item item, int count, int metadata){
        ItemStack s = new ItemStack(item, count);
        if(metadata != 0)
            s.setDamageValue(metadata);
        return s;
    }

    /**
     * Writes a string to a byte buffer
     * @param s the string to write
     * @param buf the buffer where to write
     */
    public static void writeString(String s, FriendlyByteBuf buf){
        buf.writeInt(s.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(s);
    }

    /**
     * Reads a string from a byte buffer
     * @param buf the buffer to read from
     * @return the read string
     */
    public static String readString(FriendlyByteBuf buf){
        int length = buf.readInt();
        return buf.readUtf(length);
    }

    /**
     * Writes a byte array to a byte buffer
     * @param array the array to write
     * @param buf the buffer where to write
     */
    public static void writeByteArray(byte[] array, FriendlyByteBuf buf){
        buf.writeInt(array.length);
        buf.writeByteArray(array);
    }

    /**
     * Reads a byte array from a byte buffer
     * @param buf the buffer to read from
     * @return the read byte array
     */
    public static byte[] readByteArray(FriendlyByteBuf buf){
        int length = buf.readInt();
        return buf.readByteArray(length);
    }
}
