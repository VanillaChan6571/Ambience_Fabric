package net.mcneko.vanilla.ambience_remixed.playlist;

// Ambience Remixed
import net.mcneko.vanilla.ambience_remixed.SongEvents;

public final class EventPlaylistRequestBuilder extends PlaylistRequestBuilder<EventPlaylistRequestBuilder>
{        
    // Construction
    public EventPlaylistRequestBuilder() {}
    
    // Public Methods
    public EventPlaylistRequest buildEventPlayRequest( SongEvents _songEvent )
    {
        return new EventPlaylistRequest( _songEvent, playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
