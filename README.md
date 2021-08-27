# Jobs
Summary :

 

The Jobs mod is a mod that adds a jobs system to Minecraft. The players will gain xp for one of the four jobs (Hunter, Magician, Farmer and Miner) by executing some actions, like mining ores, harvesting crops, killing mobs, etc. The goal is to reach the max level (level 25) for each job. At each level, the player will receive rewards and unlock blocked crafts !

 

This mod can be used to create a more RP world, or create a progress and quests for an RPG server. The mod can be used in Singleplayer worlds, or in Forge and Mohist Servers.

 

The mod can be fully configured, by choosing in the config files how much xp you gain for each action, which crafts are blocked until which level and what reward is gained at each level.

 

/!\ The configuration of the mod requires a good knowledge on JSON structures and syntax.

 /!\ Because this mod modify the crafting system, it may not be compatible with mods that also modify the crafting system. 

 

 

Configuration :

 

How to find the configuration folder of my world / server ?

In a singleplayer world :
First create your world, then go to your “.minecraft” folder, then go to “.minecraft > saves > world_name > jobs > data”, where world_name is the name of your world.

In a Forge/Mohist server :
First, run your server once, then go to your server folder, then go to “server_folder > jobs > data”.

All those files are created automatically when a new world or server is created, with a config template.

 

Syntax :To refer to a job, use “HUNTER”, “MAGICIAN”, “FARMER”, “MINER”

To refer to an item or a block, use “modid-item_name”,
for example,the reference to Coal Ore is “minecraft-coal_ore”

 

Configuration of XP Files :

“breakblock.json” :
This file configures how much xp you gain when breaking blocks.

The file is composed of those config :

            "minecraft-coal_ore": {

                        "job":"MINER",

                        "xp":[4,5,6,2,3,3,1,2,2,2,2,2,2,4,4,4,5,5,0,0,0,0,0,0,0]

            }

In this case, the player will gain xp when breaking Coal Ore, 4xp at level 0, 5xp at level 1, etc.

 

“craftitem.json” & “smeltitem.json” :
Those files configures how much xp the player gain when crafting and smelting items.

Config Example :

            "minecraft-iron_ingot": {

                        "job":"MINER",

      "xp":[5,6,7,8,8,9,9,10,6,7,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0]

            }

In this case, the player will gain 5 xp at level 0 when collecting iron ingots in a furnace, 6xp at level 1, etc.

 

“harvestcrop.json” :
This file configures how much xp the player gain when harvesting mature crops. Example :

            "minecraft-wheat_seeds": {

                        "job" : "FARMER",

                        "xp": [0,1,2,3,4,5,6,7,8,9,1,1,1,1,1,15,16,17,18,19,20,21,22,23,24]

            }

For this file, you need to use the Seed Item instead of the block, you can check the list in the Useful Informations Section.

 

“killentity.json” :
This file configures how much xp the player gain when killing a mob. Example :

            "Cow": {

                        "job":"HUNTER",

                        "xp" : [0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,17,18,19,20,21,22,23,24]

            }

In this case, the player will not gain xp when killing a cow at level 0, will gain 1xp at level 1, etc. All the mob names that can be used are listed in the Useful Informations Section.

 

Configuration of Blocked Crafts (blockedcraft.json) :

This file configures which crafts are blocked until which level.

Example :

            "minecraft-diamond_pickaxe" : {

                        "job" : "MAGICIAN",

                        "lvl" : 3

            }

In this case, the player will not be able to craft a Diamond Pickaxe before he reach level 3 for the Job Magician.

 

Configuration of Rewards (rewards.json) :

This file configures the rewards that the player will receive each time he passes to the superior level.

Example :

            "HUNTER":{

                              "1":[{"item":"minecraft-coal","count":3}],

                              "2":[{"item":"minecraft-coal","count":1},{"item":"minecraft-diamond","count":1}],

                              "3":[{"item":"minecraft-coal","count":1}],

                              "4":[{"item":"minecraft-coal","count":1}],

                              "5":[{"item":"minecraft-coal","count":1}],

                              "6":[{"item":"minecraft-coal","count":1}],

                              "7":[{"item":"minecraft-coal","count":1}],

                              "8":[{"item":"minecraft-coal","count":1}],

                              "9":[{"item":"minecraft-coal","count":1}],

                              "10":[{"item":"minecraft-coal","count":1}],

                              "11":[{"item":"minecraft-coal","count":1}],

                              "12":[{"item":"minecraft-coal","count":1}],

                              "13":[{"item":"minecraft-coal","count":1}],

                              "14":[{"item":"minecraft-coal","count":1}],

                              "15":[{"item":"minecraft-coal","count":1}],

                              "16":[{"item":"minecraft-coal","count":1}],

                              "17":[{"item":"minecraft-coal","count":1}],

                              "18":[{"item":"minecraft-coal","count":1}],

                              "19":[{"item":"minecraft-coal","count":1}],

                              "20":[{"item":"minecraft-coal","count":1}],

                              "21":[{"item":"minecraft-coal","count":1}],

                              "22":[{"item":"minecraft-coal","count":1}],

                              "23":[{"item":"minecraft-coal","count":1}],

                              "24":[{"item":"minecraft-coal","count":1}],

                              "25":[{"item":"minecraft-coal","count":1}]

               }

This file is composed of 4 “block” like this, one for each job. In this case, when the player reaches level 1 for the job Hunter, he will receive 3 Coal, when he reaches level 2 of the job Hunter, he will receive 1 Coal and 1 Diamond, etc.

 

Mod Config File since version 1.3 (1.12.2 only) : 

This file can be found at the same place than the others json files (/jobs/config.json)

Config : 

isHardMode : (true/false) this determine if you need a Jobs Book to open the Jobs Menu.
If it is set to true, the player will not be able to open the Jobs Menu by pressing the J key

The player will need a Jobs Book to open it.

 

Jobs Book : New item added in versions 1.3+ (1.12.2)

Craft : 1 Book + 4 Lapis + 4 Redstone Dust => 1 Jobs Book
Use : when you right click this book, the Jobs Menu is opened
 

In-Game Use :

 

To access the job menu in-game, you can press the key J. (you can change it in the controls options) In this menu, you can see how you can gain xp for each job, and which crafts are blocked for your current level.

 

There is also 3 admin commands, useful on servers :

jobs-info <Player> : shows the jobs levels of a player.
jobs-set <Player> <Job> <level> <xp> : sets the level and the xp of the specified job for a player.
Jobs-add <Player> <Job> <xp> : adds xp of the specified job to a player.
Those commands need a permission level 2 (op players) to be executed.

 

Useful Informations : 

 

XP needed per level : (100, 150, 250, 500, 800, 1150, 1550, 2000, 2500, 3100, 3750, 4500, 5500, 7000, 9000, 11500, 15.000, 19.000, 24.000, 30.000, 39.250, 49.500, 63.100,       80.000, 100.000)
 

List of Vanilla Crops (harvestcrop.json)  :
Wheat : "minecraft-wheat_seeds" (1.16.5 : "minecraft-wheat")

Carrots : "minecraft-carrot"

Potatoes : "minecraft-potato"

Beetroots : "minecraft-beetroot_seeds" (1.16.5 : "minecraft-beetroot")

 

Mobs that can be killed to gain XP :
Chicken, Cow, Sheep, Pig, Horse, Donkey, Rabbit, Slime, Player
Zombie, Skeleton, Creeper, Enderman, Spider, Cave Spider, Blaze, Ghast, Magma Cube, Witch, Wither Skeleton, Zombie Pigman, Guardian, Elder Guardian, Wither, Ender Dragon
from 1.16.5 (version 2.0) : Piglin, Hoglin, Zoglin, Drawned, Dolphin
  

You can download a template config in the branch PolarFox27-misc.
The mod can be downloaded here : https://www.curseforge.com/minecraft/mc-mods/jobs
