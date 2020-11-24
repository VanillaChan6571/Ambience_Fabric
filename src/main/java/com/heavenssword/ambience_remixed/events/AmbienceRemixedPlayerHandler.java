package com.heavenssword.ambience_remixed.events;

// Java
import java.util.Set;

// Minecraft
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
//import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Ambience Remixed
import com.heavenssword.ambience_remixed.SongEvents;
import com.heavenssword.ambience_remixed.playlist.BiomePlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.CustomEventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.EventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.IPlaylistStillValidCallback;
import com.heavenssword.ambience_remixed.playlist.TagPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.BiomeMapper;
import com.heavenssword.ambience_remixed.PlayPriority;

public class AmbienceRemixedPlayerHandler extends AmbienceRemixedEventHandler
{
    // Private Fields
    private DyingStillValid dyingStillValidCallback = new DyingStillValid();
    private FishingStillValid fishingStillValidCallback = new FishingStillValid();
    private PumpkinHeadStillValid pumpkinHeadStillValidCallback = new PumpkinHeadStillValid();
    private UnderwaterStillValid underwaterStillValidCallback = new UnderwaterStillValid();
    private UndergroundStillValid undergroundStillValidCallback = new UndergroundStillValid();
    private DeepUndergroundStillValid deepUndergroundStillValidCallback = new DeepUndergroundStillValid();
    private RainingStillValid rainingStillValidCallback = new RainingStillValid();
    private HighAltitudeStillValid highAlitudeStillValidCallback = new HighAltitudeStillValid();
    private NightimeStillValid nightimeStillValidCallback = new NightimeStillValid();
    private VillageStillValid villageStillValidCallback = new VillageStillValid();
    private VillageAtNightStillValid villageAtNightStillValidCallback = new VillageAtNightStillValid();
    private BiomeStillValid biomeStillValidCallback = new BiomeStillValid();
    
    private Biome lastBiomeToPlay = null;
    
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
            if( songDJ.requestPlaylistForCustomEvent( new CustomEventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                             .buildCustomEventPlayRequest( customEventName ) ) )
            {
                return;
            }
        }
        //
        
        // Player is Dying
        if( player.getHealth() < 7.0f )
        {
            if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .canBeOverriden( true )
                                                                                 .playlistStillValidCallback( dyingStillValidCallback )
                                                                                 .fadeTime( 0.25 )
                                                                                 .buildEventPlayRequest( SongEvents.DYING ) ) )
            {
                return;
            }
        }
        //
        
        // Player is Fishing
        if( player.fishingBobber != null )
        {
            if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .canBeOverriden( true )
                                                                                 .playlistStillValidCallback( fishingStillValidCallback )
                                                                                 .buildEventPlayRequest( SongEvents.FISHING ) ) )
            {
                return;
            }
        }
        //
        
        // Player is wearing Pumkin Head
        ItemStack headItem = player.getItemStackFromSlot( EquipmentSlotType.HEAD );
        if( headItem != null && headItem.getItem().equals( Blocks.PUMPKIN.asItem() ) )
        {
            if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .canBeOverriden( true )
                                                                                 .playlistStillValidCallback( pumpkinHeadStillValidCallback )
                                                                                 .buildEventPlayRequest( SongEvents.WEARING_PUMPKIN_HEAD ) ) )
            {
                return;
            }
        }
        //
        
        // Player is underwater
        if( player.areEyesInFluid( FluidTags.WATER ) )
        {
            if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .canBeOverriden( true )
                                                                                 .playlistStillValidCallback( underwaterStillValidCallback )
                                                                                 .buildEventPlayRequest( SongEvents.UNDERWATER ) ) )
            {
                return;
            }
        }
        //
        
        long time = world.getDayTime() % 24000;
        boolean isNightime = time > 13300 && time < 23200;
        
        // Player is in/near a Village
        int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                         new AxisAlignedBB( player.getPosX() - AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() - AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() - AmbienceRemixed.VILLAGE_RADIUS_Z,
                                                                            player.getPosX() + AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() + AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() + AmbienceRemixed.VILLAGE_RADIUS_Z ) ).size();
        if( villagerCount > AmbienceRemixed.VILLAGE_POPULATION_REQUIREMENT )
        {
            if( isNightime )
            {
                if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.MID_HIGH )
                                                                                     .canBeOverriden( true )
                                                                                     .playlistStillValidCallback( villageAtNightStillValidCallback )
                                                                                     .buildEventPlayRequest( SongEvents.VILLAGE_NIGHT ) ) )
                {
                    return;
                }
            }

            if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                 .canBeOverriden( true )
                                                                                 .playlistStillValidCallback( villageStillValidCallback )
                                                                                 .buildEventPlayRequest( SongEvents.VILLAGE ) ) )
            {
                return;
            }
        }
        //
        
        if( world.getDimensionKey().equals( World.OVERWORLD ) )
        {
            // Player is underground
            if( !world.canSeeSky( pos ) )
            {   // Deep underground
                if( pos.getY() < AmbienceRemixed.DEEP_UNDERGROUND_HEIGHT )
                {
                    if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                         .canBeOverriden( true )
                                                                                         .playlistStillValidCallback( undergroundStillValidCallback )
                                                                                         .buildEventPlayRequest( SongEvents.DEEP_UNDERGROUND ) ) )
                    {
                        return;
                    }
                }
                else if( pos.getY() < AmbienceRemixed.UNDERGROUND_HEIGHT )
                {
                    if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                         .canBeOverriden( true )
                                                                                         .playlistStillValidCallback( deepUndergroundStillValidCallback )
                                                                                         .buildEventPlayRequest( SongEvents.UNDERGROUND ) ) )
                    {
                        return;
                    }
                }
                else
                {
                    if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                         .canBeOverriden( true )
                                                                                         .playlistStillValidCallback( deepUndergroundStillValidCallback )
                                                                                         .buildEventPlayRequest( SongEvents.CAVE ) ) )
                    {
                        return;
                    }
                }
            }// It's raining outside.
            else if( world.isRaining() )
            {
                if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                     .canBeOverriden( true )
                                                                                     .playlistStillValidCallback( rainingStillValidCallback )
                                                                                     .buildEventPlayRequest( SongEvents.RAIN ) ) )
                {
                    return;
                }
            }
            //

            // Player is at a high altitude
            if( pos.getY() > AmbienceRemixed.HIGH_ALITUDE_HEIGHT )
            {
                if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                     .canBeOverriden( true )
                                                                                     .playlistStillValidCallback( highAlitudeStillValidCallback )
                                                                                     .buildEventPlayRequest( SongEvents.HIGH_ALTITUDE ) ) )
                {
                    return;
                }
            }
            //

            // It's nightime
            if( isNightime )
            {
                if( songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                     .canBeOverriden( true )
                                                                                     .playlistStillValidCallback( nightimeStillValidCallback )
                                                                                     .buildEventPlayRequest( SongEvents.NIGHT ) ) )
                {
                    return;
                }
            }
            //
        }
        
        // Custom Event POST
        event = new AmbienceRemixedEvent.PostEventCheck( world, pos );
        MinecraftForge.EVENT_BUS.post( event );
        
        customEventName = event.getEventName();
        if( customEventName != null && customEventName != "" )
        {
            if( songDJ.requestPlaylistForCustomEvent( new CustomEventPlaylistRequestBuilder().playPriority( PlayPriority.HIGH )
                                                                                             .buildCustomEventPlayRequest( customEventName ) ) )
            {
                return;
            }
        }
        //
        
        // Player in certain Biome
        Biome biome = world.getBiome( pos );
        if( songDJ.requestPlaylistForBiome( new BiomePlaylistRequestBuilder().playPriority( PlayPriority.MEDIUM )
                                                                             .canBeOverriden( true )
                                                                             .playlistStillValidCallback( biomeStillValidCallback )
                                                                             .buildBiomePlayRequest( biome.getRegistryName() ) ) )
        {
            lastBiomeToPlay = biome;
            
            return;
        }
        //
        
        // Player is in area with certain Biome Tags
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes( BiomeMapper.getBiomeRegistryKey( biome.getRegistryName() ) );
        
        // Primary Tags
        if( songDJ.requestPlaylistForTags( new TagPlaylistRequestBuilder().playPriority( PlayPriority.MID_LOW )
                                                                          .canBeOverriden( true )
                                                                          .playlistStillValidCallback( biomeStillValidCallback )
                                                                          .buildTagPlayRequest( types, true ) ) )
        {
            lastBiomeToPlay = biome;
            
            return;
        }
        
        // Secondary Tags
        if( songDJ.requestPlaylistForTags( new TagPlaylistRequestBuilder().playPriority( PlayPriority.LOW )
                                                                          .canBeOverriden( true )
                                                                          .playlistStillValidCallback( biomeStillValidCallback )
                                                                          .buildTagPlayRequest( types, false ) ) )
        {
            lastBiomeToPlay = biome;
            
            return;
        }
        //
        
        // Fallback playlist if nothing else
        songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.LOWEST )
                                        .shouldDeferPlay( true )
                                        .buildEventPlayRequest( SongEvents.GENERIC ) );
        //
    }
    
    @SubscribeEvent
    public void onPlayerSleep( PlayerSleepInBedEvent playerSleepEvent )
    {
        //AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerSleep() - Begin." );
        
        if( playerSleepEvent == null || songDJ == null )
            return;

        // We only care about the local user
        PlayerEntity player = playerSleepEvent.getPlayer();
        //AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerSleep() - Player is User = " + player.isUser() ? "TRUE" : "FALSE" );
        //if( !player.isUser() )// This check is failing for some reason. This is always a ServerPlayer instance?
        //    return;
                
        // Make sure the player can actually sleep.
        if( player != null && !player.world.isDaytime() )
        {
            //AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerSleep() - Appropriate sleep time. RequestingPlaylist." );
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.HIGHEST )
                                                                             .shouldLoop( false )
                                                                             .fadeTime( 0.0 )
                                                                             .buildEventPlayRequest( SongEvents.SLEEPING ) );
        }
    }
    
    /*@SubscribeEvent
    public void onPlayerWakeUp( PlayerWakeUpEvent playerWakeUpEvent )
    {
        //AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.onPlayerWakeUp() - Begin." );
        
        if( playerWakeUpEvent == null || songDJ == null )
            return;
        
        // We only care about the local user
        //PlayerEntity player = playerWakeUpEvent.getPlayer();
        //if( !player.isUser() )
        //    return;
        
        songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.LOWEST )
                                                                         .shouldDeferPlay( true )
                                                                         .buildEventPlayRequest( SongEvents.GENERIC ) );
    }*/
    
    /*@SubscribeEvent
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
    }*/
    
    // Callback Classes
    public final class DyingStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            
            return ( player != null ? ( player.getHealth() < 7.0f ) : false );
        }        
    }
    
    public final class FishingStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            
            return ( player != null ? ( player.fishingBobber != null ) : false );
        }        
    }
    
    public final class PumpkinHeadStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            ItemStack headItem = player.getItemStackFromSlot( EquipmentSlotType.HEAD );
            
            return ( headItem != null ? headItem.getItem().equals( Blocks.PUMPKIN.asItem() ) : false );
        }        
    }
    
    public final class UnderwaterStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            
            return ( player != null ? player.areEyesInFluid( FluidTags.WATER ) : false );
        }        
    }
    
    public final class UndergroundStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            BlockPos pos = new BlockPos( player.getPositionVec() );
            
            boolean inOverworld = world.getDimensionKey().equals( World.OVERWORLD );
            boolean cantSeeSky = !world.canSeeSky( pos );
            boolean isUndergroundHeight = pos.getY() < AmbienceRemixed.UNDERGROUND_HEIGHT;
            
            return ( inOverworld && cantSeeSky && isUndergroundHeight );
        }        
    }
    
    public final class DeepUndergroundStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            BlockPos pos = new BlockPos( player.getPositionVec() );
            
            boolean inOverworld = world.getDimensionKey().equals( World.OVERWORLD );
            boolean cantSeeSky = !world.canSeeSky( pos );
            boolean isUndergroundHeight = pos.getY() < AmbienceRemixed.DEEP_UNDERGROUND_HEIGHT;
            
            return ( inOverworld && cantSeeSky && isUndergroundHeight );
        }        
    }
    
    public final class RainingStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            
            boolean inOverworld = world.getDimensionKey().equals( World.OVERWORLD );
            
            return ( inOverworld && world.isRaining() );
        }        
    }
    
    public final class HighAltitudeStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            BlockPos pos = new BlockPos( player.getPositionVec() );
            
            boolean inOverworld = world.getDimensionKey().equals( World.OVERWORLD );
            boolean isHighAltitudeHeight = pos.getY() > AmbienceRemixed.HIGH_ALITUDE_HEIGHT;
            
            return ( inOverworld && isHighAltitudeHeight );
        }        
    }
    
    public final class NightimeStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            
            long time = world.getDayTime() % 24000;
            boolean isNightime = time > 13300 && time < 23200;
            
            boolean inOverworld = world.getDimensionKey().equals( World.OVERWORLD );
            
            // Is the player in a village? Because VILLAGE_NIGHT should override this event then.
            int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                             new AxisAlignedBB( player.getPosX() - AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() - AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() - AmbienceRemixed.VILLAGE_RADIUS_Z,
                                                                                player.getPosX() + AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() + AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() + AmbienceRemixed.VILLAGE_RADIUS_Z ) ).size();
            boolean isVillageNightValid = ( villagerCount > AmbienceRemixed.VILLAGE_POPULATION_REQUIREMENT ) && songDJ.canRequestEventPlaylist( SongEvents.VILLAGE_NIGHT );
            
            return ( inOverworld && isNightime ) && !isVillageNightValid;
        }
    }
    
    public final class VillageStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
                        
            int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                             new AxisAlignedBB( player.getPosX() - AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() - AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() - AmbienceRemixed.VILLAGE_RADIUS_Z,
                                                                                player.getPosX() + AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() + AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() + AmbienceRemixed.VILLAGE_RADIUS_Z ) ).size();
            
            return ( villagerCount > AmbienceRemixed.VILLAGE_POPULATION_REQUIREMENT );
        }        
    }
    
    public final class VillageAtNightStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            
            long time = world.getDayTime() % 24000;
            boolean isNightime = time > 13300 && time < 23200;
            
            int villagerCount = world.getEntitiesWithinAABB( AbstractVillagerEntity.class,
                                                             new AxisAlignedBB( player.getPosX() - AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() - AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() - AmbienceRemixed.VILLAGE_RADIUS_Z,
                                                                                player.getPosX() + AmbienceRemixed.VILLAGE_RADIUS_X, player.getPosY() + AmbienceRemixed.VILLAGE_RADIUS_Y, player.getPosZ() + AmbienceRemixed.VILLAGE_RADIUS_Z ) ).size();
            
            return ( ( villagerCount > AmbienceRemixed.VILLAGE_POPULATION_REQUIREMENT ) && isNightime );
        }        
    }
    
    public final class BiomeStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if( player == null )
                return false;
            
            World world = player.world;
            BlockPos pos = new BlockPos( player.getPositionVec() );
            Biome biome = world.getBiome( pos );
            
            return ( lastBiomeToPlay == biome );
        }
    }
}
