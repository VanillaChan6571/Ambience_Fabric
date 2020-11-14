package com.heavenssword.ambience_remixed.playlist;

// Java
import java.util.ArrayList;

import com.heavenssword.ambience_remixed.PlayPriority;

public interface IPlaylistRequest
{
    public PlayPriority getPlayPriority();
    
    public boolean getCanBeOverriden();
    public boolean getShouldLoop();
    public boolean getShouldAllowMerging();    
    public boolean isPlaylistStillValid();
    public boolean getShouldDeferPlay();
    
    public ArrayList<String> getPlaylist();    
    public void setPlaylist( String[] newPlaylist );    
    public void setPlaylist( ArrayList<String> newPlaylist );
}
