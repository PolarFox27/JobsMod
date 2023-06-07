#!/bin/bash

./gradlew build
rm /home/dorian/.var/app/com.mojang.Minecraft/.minecraft/mods/*.jar
rm /home/dorian/.var/app/com.mojang.Minecraft/.minecraft/mods/Jobs/*.jar
cp /home/dorian/Desktop/Programmation/Mods/1.12.2/build/libs/Jobs-1.12.2-3.0.2.jar /home/dorian/.var/app/com.mojang.Minecraft/.minecraft/mods/Jobs/
cp /home/dorian/Desktop/Programmation/Mods/1.12.2/build/libs/Jobs-1.12.2-3.0.2.jar /home/dorian/.var/app/com.mojang.Minecraft/.minecraft/mods/ 
flatpak run com.mojang.Minecraft

