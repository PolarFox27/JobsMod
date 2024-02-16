package net.polarfox27.jobs.data.registry.unlock;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public class UnlockStack implements Comparable<UnlockStack>{

    private final int level;
    private final ItemStack stack;
    private final HashSet<BlockedData.Type> types;

    /**
     * Constructs an Unlock Stack to be shown in the GUI
     * @param level the level at which it is unlocked
     * @param stack the stack rendered in the GUI
     * @param type the type of UnlockStack it is
     */
    public UnlockStack(int level, ItemStack stack, BlockedData.Type type) {
        this.level = level;
        this.stack = stack;
        this.types = new HashSet<>(List.of(type));
    }

    /**
     * Comparator used to sort them in the GUI
     * @param o the object to be compared.
     * @return this.level - o.level
     */
    @Override
    public int compareTo(UnlockStack o) {
        int i = Integer.compare(this.level, o.level);
        if(i != 0)
            return i;
        else{
            return Integer.compare(this.stack.getItem().hashCode(), o.stack.getItem().hashCode());
        }
    }

    public int getLevel() {
        return level;
    }

    public ItemStack getStack() {
        return stack;
    }

    /**
     * @return the names of the unlock types of this unlock stack.
     */
    public List<String> getTypes() {
        return types.stream().map(x -> x.name().toLowerCase()).toList();
    }

    /**
     * Adds the unlock types of the other UnlockStack if they have the same level and Item.
     * @param other the other unlock stack to merge with this one.
     * @return this.
     */
    public UnlockStack merge(UnlockStack other){
        if(compareTo(other) != 0 || !this.stack.getItem().equals(other.stack.getItem()))
            return null;
        this.types.addAll(other.types);
        return this;
    }
}
