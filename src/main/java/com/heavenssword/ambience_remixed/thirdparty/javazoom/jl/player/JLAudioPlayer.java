package com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player;

// Java
import java.io.InputStream;

import com.heavenssword.ambience_remixed.AmbienceRemixed;
// HeavensSword
import com.heavenssword.ambience_remixed.audio.AudioPlayer;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.decoder.JavaLayerException;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.advanced.AdvancedPlayer;

public final class JLAudioPlayer extends AudioPlayer
{
    // Private Fields
    AdvancedPlayer player;
    InputStream currentInputStream;

    int pausedFrame = 0;

    // Public AudioPlayer Methods
    @Override
    public void setStream( InputStream inputStream )
    {
        if( inputStream == null )
            return;

        try
        {
            if( currentInputStream == null || currentInputStream != inputStream )
            {
                if( player != null )
                {
                    player.close();
                    player.openStream( inputStream );
                }
                else
                    player = new AdvancedPlayer( inputStream );

                currentInputStream = inputStream;

                isPaused = isPlaying = false;
            }
        }
        catch( JavaLayerException e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public void clearStream()
    {
        currentInputStream = null;

        stop();
    }

    @Override
    public void play()
    {
        AmbienceRemixed.getLogger().debug( "JLAudioPlayer.play() - Begin." );

        try
        {
            if( player != null )
            {
                if( isPaused )
                {
                    player.play( pausedFrame, Integer.MAX_VALUE );
                    isPaused = false;
                }
                else
                    player.play();

                isPlaying = true;
            }
        }
        catch( JavaLayerException e )
        {
            isPaused = isPlaying = false;

            e.printStackTrace();
        }
    }

    @Override
    public void pause()
    {
        if( player != null )
        {
            pausedFrame = player.getFrames();
            player.stop();

            isPaused = true;
            isPlaying = false;
        }
        else
        {
            pausedFrame = 0;
            isPaused = isPlaying = false;
        }
    }

    @Override
    public void stop()
    {
        if( player != null )
            player.stop();

        isPaused = isPlaying = false;
    }

    @Override
    public void setGain( float gain )
    {
        if( player != null )
        {
            AudioDevice device = player.getAudioDevice();
            if( device != null && device instanceof JavaSoundAudioDevice )
            {
                try
                {
                    ( (JavaSoundAudioDevice)device ).setGain( gain );
                }
                catch( IllegalArgumentException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public float getGain()
    {
        if( player != null )
        {
            AudioDevice device = player.getAudioDevice();
            if( device != null && device instanceof JavaSoundAudioDevice )
                return ( (JavaSoundAudioDevice)device ).getGain();
        }

        return 0;
    }

    @Override
    public float getMinGain()
    {
        return -50.0f;
    }

    @Override
    public float getMaxGain()
    {
        return 0.0f;
    }

    @Override
    public int getCurrentFrame()
    {
        if( player != null )
            return player.getFrames();

        return 0;
    }

    @Override
    public void seekToFrame( int frame )
    {
        try
        {
            if( player != null )
                player.play( frame, Integer.MAX_VALUE );
        }
        catch( JavaLayerException e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getTotalFrames()
    {
        // JLayer doesn't know the total frames...
        return 0;
    }

    @Override
    public void cleanup()
    {
        pausedFrame = 0;
        isPaused = isPlaying = false;

        currentInputStream = null;
        if( player != null )
            player.close();
        player = null;
    }
}
