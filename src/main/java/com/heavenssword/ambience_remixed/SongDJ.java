package com.heavenssword.ambience_remixed;

// Ambience Remixed
import com.heavenssword.ambience_remixed.audio.JukeboxRunnable;

public class SongDJ
{
    // Private Fields
    private SongDatabase songDB = null;
    private JukeboxRunnable jukebox = null;
    
    private IPlaylistRequest activePlaylistRequest = null;
    
    // Construction
    public SongDJ( JukeboxRunnable _jukebox, SongDatabase _songDB )
    {
        jukebox = _jukebox;
        songDB = _songDB;
    }
    
    // Public Methods
    public void requestPlaylist( IPlaylistRequest playlistRequest )
    {
        if( playlistRequest == null )
            return;
                
        if( shouldReplaceActivePlaylistRequest( playlistRequest ) )
            beginPlaylist( playlistRequest );
        //else if( playlistRequest.getShouldAllowMerging() && priorityComparisonResult == 0 )// Same priority as the active list
        //    MergePlaylistIntoActive( playlistRequest );
    }
    
    public void requestPlaylistForEvent( EventPlaylistRequest playlistRequest )
    {
        if( songDB != null && playlistRequest != null )
        {
            playlistRequest.setPlaylist( songDB.getSongsForEvent( playlistRequest.getSongEvent() ) );
            
            AmbienceRemixed.getLogger().debug( "SongDJ.RequestPlaylistForEvent() - Reqesting playlist for event \"" + playlistRequest.getSongEvent() + "\"" );
            requestPlaylist( playlistRequest );
        }
    }
    
    public void requestPlaylistForBiome( BiomePlaylistRequest playlistRequest )
    {
        if( songDB != null && playlistRequest != null )
        {
            playlistRequest.setPlaylist( songDB.getSongsForBiome( playlistRequest.getBiome() ) );
            
            requestPlaylist( playlistRequest );
        }
    }
    
    public void RequestPlaylistForTag( TagPlaylistRequest playlistRequest )
    {
        if( songDB != null && playlistRequest != null )
        {
            if( playlistRequest.getIsPrimary() )
                playlistRequest.setPlaylist( songDB.getSongsForPrimaryTag( playlistRequest.getTag() ) );
            else
                playlistRequest.setPlaylist( songDB.getSongsForSecondaryTag( playlistRequest.getTag() ) );
                
            requestPlaylist( playlistRequest );
        }
    }
    
    // Private Methods    
    private void beginPlaylist( IPlaylistRequest playlistRequest )
    {        
        activePlaylistRequest = playlistRequest;
        
        if( jukebox != null )
        {
            jukebox.setPlaylist( activePlaylistRequest.getPlaylist().toArray( new String[0] ) );
            
            if( !playlistRequest.getShouldDeferPlay() )
                jukebox.playNextSong();
        }
    }
    
    private boolean shouldReplaceActivePlaylistRequest( IPlaylistRequest newPlayListRequest )
    {
        AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - playPriority val = \"" + newPlayListRequest.getPlayPriority().Value + "\"" );
        if( activePlaylistRequest != null )
        {
            AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - activePlaylist val = \"" + activePlaylistRequest.getPlayPriority().Value + "\"" );
            AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - compareTo result = \"" + PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) + "\"" );
        
            boolean isHigherPriority = PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) < 0;
         
            boolean isActivePlaylistStillValid = false;
            if( activePlaylistRequest.getCanBeOverriden() )
                isActivePlaylistStillValid = activePlaylistRequest.isPlaylistStillValid();
            
            AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - isHigherPriority = " + ( isHigherPriority ? "TRUE" : "FALSE" ) + " isActivePlaylistStillValid = " + ( isActivePlaylistStillValid ? "TRUE" : "FALSE" ) );
            
            return ( isHigherPriority || !isActivePlaylistStillValid );
        }
        
        return true;
    }
    
    /*private void mergePlaylistIntoActive( IPlaylistRequest playlistRequest )
    {
        ArrayList<String> mergedList = ( activePlaylistRequest != null ? new ArrayList<String>( activePlaylistRequest.getPlaylist() ) : new ArrayList<String>() );
        
        for( String song : playlistRequest.getPlaylist() )
        {
            if( !mergedList.contains( song ) )
                mergedList.add( song );
        }
        
        PlaylistRequest newPlaylistRequest = new PlaylistRequest( activePlaylistRequest ); 
        
        playlistRequest.setPlaylist( mergedList );
        
        BeginPlaylist( new PlaylistRequest( activePlaylist ), false );
    }*/
}
