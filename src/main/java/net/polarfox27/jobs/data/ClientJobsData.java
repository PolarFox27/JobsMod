package net.polarfox27.jobs.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.world.item.ItemStack;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.data.registry.TranslationData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.BlockedData;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.UnlockStack;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;
import net.polarfox27.jobs.gui.GuiGainXP;
import net.polarfox27.jobs.gui.screens.GuiLevelUp;

import java.util.*;
import java.util.stream.Stream;

public class ClientJobsData {

    public static LevelData JOBS_LEVELS = new LevelData();
    public static Map<BlockedData.Type, ItemBlockedRegistry> BLOCKED_ITEMS_REGISTRIES = new HashMap<>();
    public static Map<BlockedData.Type, BlockBlockedRegistry> BLOCKED_BLOCKS_REGISTRIES = new HashMap<>();

    public static Set<XPRegistry<? extends XPData>> XP_REGISTRIES = new HashSet<>();

    public static Map<String, DynamicTexture> JOBS_ICONS = new HashMap<>();

    public static TranslationData TRANSLATIONS = null;

    public static GuiGainXP.GuiAddXpInfos addXPInfos = new GuiGainXP.GuiAddXpInfos();
    public static final List<ItemStack> CURRENT_REWARDS = new ArrayList<>();
    public static PlayerJobs playerJobs = null;

    /**
     * Shows the Gain XP GUI
     * @param job the job for which the player received xp
     * @param xpAdded the amount of xp received
     */
    public static void showAddGui(String job, long xpAdded) {
        addXPInfos.addXP(job, xpAdded);
    }

    /**
     * Shows the Level Up GUI
     * @param job the job for which the player has received xp
     * @param previousLevel the level the player was at before leveling up
     */
    public static void showLevelUpGui(String job, int previousLevel) {
        Minecraft.getInstance().setScreen(new GuiLevelUp(job, previousLevel));
    }


    /**
     * Returns the XPData of the registry sorted by the amount of xp they can give to the player
     * @param job the job for which the XPData give xp
     * @param level the level of the player
     * @param registry the registry to sort
     * @return a sorted list of XPData, ready to be rendered in the GUI
     */
    public static List<XPData> getOrderedXPFromRegistry(String job, int level, XPRegistry<? extends XPData> registry){
        List<XPData> unordered = registry.getXPDataByJob(job)
                .stream()
                .map(x -> (XPData)x)
                .filter(x -> !x.createStack().isEmpty())
                .toList();
        List<XPData> ordered = new ArrayList<>();
        for(XPData x : unordered){
            boolean flag = true;
            for(XPData y : ordered){
                if(x.getXPByLevel(level) > y.getXPByLevel(level)){
                    ordered.add(ordered.indexOf(y), x);
                    flag = false;
                    break;
                }
            }
            if(flag)
                ordered.add(x);
        }
        return ordered;
    }

    /**
     * Returns all the Blocked Blocks and Crafts sorted by the level at which they are unlocked
     * @param job the job for which the Blocks and Crafts are blocked
     * @return a sorted list of UnlockStacks, ready to be rendered in the GUI
     */
    public static List<UnlockStack> getUnlockedStacksSorted(String job){
        List<UnlockStack> blockedItems = new ArrayList<>(Stream.concat(
                BLOCKED_ITEMS_REGISTRIES.values()
                                .stream()
                                .flatMap(r -> r.getBlockedData(job).stream())
                                .map(BlockedData.ItemBlockedData::createUnlockStack),
                BLOCKED_BLOCKS_REGISTRIES.values()
                                .stream()
                                .flatMap(r -> r.getBlockedData(job).stream())
                                .map(BlockedData.BlockBlockedData::createUnlockStack))
                .sorted().toList());

        boolean flag = true;
        while(flag){
            for(int i = 0; i < blockedItems.size()-1; i++){
                UnlockStack merged = blockedItems.get(i).merge(blockedItems.get(i+1));
                if(merged != null){
                    blockedItems.set(i, merged);
                    blockedItems.remove(i+1);
                    break;
                }
                if(i == blockedItems.size() - 2)
                    flag = false;
            }
        }
        return blockedItems;
    }

    /**
     * Gets the locale translated name of a Job
     * @param job the job to translate
     * @return the locale name of the job
     */
    public static String getJobName(String job){
        return TRANSLATIONS.getTranslation(job,
                Minecraft.getInstance()
                        .getLanguageManager()
                        .getSelected()
                        .getCode());
    }
}
