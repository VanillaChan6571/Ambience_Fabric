package com.heavenssword.ambience_remixed.audio;

// Java
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

// Minecraft
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

// Ambience Remixed
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.SongLoader;

public class JukeboxRunnable implements Runnable
{
    // Private Volitiled Fields
    private volatile String currentSong = null;
    private volatile ArrayList<String> currentPlaylist = new ArrayList<String>();
    
    private volatile boolean isQueued = false;
    private volatile boolean shouldKill = false;

    // Private Fields
    private IAudioPlayer audioPlayer;
    
    private Thread musicThread = null;
    
    private Random rand = new Random();
    
    private boolean isPlaylistShuffleEnabled = true;
    
    private int currentSongIdx = 0;
    private int previousSongIdx = 0;
    
    private boolean isFading = false;
    private float currentFadeLerp = 1.0f;
    private final float fadeTime = 2.0f; 
    
    // Construction
    public JukeboxRunnable( IAudioPlayer _audioPlayer )
    {
        musicThread = new Thread( this );
        
        musicThread.setDaemon( true );
        musicThread.setName( "Ambience Remixed Jukebox Thread" );
               
        audioPlayer = _audioPlayer;
        
        musicThread.start();
    }
    
    // Public Methods
    public boolean getIsPlaylistShuffleEnabled()
    {
        return isPlaylistShuffleEnabled;
    }
    
    public void setIsPlaylistShuffleEnabled( boolean isShuffleEnabled )
    {
        isPlaylistShuffleEnabled = isShuffleEnabled;
    }
    
    public String getCurrentSongName()
    {
        return currentSong;
    }
    
    public String getNextSongName()
    {
        int nextSongIdx = currentSongIdx + 1;
        
        if( currentPlaylist.isEmpty() || nextSongIdx < 0 || nextSongIdx >= currentPlaylist.size() )
            return "";
        
        return currentPlaylist.get( nextSongIdx );
    }
    
    public String getPreviousSongName()
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
            while( !shouldKill )
            {
                if( isQueued && currentSong != null )
                {
                    clearCurrentStream();
                    
                    InputStream stream = SongLoader.loadSongStream( currentSong );
                    if( stream == null )
                        continue;

                    audioPlayer.setStream( stream );
                    audioPlayer.play();
                    
                    isQueued = false;
                }
                else if( getGain() > audioPlayer.getMinGain() && ( !audioPlayer.isPlaying() && !audioPlayer.isPaused() ) )
                    playNextSong();
                
                if( isFading )
                {
                    currentFadeLerp = MathHelper.lerp( currentFadeLerp + ( mc.getTickLength() / fadeTime ), 0.0f, 1.0f );
                    AmbienceRemixed.getLogger().debug( "JukeboxRunnable.Run() - CurrentFadeLerp = " + currentFadeLerp );
                    
                    if( currentFadeLerp >= 1.0f )
                    {
                        currentFadeLerp = 1.0f;
                        isFading = false;
                    }
                }
                
                updateFromGameVolume();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    public void setPlaylist( String[] playlist )
    {
        clearPlaylist();
        
        if( isPlaylistShuffleEnabled )
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
    
    public void addSongToPlaylist( String songName )
    {
        if( !currentPlaylist.contains( songName ) )
            currentPlaylist.add( songName );
    }
    
    public void removeSongFromPlaylist( String songName )
    {
        if( currentPlaylist.contains( songName ) )
            currentPlaylist.remove( songName );
    }
    
    public void clearPlaylist()
    {
        currentPlaylist.clear();
        
        currentSongIdx = previousSongIdx = 0;
    }

    public void playNextSong()
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
                currentSongIdx = 0;
            
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

    public void resetPlayer()
    {
        if( audioPlayer != null )
            audioPlayer.clearStream();

        currentSong = null;
    }
    
    public void cleanup()
    {
        if( audioPlayer != null )
            audioPlayer.cleanup();
        audioPlayer = null;        
        
        currentSong = null;
        currentPlaylist.clear();
    }

    public void play( String song )
    {
        AmbienceRemixed.getLogger().debug( "JukeboxRunnable.play() - Attempted to play song : \"" + ( song == null ? "NULL" : song ) + "\"" );
        AmbienceRemixed.getLogger().debug( "JukeboxRunnable.play() - CurrentSong = \"" + ( currentSong == null ? "NULL" : currentSong ) + "\"" );
        
        if( audioPlayer == null || ( audioPlayer.isPlaying() && ( currentSong != null && currentSong.equals( song ) ) ) )
            return;
        
        resetPlayer();

        currentSong = song;
        isQueued = true;
        
        //isFading = true;
        //currentFadeLerp = 0.0f;
    }

    public float getGain()
    {        
        return ( audioPlayer != null ? audioPlayer.getGain() : 0.0f );
    }

    public void addGain( float gain )
    {
        setGain( getGain() + gain );
    }

    public void setGain( float gain )
    {
        if( audioPlayer == null )
            return;

        audioPlayer.setGain( Math.min( audioPlayer.getMaxGain(), Math.max( audioPlayer.getMinGain(), gain ) ) );
    }

    public float getRelativeVolume()
    {
        return getRelativeVolume( getGain() );
    }

    public float getRelativeVolume( float gain )
    {
        float width = audioPlayer.getMaxGain() - audioPlayer.getMinGain();
        float rel = Math.abs( gain - audioPlayer.getMinGain() );
        
        return ( width != 0.0f ? rel / Math.abs( width ) : 0.0f );
    }
    
    public void forceKill()
    {
        try
        {
            resetPlayer();
            musicThread.interrupt();

            shouldKill = true;
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }
    
    // Private Methods
    @SuppressWarnings( "resource" )
    private void updateFromGameVolume()
    {
        if( audioPlayer == null )
            return;
        
        GameSettings settings = Minecraft.getInstance().gameSettings;
        float musicGain = settings.getSoundLevel( SoundCategory.MUSIC ) * settings.getSoundLevel( SoundCategory.MASTER );
        
        float normalizedRealGain = audioPlayer.getMinGain() + ( audioPlayer.getMaxGain() - audioPlayer.getMinGain() ) * musicGain;        
        
        //AmbienceRemixed.getLogger().debug( "JukeboxRunnable.updateFromGameVolume() - musicGain is " + musicGain );
        //AmbienceRemixed.getLogger().debug( "JukeboxRunnable.updateFromGameVolume() - normalizedRealGain is " + normalizedRealGain );
        //AmbienceRemixed.getLogger().debug( "JukeboxRunnable.updateFromGameVolume() - Setting Gain to " + ( normalizedRealGain * currentFadeLerp ) );
        audioPlayer.setGain( normalizedRealGain * currentFadeLerp );
        
        // If the game volume was turned all the way down, just stop the player to save resources.
        if( musicGain <= 0.0f )
            resetPlayer();
    }
    
    private void clearCurrentStream()
    {
        if( audioPlayer != null )
            audioPlayer.clearStream();
    }
}
