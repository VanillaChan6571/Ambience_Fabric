package com.heavenssword.ambience_remixed.playlist;

import java.util.Set;

// Minecraft
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

public final class BiomePlaylistRequestBuilder extends PlaylistRequestBuilder<BiomePlaylistRequestBuilder>
{        
    // Construction
    public BiomePlaylistRequestBuilder() {}
    
    // Public Methods
    public BiomePlaylistRequest buildBiomePlayRequest( ResourceLocation _biomeRegistry, Set<BiomeDictionary.Type> _tagSet )
    {
        return new BiomePlaylistRequest( _biomeRegistry, _tagSet, playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
