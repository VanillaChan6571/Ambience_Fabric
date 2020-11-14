package com.heavenssword.ambience_remixed.events;

// Java
import java.util.Set;

// Minecraft
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

// MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Ambience Remixed
import com.heavenssword.ambience_remixed.SongEvents;
import com.heavenssword.ambience_remixed.playlist.BiomePlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.CustomEventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.EventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.TagPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.BiomeMapper;
import com.heavenssword.ambience_remixed.PlayPriority;

public class AmbienceRemixedPlayerHandler extends AmbienceRemixedEventHandler
{
    @SubscribeEvent
    public void onPlayerTicked( PlayerTickEvent playerTickEvent )
    {
        if( playerTickEvent == null || songDJ == null )
            return;
        
        // We only care about the local user
        PlayerEntity player = playerTickEvent.player;
        if( !player.isUser() )
            return;
        
        World world = player.world;
        BlockPos pos = new BlockPos( player.getPositionVec() );

        // Custom Event PRE
        AmbienceRemixedEvent event = new AmbienceRemixedEvent.PreEventCheck( world, pos );
        MinecraftForge.EVENT_BUS.post( event );
        
        String customEventName = event.getEventName();
        if( customEventName != null && customEventName != "" )
        {
            songDJ.requestPlaylistForCustomEvent( new CustomEventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                         .buildCustomEventPlayRequest( customEventName ) );
            
            return;
        }
        //
        
        // Player is Dying
        if( player.getHealth() < 7.0f )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                             .buildEventPlayRequest( SongEvents.DYING ) );
            
            return;
        }
        //
        
        // Player is Fishing
        if( player.fishingBobber != null )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                             .buildEventPlayRequest( SongEvents.FISHING ) );

            return;
        }
        //
        
        // Player is wearing Pumkin Head
        ItemStack headItem = player.getItemStackFromSlot( EquipmentSlotType.HEAD );
        if( headItem != null && headItem.getItem().equals( Blocks.PUMPKIN.asItem() ) )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                             .buildEventPlayRequest( SongEvents.WEARING_PUMPKIN_HEAD ) );

            return;
        }
        //
        
        // Player is underwater
        if( player.isInWater() )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                             .buildEventPlayRequest( SongEvents.UNDERWATER ) );

            return;
        }
        //
        
        boolean isNighttime = !world.isDaytime();
        if( world.getDimensionKey().equals( World.OVERWORLD ) )
        {
            // Player is underground
            if( !world.canSeeSky( pos ) )
            {   // Deep underground
                if( pos.getY() < 20 )
                {
                    songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                     .buildEventPlayRequest( SongEvents.DEEP_UNDERGROUND ) );

                    return;
                }
                else if( pos.getY() < 55 )
                {
                    songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                     .buildEventPlayRequest( SongEvents.UNDERGROUND ) );

                    return;
                }
            }// It's raining outside.
            else if( world.isRaining() )
            {
                songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .buildEventPlayRequest( SongEvents.RAIN ) );

                return;
            }
            //

            // Player is at a high altitude
            if( pos.getY() > 128 )
            {
                songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .buildEventPlayRequest( SongEvents.HIGH_ALTITUDE ) );

                return;
            }
            //

            // It's nighttime
            if( isNighttime )
            {
                songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .buildEventPlayRequest( SongEvents.NIGHT ) );

                return;
            }
            //
        }
        
        // Player is in/near a Village
        int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                         new AxisAlignedBB( player.getPosX() - 30, player.getPosY() - 8, player.getPosZ() - 30,
                                                                            player.getPosX() + 30, player.getPosY() + 8, player.getPosZ() + 30 ) ).size();
        if( villagerCount > 3 )
        {
            if( isNighttime )
            {
                songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .buildEventPlayRequest( SongEvents.VILLAGE_NIGHT ) );

                return;
            }

            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                             .buildEventPlayRequest( SongEvents.VILLAGE ) );

            return;
        }
        //
        
        // Custom Event POST
        event = new AmbienceRemixedEvent.PostEventCheck( world, pos );
        MinecraftForge.EVENT_BUS.post( event );
        
        customEventName = event.getEventName();
        if( customEventName != null && customEventName != "" )
        {
            songDJ.requestPlaylistForCustomEvent( new CustomEventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                         .buildCustomEventPlayRequest( customEventName ) );
            
            return;
        }
        //
        
        // Player in certain Biome
        Biome biome = world.getBiome( pos );
        if( songDJ.requestPlaylistForBiome( new BiomePlaylistRequestBuilder().playPriority( PlayPriority.MEDIUM )
                                                                             .buildBiomePlayRequest( biome ) ) )
        {
            return;
        }
        //
        
        // Player is in area with certain Biome Tags
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes( BiomeMapper.getBiomeRegistryKey( biome.getRegistryName() ) );
        
        // Primary Tags
        if( songDJ.requestPlaylistForTags( new TagPlaylistRequestBuilder().playPriority( PlayPriority.MID_LOW )
                                                                          .buildTagPlayRequest( types, true ) ) )
        {
            return;
        }
        
        // Secondary Tags
        if( songDJ.requestPlaylistForTags( new TagPlaylistRequestBuilder().playPriority( PlayPriority.LOW )
                                                                          .buildTagPlayRequest( types, false ) ) )
        {
            return;
        }
        //
    }
    
    @SubscribeEvent
    public void onPlayerSleep( PlayerSleepInBedEvent playerSleepEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerSleep() - Begin." );
        
        if( playerSleepEvent == null || songDJ == null )
            return;
        
        // We only care about the local user
        PlayerEntity player = playerSleepEvent.getPlayer();
        if( !player.isUser() )
            return;
        
        // Make sure the player can actually sleep.
        if( player != null && !player.world.isDaytime() )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGHEST )
                                                                             .shouldLoop( false )
                                                                             .buildEventPlayRequest( SongEvents.SLEEPING ) );
        }
    }
    
    @SubscribeEvent
    public void onPlayerWakeUp( PlayerWakeUpEvent playerWakeUpEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.onPlayerWakeUp() - Begin." );
        
        if( playerWakeUpEvent == null || songDJ == null )
            return;
        
        // We only care about the local user
        PlayerEntity player = playerWakeUpEvent.getPlayer();
        if( !player.isUser() )
            return;
        
        songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.LOWEST )
                                                                         .shouldDeferPlay( true )
                                                                         .buildEventPlayRequest( SongEvents.GENERIC ) );
    }
    
    @SubscribeEvent
    public void onPlayerRespawn( PlayerEvent.PlayerRespawnEvent playerRespawnEvent )    
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerRespawn() - Begin." );
        
        if( playerRespawnEvent == null || songDJ == null )
            return;
        
        // We only care about the local user
        PlayerEntity player = playerRespawnEvent.getPlayer();
        if( !player.isUser() )
            return;
        
        songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.LOWEST )
                                                                         .buildEventPlayRequest( SongEvents.GENERIC ) );
    }
}
