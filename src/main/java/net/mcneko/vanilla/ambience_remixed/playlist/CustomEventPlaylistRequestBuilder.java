package net.mcneko.vanilla.ambience_remixed.playlist;

public final class CustomEventPlaylistRequestBuilder extends PlaylistRequestBuilder<CustomEventPlaylistRequestBuilder>
{        
    // Construction
    public CustomEventPlaylistRequestBuilder() {}
    
    // Public Methods
    public CustomEventPlaylistRequest buildCustomEventPlayRequest( String _eventName )
    {
        return new CustomEventPlaylistRequest( _eventName, playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
}
