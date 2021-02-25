# Jobs
A minecraft mod that adds jobs to Minecraft

Description :

This mod adds jobs to Minecraft. There are four of them : Hunter, Magician, Farmer and Miner. 

By doing some actions, the player will gain job experience. You can reach up to level 25 for each job. By passing to the next level of a job, the player will earn rewards and unlock crafts of items !

By pressing the key J, you will open the Jobs menu that tells you what level you are and how to gain xp for each jobs.

 

Compatibility :

This mod can be used on Forge Servers or in Singleplayer Worlds. It is compatible with others mods, but if you discover a bug or a problem, report it.

This mod is also compatible with Mohist Servers.

/!\ Because this mod modify the craft system of minecraft, it may not be compatible with other mods that also modify the crafting system, but it works perfectly fine with JEI.

 

Usage/Config : 

/!\ This mod requires a good knowledge of how JSON files works.

You can find a template config in the branch PolarFox27-misc.

 

On servers :  Start your server once to create the empty JSON config files. You should see yellow message in the command prompt, like this : Warning : File <blockedcrafts.json > not found. An empty file was created.

The config files will be found in the folder /jobs/data/ where is the folder where your server is.

XP Files  :  there are 5 of them :

xp_breakblock.json (config of the xp gained when breaking blocks) 
xp_craftitem.json (config of the xp gained when crafting items) 
xp_smeltitem.json (config of the xp gained when smelting items) 
xp_harvestcrop.json (config of the xp gained when harvesting mature crops) 
xp_killentity.json (config of the xp gained when killing a mob) those files are composed of a list of this type of json block : 

      "minecraft-iron_ingot":{
        "job":"MINER",
        "xp":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25]
      }
This block in the xp_smeltitem.json will tell the game to give xp for the job Miner  : 1xp if the player is level 0, 2xp if the player is level 1, 3xp if the player is level 2, etc.

The syntax for those json config is : 

Jobs must be upper case (HUNTER, MAGICIAN, FARMER, MINER) item names must be hyphen<registry_name>. So you can use non-vanilla items in those config files as long as you use the correct syntax.  the xp array MUST have 25 numbers, they can be 0, but too many or too few numbers will throw an error. The xp_killentity.json do not use item, but entity names as parameter. The list of the available entities is at the end of the description. The xp_harvestcrop.json requires the "seeds" of the crop name, not the crop itself. (e.g. do not use minecraft-wheat, use minecraft-wheat_seeds)  

Other Files :

blockedcraft.json (config of the crafts unlocked with jobs levels) :
this file is composed of this type of json block : 

      "minecraft-iron_pickaxe":{
        "job":"MINER",
        "lvl":1
      }
this block will tell the game that a player cannot craft an Iron Pickaxe before he reach level 1 for the job Miner.

The syntax for this json file is basically the same as the syntax of the xp files, but the "lvl" value MUST be between 1 and 25 included.

 

rewards.json (config of the rewards of each level of each jobs) :
This file is composed of 4 big blocks, one for each job, and those big block is composed of 25 blocks similar to this one : 

      "17":[
         {"item":"minecraft-arrow","count":10},
         {"item":"minecraft-bow","count":1}
      ]
this block tells the game to give 10 Arrow and 1 Bow when a player reach level 17 of the job in question.

You can add as many items as you want in the list, but the "count" cannot exceed 64. (logic)

If this explanation is not clear for you, download the template available at the end of the description and look how the rewards.json is.

 

 

In Singleplayer Worlds :  
    The config files are exactly the same as the one on servers. You can find them in .minecraft/saves//jobs/data/ where is the name of your world.
    The files are exaclty the same as the server files, so take a look above to see how they works.

 

 

The list of available entities for the xp_killentity.json : (Chicken, Cow, Zombie, Sheep, Rabbit, Blaze, Creeper, Enderman, Ghast, Magma Cube, Pig, Skeleton, Slime, Spider, Witch, Wither Skeleton, Zombie Pigman, Player) 
The xp needed to reach the next level : (100, 150, 250, 500, 800, 1150, 1550, 2000, 2500, 3100, 3750, 4500, 5500, 7000, 9000, 11500, 15.000, 19.000, 24.000, 30.000, 39.250, 49.500, 63.100, 80.000, 100.000)  

You can download a template config in the branch PolarFox27-misc.
