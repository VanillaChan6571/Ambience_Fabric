package com.heavenssword.ambience_remixed;

// Java
import java.io.InputStream;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.client.GameSettings;
import net.minecraft.util.SoundCategory;

// JLayer
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.AudioDevice;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.JavaSoundAudioDevice;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.advanced.AdvancedPlayer;

public class MusicPlayerThread extends Thread
{
    // Public Static Const Values
    public static final float MIN_GAIN = -50F;
    public static final float MAX_GAIN = 0F;

    public static float[] fadeGains;

    static
    {
        fadeGains = new float[AmbienceRemixed.FADE_DURATION];
        float totalDiff = MIN_GAIN - MAX_GAIN;
        float averageDiff = totalDiff / fadeGains.length;
        
        for( int i = 0; i < fadeGains.length; ++i )
            fadeGains[i] = MAX_GAIN + averageDiff * i;
    }

    AdvancedPlayer player;
    
    public volatile static float gain = MAX_GAIN;
    public volatile static float realGain = 0;

    public volatile static String currentSong = null;
    public volatile static String currentSongChoices = null;

    public volatile boolean isPlaying = false;
    
    private volatile boolean isQueued = false;
    private volatile boolean shouldKill = false;

    public MusicPlayerThread()
    {
        setDaemon( true );
        setName( "Ambience Remixed Player Thread" );
        
        start();
    }

    @Override
    public void run()
    {
        try
        {
            while( !shouldKill )
            {
                if( isQueued && currentSong != null )
                {
                    if( player != null )
                        resetPlayer();
                    
                    InputStream stream = SongLoader.getStream();
                    if( stream == null )
                        continue;

                    player = new AdvancedPlayer( stream );
                    isQueued = false;
                }

                boolean wasPlayed = false;
                if( player != null && player.getAudioDevice() != null && realGain > MIN_GAIN )
                {
                    setGain( fadeGains[0] );
                    player.play();
                    
                    isPlaying = true;
                    wasPlayed = true;
                }

                if( wasPlayed && !isQueued )
                    next();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public void next()
    {
        if( !currentSongChoices.contains( "," ) )
            play( currentSong );
        else
        {
            if( SongPicker.getSongsString().equals( currentSongChoices ) )
            {
                String newSong;
                do
                {
                    newSong = SongPicker.getRandomSong();
                }
                while( newSong.equals( currentSong ) );
                
                play( newSong );
            }
            else
            {
                play( null );
            }
        }
    }

    public void resetPlayer()
    {
        isPlaying = false;
        if( player != null )
            player.close();

        currentSong = null;
        player = null;
    }

    public void play( String song )
    {
        resetPlayer();

        currentSong = song;
        isQueued = true;
    }

    public float getGain()
    {
        if( player == null )
            return gain;

        AudioDevice device = player.getAudioDevice();
        if( device != null && device instanceof JavaSoundAudioDevice )
            return ((JavaSoundAudioDevice)device).getGain();
        
        return gain;
    }

    public void addGain( float gain )
    {
        setGain( getGain() + gain );
    }

    public void setGain( float gain )
    {
        gain = Math.min( MAX_GAIN, Math.max( MIN_GAIN, gain ) );

        if( player == null )
            return;

        setRealGain();
    }

    @SuppressWarnings( "resource" )
    public void setRealGain()
    {
        GameSettings settings = Minecraft.getInstance().gameSettings;
        float musicGain = settings.getSoundLevel( SoundCategory.MUSIC ) * settings.getSoundLevel( SoundCategory.MASTER );
        
        realGain = MIN_GAIN + ( MAX_GAIN - MIN_GAIN ) * musicGain;
        
        if( player != null )
        {
            AudioDevice device = player.getAudioDevice();
            if( device != null && device instanceof JavaSoundAudioDevice )
            {
                try
                {
                    ((JavaSoundAudioDevice)device).setGain( realGain );
                }
                catch( IllegalArgumentException e )
                {
                } // If you can't fix the bug just put a catch around it ~Vazkii
            }
        }

        if( musicGain == 0 )
            play( null );
    }

    public float getRelativeVolume()
    {
        return getRelativeVolume( getGain() );
    }

    public float getRelativeVolume( float gain )
    {
        float width = MAX_GAIN - MIN_GAIN;
        float rel = Math.abs( gain - MIN_GAIN );
        
        return ( width != 0.0f ? rel / Math.abs( width ) : 0.0f );
    }

    public int getFramesPlayed()
    {
        return player == null ? 0 : player.getFrames();
    }

    public void forceKill()
    {
        try
        {
            resetPlayer();
            interrupt();

            //finalize();
            shouldKill = true;
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }
}
