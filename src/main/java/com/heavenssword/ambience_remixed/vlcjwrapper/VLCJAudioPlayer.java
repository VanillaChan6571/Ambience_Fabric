package com.heavenssword.ambience_remixed.vlcjwrapper;

// Java
import java.io.InputStream;

// Log4j
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// VLCJ
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;

// Ambience Remixed
import com.heavenssword.ambience_remixed.audio.AudioPlayer;

public final class VLCJAudioPlayer extends AudioPlayer
{
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    @Override
    public void setStream( InputStream inputStream )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearStream()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void play()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pause()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setVolume( float volume )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public float getVolume()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setGain( float gain )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public float getGain()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getMinGain()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getMaxGain()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCurrentFrame()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void seekToFrame( int frame )
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getTotalFrames()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void cleanup()
    {
        // TODO Auto-generated method stub
        
    }
}
