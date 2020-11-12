package com.heavenssword.ambience_remixed.audio;

// Java
import java.io.InputStream;

public interface IAudioPlayer
{
    public void setStream( InputStream inputStream );
    public void clearStream();
    
    public void play();
    public void pause();
    public void stop();
    
    public boolean isPlaying();
    public boolean isPaused();
    
    public void setGain( float gain );
    public float getGain();
    public float getMinGain();
    public float getMaxGain();
    
    public int getCurrentFrame();
    public void seekToFrame( int frame );
    public int getTotalFrames();
    
    public void cleanup();
}