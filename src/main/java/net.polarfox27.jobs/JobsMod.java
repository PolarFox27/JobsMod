package net.polarfox27.jobs;

import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(JobsMod.MOD_ID)
public class JobsMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "jobs";

    public JobsMod() {
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event){
    }
}