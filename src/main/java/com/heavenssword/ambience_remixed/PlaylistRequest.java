package com.heavenssword.ambience_remixed;

// Java
import java.util.ArrayList;

public class PlaylistRequest implements IPlaylistRequest
{
    // Protected Fields
    protected final PlayPriority playPriority;
    protected final boolean canBeOverriden;
    protected final boolean shouldAllowMerging;
    protected final boolean shouldDeferPlay;
    
    protected final IPlaylistStillValidCallback playlistStillValidCallback;
    
    protected ArrayList<String> playlist = null;
    
    // Construction    
    public PlaylistRequest( PlayPriority _playPriority ) 
    { 
        playPriority = _playPriority;
        canBeOverriden = false;
        shouldAllowMerging = false;
        shouldDeferPlay = false;
        playlistStillValidCallback = null;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = false;
        shouldDeferPlay = false;
        playlistStillValidCallback = null;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = false;
        shouldDeferPlay = _shouldDeferPlay;
        playlistStillValidCallback = null;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = _shouldAllowMerging;
        shouldDeferPlay = _shouldDeferPlay;
        playlistStillValidCallback = null;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, IPlaylistStillValidCallback playListStillValidCallback ) 
    { 
        playPriority = _playPriority;
        canBeOverriden = false;
        shouldAllowMerging = false;
        shouldDeferPlay = false;
        playlistStillValidCallback = playListStillValidCallback;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, IPlaylistStillValidCallback playListStillValidCallback )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = false;
        shouldDeferPlay = false;
        playlistStillValidCallback = playListStillValidCallback;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, IPlaylistStillValidCallback playListStillValidCallback )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = false;
        shouldDeferPlay = _shouldDeferPlay;
        playlistStillValidCallback = playListStillValidCallback;
    }
    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback playListStillValidCallback )
    {
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldAllowMerging = _shouldAllowMerging;
        shouldDeferPlay = _shouldDeferPlay;
        playlistStillValidCallback = playListStillValidCallback;
    }
    
    // Public Methods
    @Override
    public PlayPriority getPlayPriority()
    {
        return playPriority;
    }
    
    @Override
    public boolean getCanBeOverriden()
    {
        return canBeOverriden;
    }
    
    @Override
    public boolean getShouldAllowMerging()
    {
        return shouldAllowMerging;
    }
    
    @Override
    public boolean isPlaylistStillValid()
    {        
        return ( playlistStillValidCallback != null ? playlistStillValidCallback.isPlaylistStillValid() : false );
    }
    
    @Override
    public boolean getShouldDeferPlay()
    {
        return shouldDeferPlay;
    }
    
    @Override
    public ArrayList<String> getPlaylist()
    {
        return playlist;
    }
    
    @Override
    public void setPlaylist( String[] newPlaylist )
    {
        if( playlist == null )
            playlist = new ArrayList<String>();
        else
            playlist.clear();
        
        for( String song : newPlaylist )
            playlist.add( song );
    }
    
    @Override
    public void setPlaylist( ArrayList<String> newPlaylist )
    {
        playlist = newPlaylist;
    }
}
