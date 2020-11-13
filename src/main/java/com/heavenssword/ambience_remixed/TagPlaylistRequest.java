package com.heavenssword.ambience_remixed;

// Minecraft
import net.minecraftforge.common.BiomeDictionary;

public final class TagPlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final BiomeDictionary.Type tag;
    private final boolean isPrimary;
    
    // Construction
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority ) 
    { 
        super( _playPriority );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden )
    {
        super( _playPriority, _canBeOverriden );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, IPlaylistStillValidCallback _playListStillValidCallback ) 
    { 
        super( _playPriority, _playListStillValidCallback );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _playListStillValidCallback );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _playListStillValidCallback );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    public TagPlaylistRequest( BiomeDictionary.Type _tag, boolean _isPrimary, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        tag = _tag;
        isPrimary = _isPrimary;
    }
    
    // Public Methods
    public BiomeDictionary.Type getTag()
    {
        return tag;
    }
    
    public boolean getIsPrimary()
    {
        return isPrimary;
    }
}
