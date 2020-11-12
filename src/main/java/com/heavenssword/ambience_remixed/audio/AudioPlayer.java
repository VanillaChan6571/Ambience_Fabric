package com.heavenssword.ambience_remixed.audio;

// Java
import java.io.InputStream;

public abstract class AudioPlayer implements IAudioPlayer
{
    // Protected Fields
    protected boolean isPlaying = false;
    protected boolean isPaused = false;
    
    // Public IAudioPlayer Methods
    @Override
    public abstract void setStream( InputStream inputStream );

    @Override
    public abstract void clearStream();

    @Override
    public abstract void play();

    @Override
    public abstract void pause();

    @Override
    public abstract void stop();
    
    @Override
    public boolean isPlaying()
    {
        return isPlaying;
    }
    
    @Override
    public boolean isPaused()
    {
        return isPaused;
    }

    @Override
    public abstract void setGain( float gain );

    @Override
    public abstract float getGain();
    
    @Override
    public abstract float getMinGain();
    
    @Override
    public abstract float getMaxGain();

    @Override
    public abstract int getCurrentFrame();

    @Override
    public abstract void seekToFrame( int frame );

    @Override
    public abstract int getTotalFrames();

    @Override
    public abstract void cleanup();
}
