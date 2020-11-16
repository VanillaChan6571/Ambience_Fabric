package com.heavenssword.ambience_remixed;


// Ambience Remixed
import com.heavenssword.ambience_remixed.audio.JukeboxRunnable;
import com.heavenssword.ambience_remixed.playlist.BiomePlaylistRequest;
import com.heavenssword.ambience_remixed.playlist.CustomEventPlaylistRequest;
import com.heavenssword.ambience_remixed.playlist.EventPlaylistRequest;
import com.heavenssword.ambience_remixed.playlist.EventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.IPlaylistRequest;
import com.heavenssword.ambience_remixed.playlist.TagPlaylistRequest;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Set;

import com.heavenssword.ambience_remixed.audio.IAudioPlaybackListener;

public class SongDJ implements IAudioPlaybackListener
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
        
        if( jukebox != null )
            jukebox.registerAudioPlaybackListener( this );
    }
    
    // Public Methods
    public boolean canRequestEventPlaylist( SongEvents eventKey )
    {
        if( songDB != null )
            return songDB.doesEventHavePlaylist( eventKey );
        
        return false;
    }
    
    public boolean canRequestCustomEventPlaylist( String eventKey )
    {
        if( songDB != null )
            return songDB.doesCustomEventHavePlaylist( eventKey );
        
        return false;
    }
    
    public boolean canRequestBiomePlaylist( ResourceLocation eventKey )
    {
        if( songDB != null )
            return songDB.doesBiomeHavePlaylist( eventKey );
        
        return false;
    }
    
    public boolean canRequestPrimaryTagSetPlaylist( Set<BiomeDictionary.Type> primaryTagSet )
    {
        if( songDB != null )
            return songDB.doesPrimaryTagSetHavePlaylist( primaryTagSet );
        
        return false;
    }
    
    public boolean canRequestSecondaryTagSetPlaylist( Set<BiomeDictionary.Type> secondaryTagSet )
    {
        if( songDB != null )
            return songDB.doesSecondaryTagSetHavePlaylist( secondaryTagSet );
        
        return false;
    }
    
    public void requestPlaylist( IPlaylistRequest playlistRequest )
    {
        if( playlistRequest == null )
            return;
                
        if( shouldReplaceActivePlaylistRequest( playlistRequest ) )
            beginPlaylist( playlistRequest );
        //else if( playlistRequest.getShouldAllowMerging() && priorityComparisonResult == 0 )// Same priority as the active list
        //    MergePlaylistIntoActive( playlistRequest );
    }
    
    public boolean requestPlaylistForEvent( EventPlaylistRequest playlistRequest )
    {
        boolean retValue = false;
        
        if( songDB != null && playlistRequest != null )
        {
            playlistRequest.setPlaylist( songDB.getSongsForEvent( playlistRequest.getSongEvent() ) );
            
            if( playlistRequest.getPlaylist() == null )
                return false;
            
            //AmbienceRemixed.getLogger().debug( "SongDJ.RequestPlaylistForEvent() - Reqesting playlist for event \"" + playlistRequest.getSongEvent() + "\"" );
            requestPlaylist( playlistRequest );
            
            retValue = true;
        }
        
        return retValue;
    }
    
    public boolean requestPlaylistForCustomEvent( CustomEventPlaylistRequest playlistRequest )
    {
        boolean retValue = false;
        
        if( songDB != null && playlistRequest != null )
        {
            playlistRequest.setPlaylist( songDB.getSongsForCustomEvent( playlistRequest.getEventName() ) );
            
            if( playlistRequest.getPlaylist() == null )
                return false;
            
            AmbienceRemixed.getLogger().debug( "SongDJ.requestPlaylistForCustomEvent() - Reqesting playlist for customEvent \"" + playlistRequest.getEventName() + "\"" );
            requestPlaylist( playlistRequest );
            
            retValue = true;
        }
        
        return retValue;
    }
    
    public boolean requestPlaylistForBiome( BiomePlaylistRequest playlistRequest )
    {
        boolean retValue = false;
        
        if( songDB != null && playlistRequest != null )
        {
            playlistRequest.setPlaylist( songDB.getSongsForBiome( playlistRequest.getBiomeRegistry() ) );
            
            if( playlistRequest.getPlaylist() == null )
                return false;
            
            requestPlaylist( playlistRequest );
            
            retValue = true;
        }
        
        return retValue;
    }
    
    public boolean requestPlaylistForTags( TagPlaylistRequest playlistRequest )
    {
        boolean retValue = false;
        
        if( songDB != null && playlistRequest != null )
        {
            if( playlistRequest.getIsPrimary() )
                playlistRequest.setPlaylist( songDB.getSongsForPrimaryTag( playlistRequest.getTagSet() ) );
            else
                playlistRequest.setPlaylist( songDB.getSongsForSecondaryTag( playlistRequest.getTagSet() ) );
            
            if( playlistRequest.getPlaylist() == null )
                return false;
                
            requestPlaylist( playlistRequest );
            
            retValue = true;
        }
        
        return retValue;
    }
    
    @Override
    public void onPlaybackStarted() { }

    @Override
    public void onPlaybackFinished()
    {
        if( jukebox == null )
            return;
        
        // If a non-looping playlist just finised, start the fall-back music.
        if( activePlaylistRequest == null || ( !jukebox.isPlaying() && !activePlaylistRequest.getShouldLoop() ) )
            requestPlaylistForEvent( new EventPlaylistRequestBuilder().playPriority( PlayPriority.LOWEST )
                                                                      .buildEventPlayRequest( SongEvents.GENERIC ) );
    }
    
    // Private Methods    
    private void beginPlaylist( IPlaylistRequest playlistRequest )
    {
        if( playlistRequest == null || playlistRequest.getPlaylist() == null )
            return;
        
        activePlaylistRequest = playlistRequest;
        
        if( jukebox != null && activePlaylistRequest.getPlaylist() != null )
        {
            jukebox.setPlaylist( activePlaylistRequest.getPlaylist().toArray( new String[0] ) );
            jukebox.setIsPlaylistLoopingEnabled( activePlaylistRequest.getShouldLoop() );
            
            if( !playlistRequest.getShouldDeferPlay() )
                jukebox.playNextSong();
        }
    }
    
    private boolean shouldReplaceActivePlaylistRequest( IPlaylistRequest newPlayListRequest )
    {
        //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - playPriority val = \"" + newPlayListRequest.getPlayPriority().Value + "\"" );
        if( activePlaylistRequest != null )
        {
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - activePlaylist val = \"" + activePlaylistRequest.getPlayPriority().Value + "\"" );
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - compareTo result = \"" + PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) + "\"" );
        
            boolean isHigherPriority = PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) < 0;
         
            boolean isActivePlaylistStillValid = true;
            if( activePlaylistRequest.getCanBeOverriden() )
                isActivePlaylistStillValid = activePlaylistRequest.isPlaylistStillValid();
            
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - isHigherPriority = " + ( isHigherPriority ? "TRUE" : "FALSE" ) + " isActivePlaylistStillValid = " + ( isActivePlaylistStillValid ? "TRUE" : "FALSE" ) );
            
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
