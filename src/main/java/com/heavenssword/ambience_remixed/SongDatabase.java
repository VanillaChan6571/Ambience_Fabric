package com.heavenssword.ambience_remixed;

// Java
import java.util.Map;
import java.util.HashMap;

// Minecraft
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public final class SongDatabase
{
    // Private Fields
    private final Map<SongEvents, String[]> eventSongs = new HashMap<SongEvents, String[]>();
    private final Map<Biome, String[]> biomeSongs = new HashMap<Biome, String[]>();
    private final Map<BiomeDictionary.Type, String[]> primaryTagSongs = new HashMap<BiomeDictionary.Type, String[]>();
    private final Map<BiomeDictionary.Type, String[]> secondaryTagSongs = new HashMap<BiomeDictionary.Type, String[]>();
    
    // Public Methods
    public void addSongsForEvent( SongEvents eventKey, String[] songs )
    {
        if( eventKey == null || songs == null )
            return;
        
        eventSongs.put( eventKey, songs );
    }
    
    public void addSongsForBiome( Biome biome, String[] songs )
    {
        if( biome == null || songs == null )
            return;
        
        biomeSongs.put( biome, songs );
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
    
    public String[] getSongsForBiome( Biome biome )
    {
        if( biome != null && biomeSongs.containsKey( biome ) )
            return biomeSongs.get( biome );
        
        return null;
    }
    
    public String[] getSongsForPrimaryTag( BiomeDictionary.Type primaryTag )
    {
        if( primaryTag != null && primaryTagSongs.containsKey( primaryTag ) )
            return primaryTagSongs.get( primaryTag );
        
        return null;
    }
    
    public String[] getSongsForSecondaryTag( BiomeDictionary.Type secondaryTag )
    {
        if( secondaryTag != null && secondaryTagSongs.containsKey( secondaryTag ) )
            return secondaryTagSongs.get( secondaryTag );
        
        return null;
    }
    
    public void clear()
    {
        eventSongs.clear();
        biomeSongs.clear();
        primaryTagSongs.clear();
        secondaryTagSongs.clear();
    }
}
