package com.heavenssword.ambience_remixed;

public class EventPlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final SongEvents songEvent;
    
    // Construction
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority ) 
    { 
        super( _playPriority );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden )
    {
        super( _playPriority, _canBeOverriden );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, IPlaylistStillValidCallback _playListStillValidCallback ) 
    { 
        super( _playPriority, _playListStillValidCallback );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _playListStillValidCallback );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _playListStillValidCallback );
        
        songEvent = _songEvent;
    }
    
    public EventPlaylistRequest( SongEvents _songEvent, PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _canBeOverriden, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        songEvent = _songEvent;
    }
    
    // Public Methods
    public SongEvents getSongEvent()
    {
        return songEvent;
    }
}
