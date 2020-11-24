package com.heavenssword.ambience_remixed.playlist;

// Ambience Remixed
import com.heavenssword.ambience_remixed.PlayPriority;

public final class CustomEventPlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final String eventName;
    
    // Construction    
    public CustomEventPlaylistRequest( String _eventName, PlayPriority _playPriority, Double _fadeTime, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _fadeTime, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        eventName = _eventName;
    }
    
    // Public Methods
    public String getEventName()
    {
        return eventName;
    }
}
