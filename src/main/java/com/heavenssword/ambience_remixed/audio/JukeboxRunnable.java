package com.heavenssword.ambience_remixed.audio;

// Java
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Minecraft
import net.minecraft.util.math.MathHelper;

// Ambience Remixed
import com.heavenssword.ambience_remixed.SongLoader;

public class JukeboxRunnable implements Runnable, IAudioPlaybackListener, IFadeCompleteListener
{
    // Private Volitiled Fields
    private volatile String currentSong = null;
    private volatile ArrayList<String> currentPlaylist = new ArrayList<String>();
    
    private AtomicBoolean isQueued = new AtomicBoolean( false );
    private AtomicBoolean shouldKill = new AtomicBoolean( false );
    private AtomicBoolean isPlaylistShuffleEnabled = new AtomicBoolean( true );
    private AtomicBoolean isPlaylistLoopingEnabled = new AtomicBoolean( true );
        
    private volatile float currentFadeValue = 1.0f;
    private AtomicDouble currentFadeLerp = new AtomicDouble( 0.0 );
    
    private volatile int currentSongIdx = -1;
    private volatile int previousSongIdx = -1;
    
    // Private Fields
    private static final Logger LOGGER = LogManager.getLogger();
    
    private IAudioPlayer audioPlayer;    
    private Thread musicThread = null;
    private FaderThread fadeThread = null;
    
    private Random rand = new Random();
    
    private final double fadeTime = 1.5f;
    
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
    public boolean getIsPlaylistShuffleEnabled()
    {
        return isPlaylistShuffleEnabled.get();
    }
    
    public void setIsPlaylistShuffleEnabled( boolean isShuffleEnabled )
    {
        isPlaylistShuffleEnabled.set( isShuffleEnabled );
    }
    
    public boolean getIsPlaylistLoopingEnabled()
    {
        return isPlaylistLoopingEnabled.get();
    }
    
    public void setIsPlaylistLoopingEnabled( boolean isLoopingEnabled )
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
        try
        {
            while( !shouldKill.get() )
            {
                if( isQueued.compareAndSet( true, false ) && currentSong != null )
                {
                    clearCurrentStream();
                    
                    InputStream stream = SongLoader.loadSongStream( currentSong );
                    //LOGGER.debug( "JukeboxRunnable.Run() - LoadedStream" );
                    if( stream == null )
                        continue;

                    //LOGGER.debug( "JukeboxRunnable.Run() - Setting and playing Stream." );
                    audioPlayer.setStream( stream );
                    audioPlayer.play();
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
        Double oldThreadFadeTime = null;
        if( fadeThread != null )
        {
            oldThreadFadeTime = fadeThread.getFadeTime();
            fadeThread.forceKill();
        }
        
        // Fade the track in
        fadeThread = new FaderThread( false, ( oldThreadFadeTime == null ? fadeTime : oldThreadFadeTime ) );
        fadeThread.setFadeCompleteListener( this );
        fadeThread.start();
    }
    
    @Override
    public void onPlaybackFinished()
    {
        if( audioPlayer == null )
            return;
        
        if( audioPlayer.getGain() > audioPlayer.getMinGain() )
            playNextSong();
    }
    
    @Override
    public void onFadeComplete( boolean wasFadingOut )
    {
        if( fadeThread != null )
            fadeThread.clearFadeCompleteListener();   
        
        if( wasFadingOut )
        {
            LOGGER.debug( "onFadeComplete() - Fade Out Complete." );
            
            clearCurrentStream();
            
            isQueued.set( true );
        }
        else
        {
            LOGGER.debug( "onFadeComplete() - Fade In Complete." );
            fadeThread = null;
        }
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

    public void playNextSong()
    {
        playNextSong( null );
    }
    
    public void playNextSong( Double fadeOverrideTime )
    {        
        if( currentPlaylist.isEmpty() )
        {
            if( currentSong == null || currentSong.equals( "" ) )
                resetPlayer();
            else
                play( currentSong, fadeOverrideTime );
        }
        else
        {
            previousSongIdx = currentSongIdx;
            ++currentSongIdx;
            
            if( currentSongIdx >= currentPlaylist.size() )
            {
                currentSongIdx = 0;
                
                // We've played the last song in the playlist, do we loop?
                //LOGGER.debug( "JukeboxRunnable.playNextSong() - ShouldLoop = " + ( isPlaylistLoopingEnabled.get() ? "TRUE" : "FALSE" ) );
                
                if( !isPlaylistLoopingEnabled.get() )
                    return;
            }
            
            //LOGGER.debug( "JukeboxRunnable.playNextSong() - currentSongIdx = " + currentSongIdx );
            
            play( currentPlaylist.get( currentSongIdx ), fadeOverrideTime );
        }
    }
    
    public void playPreviousSong()
    {
        playPreviousSong( null );
    }
    
    public void playPreviousSong( Double fadeOverrideTime )
    {
        if( currentPlaylist.isEmpty() )
        {
            if( currentSong == null || currentSong.equals( "" ) )
                resetPlayer();
            else
                play( currentSong, fadeOverrideTime );
        }
        else
        {
            previousSongIdx = currentSongIdx;
            --currentSongIdx;
            
            if( currentSongIdx < 0 )
                currentSongIdx = currentPlaylist.size() - 1;
            
            play( currentPlaylist.get( currentSongIdx ), fadeOverrideTime );
        }
    }

    public synchronized void resetPlayer()
    {
        //LOGGER.debug( "JukeboxRunnable.resetPlayer() - resetPlayer called." );
        if( audioPlayer != null )
            audioPlayer.clearStream();

        currentSong = null;
    }
    
    public synchronized void cleanup()
    {
        //LOGGER.debug( "JukeboxRunnable.cleanup() - Cleanup called." );
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
        play( song, null );
    }
    
    public synchronized void play( String song, Double _fadeOverrideTime )
    {
        //LOGGER.debug( "JukeboxRunnable.play() - Attempted to play song : \"" + ( song == null ? "NULL" : song ) + "\"" );
        //LOGGER.debug( "JukeboxRunnable.play() - CurrentSong = \"" + ( currentSong == null ? "NULL" : currentSong ) + "\"" );
        
        if( audioPlayer == null || ( audioPlayer.isPlaying() && ( currentSong != null && currentSong.equals( song ) ) ) )
            return;
        
        currentSong = song;

        currentFadeValue = 1.0f;
        currentFadeLerp.set( 0.0 );
        
        if( fadeThread != null )
            fadeThread.forceKill();
        
        // Fade the current track out if there's one playing
        if( audioPlayer.isPlaying() )
        {
            fadeThread = new FaderThread( true, ( _fadeOverrideTime == null ? fadeTime : _fadeOverrideTime ) );
            fadeThread.setFadeCompleteListener( this );
            fadeThread.start();
        }
        else
        {
            currentFadeValue = 0.0f;
            onFadeComplete( true );
        }
    }
    
    public synchronized void setVolume( float volume )
    {
        if( audioPlayer == null )
            return;

        audioPlayer.setVolume( volume * currentFadeValue );
        
        // If the game volume was turned all the way down, just stop the player to save resources.
        if( volume <= 0.0f )
            resetPlayer();
    }
    
    public synchronized float getVolume()
    {
        return ( audioPlayer != null ? audioPlayer.getVolume() : 0.0f );
    }
    
    public void forceKill()
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
        //LOGGER.debug( "JukeboxRunnable.clearCurrentStream() - ClearingCurrentStream" );
        if( audioPlayer != null )
            audioPlayer.clearStream();
    }
    
    // Private Thread Classes
    private class FaderThread extends Thread 
    {
        // Private Fields
        private final boolean isFadingOut;
        private final double fadeTime;
        private IFadeCompleteListener fadeCompleteListener = null;
        
        private AtomicBoolean shouldKill = new AtomicBoolean( false );
        
        // Construction
        public FaderThread( boolean _isFadingOut, double _fadeTime )
        {
            isFadingOut = _isFadingOut;
            fadeTime = ( ( _fadeTime <= 0.0f ) ? 0.001 : _fadeTime );
        }
        
        public Double getFadeTime()
        {
            return fadeTime;
        }
        
        public void setFadeCompleteListener( IFadeCompleteListener _fadeCompleteListener )
        {
            fadeCompleteListener = _fadeCompleteListener;
        }
        
        public void clearFadeCompleteListener()
        {
            fadeCompleteListener = null;
        }
        
        public void run() 
        {
            while( !shouldKill.get() )
            {
                if( isFadingOut )// Fading Out
                {       
                    currentFadeValue = (float)MathHelper.clampedLerp( 1.0, 0.0, currentFadeLerp.addAndGet( 0.001 / this.fadeTime ) );
                    if( currentFadeValue <= 0.0f )
                    {
                        currentFadeLerp.set( 0.0 );
                        
                        if( fadeCompleteListener != null )
                            fadeCompleteListener.onFadeComplete( isFadingOut );
                        
                        break;
                    }
                }
                else// Fading In
                {   
                    currentFadeValue = (float)MathHelper.clampedLerp( 0.0, 1.0, currentFadeLerp.addAndGet( 0.001 / this.fadeTime ) );
                    if( currentFadeValue >= 1.0 )
                    {
                        currentFadeLerp.set( 0.0 );
                        
                        if( fadeCompleteListener != null )
                            fadeCompleteListener.onFadeComplete( isFadingOut );
                        
                        break;
                    }
                }
                
                try
                {
                    sleep( 1 );
                }
                catch( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }
        
        public void forceKill()
        {
            try
            {
                this.interrupt();

                shouldKill.set( true );
            }
            catch( Throwable e )
            {
                e.printStackTrace();
            }
        }
    }
}
