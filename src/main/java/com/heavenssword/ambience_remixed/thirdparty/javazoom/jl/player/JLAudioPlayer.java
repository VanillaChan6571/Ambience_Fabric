package com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player;

// Java
import java.io.InputStream;

// HeavensSword
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.audio.AudioPlayer;
import com.heavenssword.ambience_remixed.audio.IAudioPlaybackListener;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.decoder.JavaLayerException;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.advanced.AdvancedPlayer;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.advanced.PlaybackEvent;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.advanced.PlaybackListener;

public final class JLAudioPlayer extends AudioPlayer
{
    // Private Fields
    private AdvancedPlayer player;
    private InputStream currentInputStream;
    private JLayerPlaybackListener playbackListener = new JLayerPlaybackListener( this );

    private int pausedFrame = 0;

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
                {
                    player = new AdvancedPlayer( inputStream );
                    player.setPlayBackListener( playbackListener );
                }

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

        if( player != null )
            player.close();
    }

    @Override
    public void play()
    {
        AmbienceRemixed.getLogger().debug( "JLAudioPlayer.play() - Begin Play." );

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
        
        clearAudioPlaybackListeners();

        currentInputStream = null;
        if( player != null )
            player.close();
        player = null;
    }
    
    public void onPlaybackStarted( JLayerPlaybackListener.JLAudioPlayerFriend friendClassHandshake, PlaybackEvent playbackEvent )
    {
        AmbienceRemixed.getLogger().debug( "JLAudioPlayer.onPlaybackFinished() - PlaybackStarted." );
        isPlaying = true;
        isPaused = false;
        
        for( IAudioPlaybackListener audioPlaybackListener : audioPlaybackListeners )
            audioPlaybackListener.onPlaybackStarted();
    }
    
    public void onPlaybackFinished( JLayerPlaybackListener.JLAudioPlayerFriend friendClassHandshake, PlaybackEvent playbackEvent )
    {
        AmbienceRemixed.getLogger().debug( "JLAudioPlayer.onPlaybackFinished() - PlaybackFinished." );
        pausedFrame = 0;
        isPaused = isPlaying = false;
        
        for( IAudioPlaybackListener audioPlaybackListener : audioPlaybackListeners )
            audioPlaybackListener.onPlaybackFinished();
    }
    
    // Friend Class
    private final class JLayerPlaybackListener extends PlaybackListener
    {
        public final class JLAudioPlayerFriend { private JLAudioPlayerFriend() {} }
        
        // Private Fields
        private JLAudioPlayer jlAudioPlayer = null;
        
        // Construction
        public JLayerPlaybackListener( JLAudioPlayer _jlAudioPlayer )
        {
            jlAudioPlayer = _jlAudioPlayer;
        }
        
        // Public Methods
        @Override
        public void playbackStarted( PlaybackEvent playbackEvent )
        {   
            jlAudioPlayer.onPlaybackStarted( new JLAudioPlayerFriend(), playbackEvent );
        }
        
        @Override
        public void playbackFinished( PlaybackEvent playbackEvent ) 
        {
            jlAudioPlayer.onPlaybackFinished( new JLAudioPlayerFriend(), playbackEvent );
        }
    }
}
