package com.heavenssword.ambience_remixed.playlist;

// Java
import java.util.ArrayList;

import com.heavenssword.ambience_remixed.PlayPriority;

public class PlaylistRequest implements IPlaylistRequest
{
    // Protected Fields
    protected final PlayPriority playPriority;
    protected final boolean canBeOverriden;
    protected final boolean shouldLoop;
    protected final boolean shouldDeferPlay;
    protected final boolean shouldAllowMerging;
    
    protected final IPlaylistStillValidCallback playlistStillValidCallback;
    
    protected ArrayList<String> playlist = null;
    
    // Construction    
    public PlaylistRequest( PlayPriority _playPriority, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playlistStillValidCallback ) 
    { 
        playPriority = _playPriority;
        canBeOverriden = _canBeOverriden;
        shouldLoop = _shouldLoop;
        shouldDeferPlay = _shouldDeferPlay;
        shouldAllowMerging = _shouldAllowMerging;        
        playlistStillValidCallback = _playlistStillValidCallback;
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
    public boolean getShouldLoop()
    {
        return shouldLoop;
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
        if( newPlaylist == null || newPlaylist.length <= 0 )
            return;
        
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
