package com.heavenssword.ambience_remixed.playlist;

// Minecraft
import net.minecraft.world.biome.Biome;

public final class BiomePlaylistRequestBuilder extends PlaylistRequestBuilder<BiomePlaylistRequestBuilder>
{        
    // Construction
    public BiomePlaylistRequestBuilder() {}
    
    // Public Methods
    public BiomePlaylistRequest buildBiomePlayRequest( Biome _biome )
    {
        return new BiomePlaylistRequest( _biome, playPriority, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
