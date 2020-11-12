package com.heavenssword.ambience_remixed;

// Java
import java.util.ArrayList;

// Mojang
import com.mojang.datafixers.util.Pair;

// Minecraft
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

// Ambience Remixed
import com.heavenssword.ambience_remixed.audio.JukeboxRunnable;

public class SongDJ
{
    // Private Fields
    private SongDatabase songDB = null;
    private JukeboxRunnable jukebox = null;
    
    private Pair<PlayPriority, ArrayList<String>> activePlaylist = null;
    
    // Construction
    public SongDJ( JukeboxRunnable _jukebox, SongDatabase _songDB )
    {
        jukebox = _jukebox;
        songDB = _songDB;
    }
    
    // Public Methods
    public void RequestPlaylist( String[] songPlaylist, PlayPriority playPriority )
    {
        RequestPlaylist( songPlaylist, playPriority, false );
    }
    
    public void RequestPlaylist( String[] songPlaylist, PlayPriority playPriority, boolean shouldAllowMerge )
    {
        if( activePlaylist == null || PlayPriority.compareTo( playPriority, activePlaylist.getFirst() ) < 0 )
            BeginPlaylist( songPlaylist, playPriority, true );
        else if( shouldAllowMerge && PlayPriority.compareTo( playPriority, activePlaylist.getFirst() ) == 0 )// Same priority as the active list
            MergePlaylistIntoActive( songPlaylist );
    }
    
    public void RequestPlaylistForEvent( SongEvents eventKey, PlayPriority playPriority )
    {
        if( songDB != null )
        {
            AmbienceRemixed.getLogger().debug( "SongDJ.RequestPlaylistForEvent() - Reqesting playlist for event \"" + eventKey + "\"" );
            RequestPlaylist( songDB.getSongsForEvent( eventKey ), playPriority );
        }
    }
    
    public void RequestPlaylistForBiome( Biome biome, PlayPriority playPriority )
    {
        if( songDB != null )
            RequestPlaylist( songDB.getSongsForBiome( biome ), playPriority );
    }
    
    public void RequestPlaylistForPrimaryTag( BiomeDictionary.Type primaryTag, PlayPriority playPriority )
    {
        if( songDB != null )
            RequestPlaylist( songDB.getSongsForPrimaryTag( primaryTag ), playPriority );
    }
    
    public void RequestPlaylistForSecondaryTag( BiomeDictionary.Type secondaryTag, PlayPriority playPriority )
    {
        if( songDB != null )
            RequestPlaylist( songDB.getSongsForSecondaryTag( secondaryTag ), playPriority );
    }
    
    // Private Methods
    private void BeginPlaylist( String[] songPlaylist, PlayPriority playPriority, boolean shouldPlayNextImmediately )
    {
        ArrayList<String> newPlaylist = new ArrayList<String>();
        
        for( String song : songPlaylist )
        {
            AmbienceRemixed.getLogger().debug( "SongDJ.BeginPlaylist() - Adding song to playlist : " + song );
            newPlaylist.add( song );
        }
        
        BeginPlaylist( newPlaylist, playPriority, shouldPlayNextImmediately );
    }
    
    private void BeginPlaylist( ArrayList<String> songPlaylist, PlayPriority playPriority, boolean shouldPlayNextImmediately )
    {
        activePlaylist = new Pair<PlayPriority, ArrayList<String>>( playPriority, songPlaylist );
        
        if( jukebox != null )
        {
            jukebox.setPlaylist( songPlaylist.toArray( new String[0] ) );
            
            if( shouldPlayNextImmediately )
                jukebox.playNextSong();
        }
    }
    
    private void MergePlaylistIntoActive( String[] songPlaylist )
    {
        ArrayList<String> mergedList = ( activePlaylist != null ? new ArrayList<String>( activePlaylist.getSecond() ) : new ArrayList<String>() );
        
        for( String song : songPlaylist )
        {
            if( !mergedList.contains( song ) )
                mergedList.add( song );
        }
        
        BeginPlaylist( mergedList, activePlaylist.getFirst(), false );
    }
}
