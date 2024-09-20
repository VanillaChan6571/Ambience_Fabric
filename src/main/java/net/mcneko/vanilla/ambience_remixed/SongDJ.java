package net.mcneko.vanilla.ambience_remixed;

// Java
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

// Minecraft
import net.mcneko.vanilla.ambience_remixed.audio.IAudioPlaybackListener;
import net.mcneko.vanilla.ambience_remixed.audio.JukeboxRunnable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.BiomeDictionary;

// Ambience Remixed
import net.mcneko.vanilla.ambience_remixed.playlist.BiomePlaylistRequest;
import net.mcneko.vanilla.ambience_remixed.playlist.CustomEventPlaylistRequest;
import net.mcneko.vanilla.ambience_remixed.playlist.EventPlaylistRequest;
import net.mcneko.vanilla.ambience_remixed.playlist.EventPlaylistRequestBuilder;
import net.mcneko.vanilla.ambience_remixed.playlist.IPlaylistRequest;
import net.mcneko.vanilla.ambience_remixed.playlist.TagPlaylistRequest;

public class SongDJ implements IAudioPlaybackListener
{
    // Private Fields
    private SongDatabase songDB = null;
    private JukeboxRunnable jukebox = null;
    
    private IPlaylistRequest activePlaylistRequest = null;
    
    private final float requestCooldownTime = 1.5f;// seconds
    private final float biomeRequestCooldownTime = 15f;// seconds
    private float requestCooldownTimer = 0.0f;
    
    // Construction
    public SongDJ( JukeboxRunnable _jukebox, SongDatabase _songDB )
    {
        jukebox = _jukebox;
        songDB = _songDB;
        
        if( jukebox != null )
            jukebox.registerAudioPlaybackListener( this );
    }
    
    // Public Methods
    public void tick( double deltaTime )
    {
        if( requestCooldownTimer > 0.0f )
            requestCooldownTimer = MathHelper.clamp( requestCooldownTimer - (float)deltaTime, 0.0f, requestCooldownTime);
    }
    
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
            
            //AmbienceRemixed.getLogger().debug( "SongDJ.requestPlaylistForCustomEvent() - Reqesting playlist for customEvent \"" + playlistRequest.getEventName() + "\"" );
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
            Set<String> combinedPlaylist = new LinkedHashSet<String>();

            String[] biomePlaylist = songDB.getSongsForBiome( playlistRequest.getBiomeRegistry() );
            if( biomePlaylist != null )
                combinedPlaylist.addAll( Arrays.asList( biomePlaylist ) );
   
            String[] primaryTagPlaylist = songDB.getSongsForPrimaryTag( playlistRequest.getTagSet() );
            if( primaryTagPlaylist != null )
                combinedPlaylist.addAll( Arrays.asList( primaryTagPlaylist ) );
   
            String[] secondaryTagPlaylist = songDB.getSongsForSecondaryTag( playlistRequest.getTagSet() );
            if( secondaryTagPlaylist != null )
                combinedPlaylist.addAll( Arrays.asList( secondaryTagPlaylist ) );
            
            if( combinedPlaylist.size() > 0 )
                playlistRequest.setPlaylist( combinedPlaylist.toArray( new String[0] ) );
            else
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
        
        if( jukebox != null )
        {
            requestCooldownTimer = ( playlistRequest instanceof BiomePlaylistRequest || playlistRequest instanceof TagPlaylistRequest ) ? biomeRequestCooldownTime : requestCooldownTime;
            
            boolean isActiveSongIncludedInNewList = playlistRequest.getPlaylist().contains( jukebox.getCurrentSongName() );
        
            activePlaylistRequest = playlistRequest;
            
            jukebox.setPlaylist( activePlaylistRequest.getPlaylist().toArray( new String[0] ) );
            jukebox.setIsPlaylistLoopingEnabled( activePlaylistRequest.getShouldLoop() );
            
            if( !playlistRequest.getShouldDeferPlay() && !isActiveSongIncludedInNewList )
                jukebox.playNextSong( activePlaylistRequest.getFadeTime() );
        }
    }
    
    private boolean shouldReplaceActivePlaylistRequest( IPlaylistRequest newPlayListRequest )
    {
        if( requestCooldownTimer > 0.0f )
            return false;
        
        //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - playPriority val = \"" + newPlayListRequest.getPlayPriority().Value + "\"" );
        if( activePlaylistRequest != null )
        {
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - activePlaylist val = \"" + activePlaylistRequest.getPlayPriority().Value + "\"" );
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - compareTo result = \"" + PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) + "\"" );
        
            boolean isHigherPriority = PlayPriority.compareTo( newPlayListRequest.getPlayPriority(), activePlaylistRequest.getPlayPriority() ) < 0;
         
            boolean isActivePlaylistStillValid = true;
            if( activePlaylistRequest.getCanBeOverriden() )
                isActivePlaylistStillValid = activePlaylistRequest.isPlaylistStillValid();
            
            boolean isNothingPlaying = true;
            if( jukebox != null )
                isNothingPlaying = ( jukebox.getCurrentSongName() == null || !jukebox.isPlaying() ) && !jukebox.getIsPlaylistLoopingEnabled();
            
            //AmbienceRemixed.getLogger().debug( "SongDJ.shouldReplaceActivePlaylistRequest() - isHigherPriority = " + ( isHigherPriority ? "TRUE" : "FALSE" ) + " isActivePlaylistStillValid = " + ( isActivePlaylistStillValid ? "TRUE" : "FALSE" ) );
            
            return ( isHigherPriority || !isActivePlaylistStillValid || isNothingPlaying );
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
