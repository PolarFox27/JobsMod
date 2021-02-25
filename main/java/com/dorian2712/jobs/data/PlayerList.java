package com.dorian2712.jobs.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class PlayerList {
	
	private static Map<String, JobsInfo> list = new HashMap<String, JobsInfo>();
	
	public static void addNewPlayer(String uuid)
	{
		put(uuid, new JobsInfo());
	}
	
	public static void put(String uuid, JobsInfo jobs)
	{
		list.put(uuid, jobs);
	}
	
	public static JobsInfo getJobsOfPlayer(String uuid)
	{
		return list.get(uuid);
	}
	
	public static int getPlayersNumber()
	{
		return list.size();
	}
	
	public static Set<Entry<String, JobsInfo>> getEntrySet()
	{
		return list.entrySet();
	}
	
	public static boolean containPlayer(String uuid)
	{
		return list.containsKey(uuid);
	}

}
