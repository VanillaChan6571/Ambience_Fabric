package com.heavenssword.ambience_remixed;

// Minecraft
import net.minecraft.world.biome.Biome;

public final class BiomePlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final Biome biome;
    
    // Construction
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority ) 
    { 
        super( _playPriority );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden )
    {
        super( _playPriority, _canBeOverriden );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, IPlaylistStillValidCallback _playListStillValidCallback ) 
    { 
        super( _playPriority, _playListStillValidCallback );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _playListStillValidCallback );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _playListStillValidCallback );
        
        biome = _biome;
    }
    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        biome = _biome;
    }
    
    // Public Methods
    public Biome getBiome()
    {
        return biome;
    }
}
