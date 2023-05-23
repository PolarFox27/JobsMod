package net.polarfox27.jobs.data;

public class ModConfig {
	
	private boolean isHard;
	
	public ModConfig() 
	{
		
	}
	
	public ModConfig setHardMode(boolean hard)
	{
		this.isHard = hard;
		return this;
	}
	
	public boolean isHard() 
	{
		return isHard;
	}
	
	public static ModConfig defaultConfig()
	{
		return new ModConfig().setHardMode(false);
	}

}
