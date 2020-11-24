package com.heavenssword.ambience_remixed.playlist;

// Minecraft
import net.minecraft.util.ResourceLocation;

public final class BiomePlaylistRequestBuilder extends PlaylistRequestBuilder<BiomePlaylistRequestBuilder>
{        
    // Construction
    public BiomePlaylistRequestBuilder() {}
    
    // Public Methods
    public BiomePlaylistRequest buildBiomePlayRequest( ResourceLocation _biomeRegistry )
    {
        return new BiomePlaylistRequest( _biomeRegistry, playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
