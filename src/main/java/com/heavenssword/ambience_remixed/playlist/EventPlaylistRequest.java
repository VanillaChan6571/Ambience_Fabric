package com.heavenssword.ambience_remixed.playlist;

import com.heavenssword.ambience_remixed.PlayPriority;
import com.heavenssword.ambience_remixed.SongEvents;

public class EventPlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final SongEvents songEvent;
    
    // Construction    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        songEvent = _songEvent;
    }
    
    // Public Methods
    public SongEvents getSongEvent()
    {
        return songEvent;
    }
}
