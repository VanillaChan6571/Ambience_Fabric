package net.mcneko.vanilla.ambience_remixed.playlist;

// Java
import java.util.Set;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;

public final class TagPlaylistRequestBuilder extends PlaylistRequestBuilder<TagPlaylistRequestBuilder>
{        
    // Construction
    public TagPlaylistRequestBuilder() {}
    
    // Public Methods
    public TagPlaylistRequest buildTagPlayRequest( Set<BiomeDictionary.Type> _tagSet, boolean _isPrimary )
    {
        return new TagPlaylistRequest( _tagSet, _isPrimary, playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
