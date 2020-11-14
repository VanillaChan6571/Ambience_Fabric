package com.heavenssword.ambience_remixed.playlist;

// Minecraft
import net.minecraft.util.ResourceLocation;

// Ambience Remixed
import com.heavenssword.ambience_remixed.PlayPriority;

public final class BiomePlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final ResourceLocation biomeRegistry;
    
    // Construction    
    public BiomePlaylistRequest( ResourceLocation _biomeRegistry, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        biomeRegistry = _biomeRegistry;
    }
    
    // Public Methods
    public ResourceLocation getBiomeRegistry()
    {
        return biomeRegistry;
    }
}
