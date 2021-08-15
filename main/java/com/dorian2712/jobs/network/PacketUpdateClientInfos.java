package com.dorian2712.jobs.network;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Job;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacketUpdateClientInfos implements IMessage {

    public Map<Block, long[]> BREAK_BLOCK_XP = new HashMap<>();
    public Map<Block, Job> BREAK_BLOCK_JOB = new HashMap<>();

    public Map<Item, long[]> HARVEST_CROP_XP = new HashMap<>();
    public Map<Item, Job> HARVEST_CROP_JOB = new HashMap<>();

    public Map<Item, long[]> CRAFT_ITEM_XP = new HashMap<>();
    public Map<Item, Job> CRAFT_ITEM_JOB = new HashMap<>();

    public Map<Item, long[]> SMELT_ITEM_XP = new HashMap<>();
    public Map<Item, Job> SMELT_ITEM_JOB = new HashMap<>();

    public Map<String, long[]> KILL_ENTITY_XP = new HashMap<>();
    public Map<String, Job> KILL_ENTITY_JOB = new HashMap<>();


    public Map<Item, Integer> CRAFT_UNLOCK_LVL = new HashMap<>();
    public Map<Item, Job> CRAFT_UNLOCK_JOB = new HashMap<>();


    public PacketUpdateClientInfos(){}
    public PacketUpdateClientInfos(Map<Block, long[]> m1, Map<Block, Job> m2, Map<Item, long[]> m3, Map<Item, Job> m4,
                                   Map<Item, long[]> m5, Map<Item, Job> m6,
                                   Map<Item, long[]> m7, Map<Item, Job> m8,
                                   Map<String, long[]> m9, Map<String, Job> m10,
                                   Map<Item, Integer> m11, Map<Item, Job> m12)
    {
        this.BREAK_BLOCK_XP = m1;
        this.BREAK_BLOCK_JOB = m2;
        this.HARVEST_CROP_XP = m3;
        this.HARVEST_CROP_JOB = m4;
        this.CRAFT_ITEM_XP = m5;
        this.CRAFT_ITEM_JOB = m6;
        this.SMELT_ITEM_XP = m7;
        this.SMELT_ITEM_JOB = m8;
        this.KILL_ENTITY_XP = m9;
        this.KILL_ENTITY_JOB = m10;
        this.CRAFT_UNLOCK_LVL = m11;
        this.CRAFT_UNLOCK_JOB = m12;
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        int size1 = buf.readInt();
        int size2 = buf.readInt();
        int size3 = buf.readInt();
        int size4 = buf.readInt();
        int size5 = buf.readInt();
        int size6 = buf.readInt();

        for(int i = 0; i < size1; i++) //BREAK BLOCK
        {
            Block b = Block.getBlockById(buf.readInt());
            Job j = Job.byIndex(buf.readInt());
            long[] xps = new long[25];
            for(int x = 0; x < 25; x++)
                xps[x] = buf.readLong();
            this.BREAK_BLOCK_JOB.put(b, j);
            this.BREAK_BLOCK_XP.put(b, xps);
        }

        for(int i = 0; i < size2; i++) //HARVEST CROP
        {
            Item b = Item.getItemById(buf.readInt());
            Job j = Job.byIndex(buf.readInt());
            long[] xps = new long[25];
            for(int x = 0; x < 25; x++)
                xps[x] = buf.readLong();
            this.HARVEST_CROP_JOB.put(b, j);
            this.HARVEST_CROP_XP.put(b, xps);
        }

        for(int i = 0; i < size3; i++) //CRAFT ITEM
        {
            Item item = Item.getItemById(buf.readInt());
            Job j = Job.byIndex(buf.readInt());
            long[] xps = new long[25];
            for(int x = 0; x < 25; x++)
                xps[x] = buf.readLong();
            this.CRAFT_ITEM_JOB.put(item, j);
            this.CRAFT_ITEM_XP.put(item, xps);
        }

        for(int i = 0; i < size4; i++) //SMELT ITEM
        {
            Item item = Item.getItemById(buf.readInt());
            Job j = Job.byIndex(buf.readInt());
            long[] xps = new long[25];
            for(int x = 0; x < 25; x++)
                xps[x] = buf.readLong();
            this.SMELT_ITEM_JOB.put(item, j);
            this.SMELT_ITEM_XP.put(item, xps);
        }

        for(int i = 0; i < size5; i++)//KILL ENTITY
        {
            String s = Constants.getIDEntities().get(buf.readInt());
            Job job = Job.byIndex(buf.readInt());
            long[] xps = new long[25];
            for(int j = 0; j < 25; j++)
                xps[j] = buf.readLong();
            this.KILL_ENTITY_JOB.put(s, job);
            this.KILL_ENTITY_XP.put(s, xps);
        }

        for(int i = 0; i < size6; i++) //UNLOCK CRAFT
        {
            Item item = Item.getItemById(buf.readInt());
            Job j = Job.byIndex(buf.readInt());
            int lvl = buf.readInt();
            this.CRAFT_UNLOCK_JOB.put(item, j);
            this.CRAFT_UNLOCK_LVL.put(item, lvl);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.BREAK_BLOCK_JOB.size());
        buf.writeInt(this.HARVEST_CROP_JOB.size());
        buf.writeInt(this.CRAFT_ITEM_JOB.size());
        buf.writeInt(this.SMELT_ITEM_JOB.size());
        buf.writeInt(this.KILL_ENTITY_JOB.size());
        buf.writeInt(this.CRAFT_UNLOCK_JOB.size());

        for(Block b : this.BREAK_BLOCK_JOB.keySet())   //BREAK BLOCK
        {
            buf.writeInt(Block.getIdFromBlock(b));
            buf.writeInt(this.BREAK_BLOCK_JOB.get(b).index);
            for(int i = 0 ; i < 25; i++)
                buf.writeLong(this.BREAK_BLOCK_XP.get(b)[i]);
        }

        for(Item b : this.HARVEST_CROP_JOB.keySet())   //HARVEST CROP
        {
            buf.writeInt(Item.getIdFromItem(b));
            buf.writeInt(this.HARVEST_CROP_JOB.get(b).index);
            for(int i = 0 ; i < 25; i++)
                buf.writeLong(this.HARVEST_CROP_XP.get(b)[i]);
        }

        for(Item item : this.CRAFT_ITEM_JOB.keySet())   //CRAFT ITEM
        {
            buf.writeInt(Item.getIdFromItem(item));
            buf.writeInt(this.CRAFT_ITEM_JOB.get(item).index);
            for(int i = 0 ; i < 25; i++)
                buf.writeLong(this.CRAFT_ITEM_XP.get(item)[i]);
        }

        for(Item item : this.SMELT_ITEM_JOB.keySet())   //SMELT ITEM
        {
            buf.writeInt(Item.getIdFromItem(item));
            buf.writeInt(this.SMELT_ITEM_JOB.get(item).index);
            for(int i = 0 ; i < 25; i++)
                buf.writeLong(this.SMELT_ITEM_XP.get(item)[i]);
        }

        for(String s : this.KILL_ENTITY_JOB.keySet())//KILL ENTITY
        {
            buf.writeInt(Constants.getEntitiesID().get(s));
            buf.writeInt(this.KILL_ENTITY_JOB.get(s).index);
            for(int i = 0; i < 25; i++)
                buf.writeLong(this.KILL_ENTITY_XP.get(s)[i]);
        }

        for(Item item : this.CRAFT_UNLOCK_JOB.keySet())   //UNLOCK CRAFT
        {
            buf.writeInt(Item.getIdFromItem(item));
            buf.writeInt(this.CRAFT_UNLOCK_JOB.get(item).index);
            buf.writeInt(this.CRAFT_UNLOCK_LVL.get(item));
        }

    }




    public static class MessageHandler implements IMessageHandler<PacketUpdateClientInfos, IMessage>
    {

        @Override
        public IMessage onMessage(PacketUpdateClientInfos message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT)
            {
                ClientInfos.BREAK_BLOCK_XP = message.BREAK_BLOCK_XP;
                ClientInfos.BREAK_BLOCK_JOB = message.BREAK_BLOCK_JOB;
                ClientInfos.HARVEST_CROP_XP = message.HARVEST_CROP_XP;
                ClientInfos.HARVEST_CROP_JOB = message.HARVEST_CROP_JOB;
                ClientInfos.CRAFT_ITEM_XP = message.CRAFT_ITEM_XP;
                ClientInfos.CRAFT_ITEM_JOB = message.CRAFT_ITEM_JOB;
                ClientInfos.SMELT_ITEM_XP = message.SMELT_ITEM_XP;
                ClientInfos.SMELT_ITEM_JOB = message.SMELT_ITEM_JOB;
                ClientInfos.KILL_ENTITY_XP = message.KILL_ENTITY_XP;
                ClientInfos.KILL_ENTITY_JOB = message.KILL_ENTITY_JOB;

                ClientInfos.CRAFT_UNLOCK_LVL = message.CRAFT_UNLOCK_LVL;
                ClientInfos.CRAFT_UNLOCK_JOB = message.CRAFT_UNLOCK_JOB;
            }
            return null;
        }
    }
}
