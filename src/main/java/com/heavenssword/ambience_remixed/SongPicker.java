package com.heavenssword.ambience_remixed;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraft.client.gui.screen.WinGameScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class SongPicker
{
    public static final String EVENT_MAIN_MENU = "mainMenu";
    public static final String EVENT_BOSS = "boss";
    public static final String EVENT_BOSS_WITHER = "bossWither";
    public static final String EVENT_BOSS_DRAGON = "bossDragon";
    public static final String EVENT_IN_NETHER = "nether";
    public static final String EVENT_IN_END = "end";
    public static final String EVENT_HORDE = "horde";
    public static final String EVENT_NIGHT = "night";
    public static final String EVENT_RAIN = "rain";
    public static final String EVENT_UNDERWATER = "underwater";
    public static final String EVENT_UNDERGROUND = "underground";
    public static final String EVENT_DEEP_UNDEGROUND = "deepUnderground";
    public static final String EVENT_HIGH_UP = "highUp";
    public static final String EVENT_VILLAGE = "village";
    public static final String EVENT_VILLAGE_NIGHT = "villageNight";
    public static final String EVENT_MINECART = "minecart";
    public static final String EVENT_BOAT = "boat";
    public static final String EVENT_HORSE = "horse";
    public static final String EVENT_PIG = "pig";
    public static final String EVENT_FISHING = "fishing";
    public static final String EVENT_DYING = "dying";
    public static final String EVENT_PUMPKIN_HEAD = "pumpkinHead";
    public static final String EVENT_CREDITS = "credits";
    public static final String EVENT_GENERIC = "generic";

    public static final Map<String, String[]> eventMap = new HashMap<String, String[]>();
    public static final Map<Biome, String[]> biomeMap = new HashMap<Biome, String[]>();
    public static final Map<BiomeDictionary.Type, String[]> primaryTagMap = new HashMap<BiomeDictionary.Type, String[]>();
    public static final Map<BiomeDictionary.Type, String[]> secondaryTagMap = new HashMap<BiomeDictionary.Type, String[]>();

    public static final Random rand = new Random();

    public static void reset()
    {
        eventMap.clear();
        biomeMap.clear();
        primaryTagMap.clear();
        secondaryTagMap.clear();
    }

    public static String[] getSongs()
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        World world = mc.world;

        if( player == null || world == null )
            return getSongsForEvent( EVENT_MAIN_MENU );

        if( mc.currentScreen instanceof WinGameScreen )
            return getSongsForEvent( EVENT_CREDITS );

        BlockPos pos = new BlockPos( player.getPositionVec() );

        AmbienceRemixedEvent event = new AmbienceRemixedEvent.PreEventCheck( world, pos );
        MinecraftForge.EVENT_BUS.post( event );
        String[] customEventSongList = getSongsForEvent( event.getEventName() );
        if( customEventSongList != null )
            return customEventSongList;

        BossOverlayGui bossOverlay = mc.ingameGUI.getBossOverlay();
        Map<UUID, BossInfo> map = ObfuscationReflectionHelper.getPrivateValue( BossOverlayGui.class, bossOverlay, AmbienceRemixed.OBF_MAP_BOSS_INFOS[1] );
        if( !map.isEmpty() )
        {
            try
            {
                BossInfo first = map.get( map.keySet().iterator().next() );
                ITextComponent comp = first.getName();
                String rawBossName = comp.getString();

                /*
                 * if( comp instanceof StringTextComponent ) { type =
                 * comp.getStyle().getHoverEvent()..getValue().getUnformattedComponentText();
                 * type = type.substring( type.indexOf( "type:\"" ) + 6, type.length() - 2 ); }
                 * else if( comp instanceof TranslationTextComponent ) { type =
                 * ((TranslationTextComponent)comp).getKey(); if( type.startsWith( "entity." )
                 * && type.endsWith( ".name" ) ) type = type.substring( 7, type.length() - 5 );
                 * }
                 */

                // if( type.equals( "minecraft:wither" ) )
                if( rawBossName.equals( "Wither" ) )
                {
                    String[] songs = getSongsForEvent( EVENT_BOSS_WITHER );
                    if( songs != null )
                        return songs;
                }
                // else if( type.equals( "EnderDragon" ) )
                else if( rawBossName.equals( "EnderDragon" ) )
                {
                    String[] songs = getSongsForEvent( EVENT_BOSS_DRAGON );
                    if( songs != null )
                        return songs;
                }
            }
            catch( NullPointerException e )
            {
            }

            String[] songs = getSongsForEvent( EVENT_BOSS );
            if( songs != null )
                return songs;
        }

        float hp = player.getHealth();
        if( hp < 7 )
        {
            String[] songs = getSongsForEvent( EVENT_DYING );
            if( songs != null )
                return songs;
        }

        int monsterCount = world.getEntitiesWithinAABB( MonsterEntity.class,
                                                        new AxisAlignedBB( player.getPosX() - 16, player.getPosY() - 8, player.getPosZ() - 16,
                                                                           player.getPosX() + 16, player.getPosY() + 8, player.getPosZ() + 16 ) ).size();
        if( monsterCount > 5 )
        {
            String[] songs = getSongsForEvent( EVENT_HORDE );
            if( songs != null )
                return songs;
        }

        if( player.fishingBobber != null )
        {
            String[] songs = getSongsForEvent( EVENT_FISHING );
            if( songs != null )
                return songs;
        }

        ItemStack headItem = player.getItemStackFromSlot( EquipmentSlotType.HEAD );
        if( headItem != null && headItem.getItem() == Blocks.PUMPKIN.asItem() )
        {
            String[] songs = getSongsForEvent( EVENT_PUMPKIN_HEAD );
            if( songs != null )
                return songs;
        }

        RegistryKey<World> dimensionKey = world.getDimensionKey();

        if( dimensionKey.equals( World.THE_NETHER ) )
        {
            String[] songs = getSongsForEvent( EVENT_IN_NETHER );
            if( songs != null )
                return songs;
        }
        else if( dimensionKey.equals( World.THE_END ) )
        {
            String[] songs = getSongsForEvent( EVENT_IN_END );
            if( songs != null )
                return songs;
        }

        Entity riding = player.getRidingEntity();
        if( riding != null )
        {
            if( riding instanceof AbstractMinecartEntity )
            {
                String[] songs = getSongsForEvent( EVENT_MINECART );
                if( songs != null )
                    return songs;
            }
            else if( riding instanceof BoatEntity )
            {
                String[] songs = getSongsForEvent( EVENT_BOAT );
                if( songs != null )
                    return songs;
            }
            else if( riding instanceof AbstractHorseEntity )
            {
                String[] songs = getSongsForEvent( EVENT_HORSE );
                if( songs != null )
                    return songs;
            }
            else if( riding instanceof PigEntity )
            {
                String[] songs = getSongsForEvent( EVENT_PIG );
                if( songs != null )
                    return songs;
            }
        }

        if( player.isInWater() )
        {
            String[] songs = getSongsForEvent( EVENT_UNDERWATER );
            if( songs != null )
                return songs;
        }

        long time = world.getGameTime() % 24000;
        boolean night = time > 13300 && time < 23200;

        if( world.getDimensionKey().equals( World.OVERWORLD ) )
        {
            boolean underground = !world.canSeeSky( pos );

            if( underground )
            {
                if( pos.getY() < 20 )
                {
                    String[] songs = getSongsForEvent( EVENT_DEEP_UNDEGROUND );
                    if( songs != null )
                        return songs;
                }
                if( pos.getY() < 55 )
                {
                    String[] songs = getSongsForEvent( EVENT_UNDERGROUND );
                    if( songs != null )
                        return songs;
                }
            }
            else if( world.isRaining() )
            {
                String[] songs = getSongsForEvent( EVENT_RAIN );
                if( songs != null )
                    return songs;
            }

            if( pos.getY() > 128 )
            {
                String[] songs = getSongsForEvent( EVENT_HIGH_UP );
                if( songs != null )
                    return songs;
            }

            if( night )
            {
                String[] songs = getSongsForEvent( EVENT_NIGHT );
                if( songs != null )
                    return songs;
            }
        }

        int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                         new AxisAlignedBB( player.getPosX() - 30, player.getPosY() - 8, player.getPosZ() - 30,
                                                                            player.getPosX() + 30, player.getPosY() + 8, player.getPosZ() + 30 ) ).size();
        if( villagerCount > 3 )
        {
            if( night )
            {
                String[] songs = getSongsForEvent( EVENT_VILLAGE_NIGHT );
                
                if( songs != null )
                    return songs;
            }

            String[] songs = getSongsForEvent( EVENT_VILLAGE );
            
            if( songs != null )
                return songs;
        }

        event = new AmbienceRemixedEvent.PostEventCheck( world, pos );
        MinecraftForge.EVENT_BUS.post( event );
        customEventSongList = getSongsForEvent( event.getEventName() );
        
        if( customEventSongList != null )
            return customEventSongList;

        if( world != null )
        {
            // Chunk chunk = world.getChunkFromBlockCoords(pos);
            // Biome biome = chunk.getBiome(pos, world.getBiomeProvider());
            Biome biome = world.getBiomeManager().getBiomeAtPosition( pos );
            world.getBiome( pos );
            if( biomeMap.containsKey( biome ) )
                return biomeMap.get( biome );

            /*Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes( biome );
            for( Type t : types )
                if( primaryTagMap.containsKey( t ) )
                    return primaryTagMap.get( t );
            for( Type t : types )
                if( secondaryTagMap.containsKey( t ) )
                    return secondaryTagMap.get( t );*/
        }

        return getSongsForEvent( EVENT_GENERIC );
    }

    public static String getSongsString()
    {
        return StringUtils.join( getSongs(), "," );
    }

    public static String getRandomSong()
    {
        String[] songChoices = getSongs();

        return songChoices[rand.nextInt( songChoices.length )];
    }

    public static String[] getSongsForEvent( String eventKey )
    {
        if( eventMap.containsKey( eventKey ) )
            return eventMap.get( eventKey );

        return null;
    }

    public static String getSongName( String song )
    {
        return song == null ? "" : song.replaceAll( "([^A-Z])([A-Z])", "$1 $2" );
    }
}
