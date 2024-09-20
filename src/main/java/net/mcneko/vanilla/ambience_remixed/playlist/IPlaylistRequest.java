package net.mcneko.vanilla.ambience_remixed.playlist;

// Java
import java.util.ArrayList;

import net.mcneko.vanilla.ambience_remixed.PlayPriority;

public interface IPlaylistRequest
{
    public PlayPriority getPlayPriority();
    
    public boolean getCanBeOverriden();
    public boolean getShouldLoop();
    public boolean getShouldAllowMerging();    
    public boolean isPlaylistStillValid();
    public boolean getShouldDeferPlay();
    public Double getFadeTime();
    
    public ArrayList<String> getPlaylist();    
    public void setPlaylist( String[] newPlaylist );    
    public void setPlaylist( ArrayList<String> newPlaylist );
}
