package com.heavenssword.ambience_remixed.playlist;

import com.heavenssword.ambience_remixed.PlayPriority;

// Minecraft
import net.minecraft.world.biome.Biome;

public final class BiomePlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final Biome biome;
    
    // Construction    
    public BiomePlaylistRequest( Biome _biome, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        biome = _biome;
    }
    
    // Public Methods
    public Biome getBiome()
    {
        return biome;
    }
}
