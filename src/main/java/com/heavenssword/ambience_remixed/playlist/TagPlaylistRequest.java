package com.heavenssword.ambience_remixed.playlist;

// Java
import java.util.Set;

// Minecraft
import net.minecraftforge.common.BiomeDictionary;

//Ambience Remixed
import com.heavenssword.ambience_remixed.PlayPriority;

public final class TagPlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final Set<BiomeDictionary.Type> tagSet;
    private final boolean isPrimary;
    
    // Construction
    public TagPlaylistRequest( Set<BiomeDictionary.Type> _tagSet, boolean _isPrimary, PlayPriority _playPriority, Double _fadeTime, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _fadeTime, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        tagSet = _tagSet;
        isPrimary = _isPrimary;
    }
    
    // Public Methods
    public Set<BiomeDictionary.Type> getTagSet()
    {
        return tagSet;
    }
    
    public boolean getIsPrimary()
    {
        return isPrimary;
    }
}
