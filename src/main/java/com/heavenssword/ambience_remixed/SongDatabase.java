package com.heavenssword.ambience_remixed;

// Java
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// Minecraft
import net.minecraft.util.ResourceLocation;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;

public final class SongDatabase
{
    // Private Fields
    private final Map<SongEvents, String[]> eventSongs = new HashMap<SongEvents, String[]>();
    private final Map<String, String[]> customEventSongs = new HashMap<String, String[]>();
    private final Map<ResourceLocation, String[]> biomeSongs = new HashMap<ResourceLocation, String[]>();
    private final Map<BiomeDictionary.Type, String[]> primaryTagSongs = new HashMap<BiomeDictionary.Type, String[]>();
    private final Map<BiomeDictionary.Type, String[]> secondaryTagSongs = new HashMap<BiomeDictionary.Type, String[]>();
    
    // Public Methods
    public void addSongsForEvent( SongEvents eventKey, String[] songs )
    {
        if( eventKey == null || songs == null )
            return;
        
        eventSongs.put( eventKey, songs );
    }
    
    public void addSongsForCustomEvent( String eventKey, String[] songs )
    {
        if( eventKey == null || songs == null )
            return;
        
        customEventSongs.put( eventKey, songs );
    }
    
    public void addSongsForBiome( ResourceLocation biomeResource, String[] songs )
    {
        if( biomeResource == null || songs == null )
            return;
        
        biomeSongs.put( biomeResource, songs );
    }
    
    public void addSongsForPrimaryTag( BiomeDictionary.Type primaryTag, String[] songs )
    {
        if( primaryTag == null || songs == null )
            return;
        
        primaryTagSongs.put( primaryTag, songs );
    }
    
    public void addSongsForSecondaryTag( BiomeDictionary.Type secondaryTag, String[] songs )
    {
        if( secondaryTag == null || songs == null )
            return;
        
        secondaryTagSongs.put( secondaryTag, songs );
    }
    
    public String[] getSongsForEvent( SongEvents eventKey )
    {
        if( eventKey != null && eventSongs.containsKey( eventKey ) )
            return eventSongs.get( eventKey );

        return null;
    }
    
    public String[] getSongsForCustomEvent( String eventKey )
    {
        if( eventKey != null && customEventSongs.containsKey( eventKey ) )
            return customEventSongs.get( eventKey );

        return null;
    }
    
    public String[] getSongsForBiome( ResourceLocation biomeResource )
    {
        if( biomeResource != null && biomeSongs.containsKey( biomeResource ) )
            return biomeSongs.get( biomeResource );
        
        return null;
    }
    
    public String[] getSongsForPrimaryTag( Set<BiomeDictionary.Type> primaryTagSet )
    {
        if( primaryTagSet != null )
        {
            ArrayList<String> mergedTagPlaylist = new ArrayList<String>();            
            for( BiomeDictionary.Type primaryTag : primaryTagSet )
            {
                if( primaryTag != null && primaryTagSongs.containsKey( primaryTag ) )
                    mergedTagPlaylist.addAll( Arrays.asList( primaryTagSongs.get( primaryTag ) ) );
            }

            return mergedTagPlaylist.toArray( new String[0] );            
        }
        
        return null;
    }
    
    public String[] getSongsForSecondaryTag( Set<BiomeDictionary.Type> secondaryTagSet )
    {      
        if( secondaryTagSet != null )
        {
            ArrayList<String> mergedTagPlaylist = new ArrayList<String>();            
            for( BiomeDictionary.Type secondaryTag : secondaryTagSet )
            {
                if( secondaryTag != null && secondaryTagSongs.containsKey( secondaryTag ) )
                    mergedTagPlaylist.addAll( Arrays.asList( secondaryTagSongs.get( secondaryTag ) ) );
            }

            return mergedTagPlaylist.toArray( new String[0] );            
        }
        
        return null;
    }
    
    public void clear()
    {
        eventSongs.clear();
        customEventSongs.clear();
        biomeSongs.clear();
        primaryTagSongs.clear();
        secondaryTagSongs.clear();
    }
}
