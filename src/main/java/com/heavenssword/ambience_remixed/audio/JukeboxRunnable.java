package com.heavenssword.ambience_remixed.audio;

// Java
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

// Ambience Remixed
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.SongLoader;

public class JukeboxRunnable implements Runnable, IAudioPlaybackListener
{
    // Private Volitiled Fields
    private volatile String currentSong = null;
    private volatile ArrayList<String> currentPlaylist = new ArrayList<String>();
    
    private AtomicBoolean isQueued = new AtomicBoolean( false );
    private AtomicBoolean shouldKill = new AtomicBoolean( false );
    private AtomicBoolean isFading = new AtomicBoolean( false );
    private AtomicBoolean isPlaylistShuffleEnabled = new AtomicBoolean( true );
    private AtomicBoolean isPlaylistLoopingEnabled = new AtomicBoolean( true );
    
    private volatile float currentFadeLerp = 1.0f;
    
    private volatile int currentSongIdx = -1;
    private volatile int previousSongIdx = -1;
    
    // Private Fields
    private IAudioPlayer audioPlayer;    
    private Thread musicThread = null;
    
    private Random rand = new Random();
    
    private final float fadeTime = 2.0f;
    
    // Construction
    public JukeboxRunnable( IAudioPlayer _audioPlayer )
    {
        musicThread = new Thread( this );
        
        musicThread.setDaemon( true );
        musicThread.setName( "Ambience Remixed Jukebox Thread" );
               
        audioPlayer = _audioPlayer;
        audioPlayer.registerAudioPlaybackListener( this );
        
        musicThread.start();
    }
    
    // Public Methods
    public synchronized boolean getIsPlaylistShuffleEnabled()
    {
        return isPlaylistShuffleEnabled.get();
    }
    
    public synchronized void setIsPlaylistShuffleEnabled( boolean isShuffleEnabled )
    {
        isPlaylistShuffleEnabled.set( isShuffleEnabled );
    }
    
    public synchronized boolean getIsPlaylistLoopingEnabled()
    {
        return isPlaylistLoopingEnabled.get();
    }
    
    public synchronized void setIsPlaylistLoopingEnabled( boolean isLoopingEnabled )
    {
        isPlaylistLoopingEnabled.set( isLoopingEnabled );
    }
    
    public synchronized void registerAudioPlaybackListener( IAudioPlaybackListener audioPlaybackListener )
    {
        audioPlayer.registerAudioPlaybackListener( audioPlaybackListener );
    }
    
    public synchronized void unregisterAudioPlaybackListener( IAudioPlaybackListener audioPlaybackListener )
    {
        audioPlayer.unregisterAudioPlaybackListener( audioPlaybackListener );
    }
    
    public synchronized String getCurrentSongName()
    {
        return currentSong;
    }
    
    public synchronized String getNextSongName()
    {
        int nextSongIdx = currentSongIdx + 1;
        
        if( currentPlaylist.isEmpty() || nextSongIdx < 0 || nextSongIdx >= currentPlaylist.size() )
            return "";
        
        return currentPlaylist.get( nextSongIdx );
    }
    
    public synchronized String getPreviousSongName()
    {
        if( currentPlaylist.isEmpty() || previousSongIdx < 0 || previousSongIdx >= currentPlaylist.size() )
            return "";
        
        return currentPlaylist.get( previousSongIdx );
    }

    @Override
    public void run()
    {
        Minecraft mc = Minecraft.getInstance();

        try
        {
            while( !shouldKill.get() )
            {                
                if( isQueued.compareAndSet( true, false ) && currentSong != null )
                {
                    AmbienceRemixed.getLogger().debug( "JukeboxRunnable.Run() - ClearingCurrentStream" );
                    clearCurrentStream();
                    
                    InputStream stream = SongLoader.loadSongStream( currentSong );
                    AmbienceRemixed.getLogger().debug( "JukeboxRunnable.Run() - LoadedStream" );
                    if( stream == null )
                        continue;

                    AmbienceRemixed.getLogger().debug( "JukeboxRunnable.Run() - Setting and playing Stream." );
                    audioPlayer.setStream( stream );
                    audioPlayer.play();
                }                
                
                if( isFading.get() )
                {
                    currentFadeLerp = MathHelper.lerp( currentFadeLerp + ( mc.getTickLength() / fadeTime ), 0.0f, 1.0f );
                    AmbienceRemixed.getLogger().debug( "JukeboxRunnable.Run() - CurrentFadeLerp = " + currentFadeLerp );
                    
                    if( currentFadeLerp >= 1.0f )
                    {
                        currentFadeLerp = 1.0f;
                        isFading.set( false );
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onPlaybackStarted()
    {        
    }
    
    @Override
    public void onPlaybackFinished()
    {
        if( getGain() > audioPlayer.getMinGain() )
            playNextSong();
    }
    
    public synchronized void setPlaylist( String[] playlist )
    {
        clearPlaylist();
        
        if( isPlaylistShuffleEnabled.get() )
        {
            ArrayList<String> shuffledList = new ArrayList<String>();
            for( String song : playlist )
                shuffledList.add( song );
         
            while( shuffledList.size() > 0 )
            {
                int randIdx = rand.nextInt( shuffledList.size() );
                
                currentPlaylist.add( shuffledList.get( randIdx ) );
                shuffledList.remove( randIdx );
            }
        }
        else
        {
            for( String song : playlist )
                currentPlaylist.add( song );
        }        
    }
    
    public synchronized void addSongToPlaylist( String songName )
    {
        if( !currentPlaylist.contains( songName ) )
            currentPlaylist.add( songName );
    }
    
    public synchronized void removeSongFromPlaylist( String songName )
    {
        if( currentPlaylist.contains( songName ) )
            currentPlaylist.remove( songName );
    }
    
    public synchronized void clearPlaylist()
    {
        currentPlaylist.clear();
        
        currentSongIdx = previousSongIdx = -1;
    }

    public  void playNextSong()
    {        
        if( currentPlaylist.isEmpty() )
        {
            if( currentSong == null || currentSong.equals( "" ) )
                resetPlayer();
            else
                play( currentSong );
        }
        else
        {
            previousSongIdx = currentSongIdx;
            ++currentSongIdx;
            
            if( currentSongIdx >= currentPlaylist.size() )
            {
                currentSongIdx = 0;
                
                // We've played the last song in the playlist, do we loop?
                AmbienceRemixed.getLogger().debug( "JukeboxRunnable.playNextSong() - ShouldLoop = " + ( isPlaylistLoopingEnabled.get() ? "TRUE" : "FALSE" ) );
                
                if( !isPlaylistLoopingEnabled.get() )
                    return;
            }
            
            AmbienceRemixed.getLogger().debug( "JukeboxRunnable.playNextSong() - currentSongIdx = " + currentSongIdx );
            
            play( currentPlaylist.get( currentSongIdx ) );
        }
    }
    
    public void playPreviousSong()
    {
        if( currentPlaylist.isEmpty() )
        {
            if( currentSong == null || currentSong.equals( "" ) )
                resetPlayer();
            else
                play( currentSong );
        }
        else
        {
            previousSongIdx = currentSongIdx;
            --currentSongIdx;
            
            if( currentSongIdx < 0 )
                currentSongIdx = currentPlaylist.size() - 1;
            
            play( currentPlaylist.get( currentSongIdx ) );
        }
    }

    public synchronized void resetPlayer()
    {
        if( audioPlayer != null )
            audioPlayer.clearStream();

        currentSong = null;
    }
    
    public synchronized void cleanup()
    {
        if( audioPlayer != null )
            audioPlayer.cleanup();
        audioPlayer = null;        
        
        currentSong = null;
        currentPlaylist.clear();
    }
    
    public synchronized boolean isPlaying()
    {
        return ( audioPlayer != null ? audioPlayer.isPlaying() : false );
    }
    
    public synchronized boolean isPaused()
    {
        return ( audioPlayer != null ? audioPlayer.isPaused() : false );
    }

    public synchronized void play( String song )
    {
        AmbienceRemixed.getLogger().debug( "JukeboxRunnable.play() - Attempted to play song : \"" + ( song == null ? "NULL" : song ) + "\"" );
        AmbienceRemixed.getLogger().debug( "JukeboxRunnable.play() - CurrentSong = \"" + ( currentSong == null ? "NULL" : currentSong ) + "\"" );
        
        if( audioPlayer == null || ( audioPlayer.isPlaying() && ( currentSong != null && currentSong.equals( song ) ) ) )
            return;
        
        resetPlayer();

        currentSong = song;
        isQueued.set( true );
        
        AmbienceRemixed.getLogger().debug( "JukeboxRunnable.play() - Song : \"" + ( song == null ? "NULL" : song ) + "\" has been queued." );
        
        //isFading = true;
        //currentFadeLerp = 0.0f;
    }

    public synchronized float getGain()
    {        
        return ( audioPlayer != null ? audioPlayer.getGain() : 0.0f );
    }

    public synchronized void addGain( float gain )
    {
        setGain( getGain() + gain );
    }

    public synchronized void setGain( float gain )
    {
        if( audioPlayer == null )
            return;

        audioPlayer.setGain( MathHelper.clamp( gain, audioPlayer.getMinGain(), audioPlayer.getMaxGain() ) );
        
        // If the game volume was turned all the way down, just stop the player to save resources.
        if( getRelativeVolume() <= 0.0f )
            resetPlayer();
    }

    public synchronized float getRelativeVolume()
    {
        return getRelativeVolume( getGain() );
    }

    public synchronized float getRelativeVolume( float gain )
    {
        float width = audioPlayer.getMaxGain() - audioPlayer.getMinGain();
        float rel = Math.abs( gain - audioPlayer.getMinGain() );
        
        return ( width != 0.0f ? rel / Math.abs( width ) : 0.0f );
    }
    
    public synchronized void forceKill()
    {
        try
        {
            cleanup();
            musicThread.interrupt();

            shouldKill.set( true );
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }
    
    // Private Methods    
    private void clearCurrentStream()
    {
        if( audioPlayer != null )
            audioPlayer.clearStream();
    }
}
