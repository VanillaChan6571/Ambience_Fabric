package com.heavenssword.ambience_remixed;

// Java
import java.util.ArrayList;

public interface IPlaylistRequest
{
    public PlayPriority getPlayPriority();
    
    public boolean getCanBeOverriden();    
    public boolean getShouldAllowMerging();    
    public boolean isPlaylistStillValid();
    public boolean getShouldDeferPlay();
    
    public ArrayList<String> getPlaylist();    
    public void setPlaylist( String[] newPlaylist );    
    public void setPlaylist( ArrayList<String> newPlaylist );
}
