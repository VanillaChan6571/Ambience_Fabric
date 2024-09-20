package net.mcneko.vanilla.ambience_remixed.audio;

// Java
import java.io.InputStream;
import java.util.ArrayList;

public abstract class AudioPlayer implements IAudioPlayer
{
    // Protected Fields
    protected ArrayList<IAudioPlaybackListener> audioPlaybackListeners = new ArrayList<IAudioPlaybackListener>();
    
    protected boolean isPlaying = false;
    protected boolean isPaused = false;
    
    // Public IAudioPlayer Methods
    @Override
    public void registerAudioPlaybackListener( IAudioPlaybackListener audioPlaybackListener )
    {
        if( !audioPlaybackListeners.contains( audioPlaybackListener ) )
            audioPlaybackListeners.add( audioPlaybackListener );
    }
    
    @Override
    public void unregisterAudioPlaybackListener( IAudioPlaybackListener audioPlaybackListener )
    {
        if( audioPlaybackListeners.contains( audioPlaybackListener ) )
            audioPlaybackListeners.remove( audioPlaybackListener );
    }
    
    @Override
    public void clearAudioPlaybackListeners()
    {
        audioPlaybackListeners.clear();
    }
    
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
    public abstract void setVolume( float volume );
    @Override
    public abstract float getVolume();
    
    @Override
    public float getMinVolume()
    {
        return 0.0f;
    }
    
    @Override
    public float getMaxVolume()
    {
        return 1.0f;
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
