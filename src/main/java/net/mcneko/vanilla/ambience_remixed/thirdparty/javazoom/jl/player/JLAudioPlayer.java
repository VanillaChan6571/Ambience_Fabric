package net.mcneko.vanilla.ambience_remixed.thirdparty.javazoom.jl.player;

// Java
import java.io.InputStream;

import net.mcneko.vanilla.ambience_remixed.audio.AudioPlayer;
import net.mcneko.vanilla.ambience_remixed.audio.IAudioPlaybackListener;
import net.mcneko.vanilla.ambience_remixed.thirdparty.javazoom.jl.decoder.JavaLayerException;
import net.mcneko.vanilla.ambience_remixed.thirdparty.javazoom.jl.player.advanced.AdvancedPlayer;
import net.mcneko.vanilla.ambience_remixed.thirdparty.javazoom.jl.player.advanced.PlaybackEvent;
import net.mcneko.vanilla.ambience_remixed.thirdparty.javazoom.jl.player.advanced.PlaybackListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// HeavensSword

import net.minecraft.util.math.MathHelper;

public final class JLAudioPlayer extends AudioPlayer
{
    // Private Fields
    private AdvancedPlayer player;
    private InputStream currentInputStream;
    private JLayerPlaybackListener playbackListener = new JLayerPlaybackListener( this );
    
    private volatile float internalGain = 0.0f;

    private int pausedFrame = 0;
    
    private static final Logger LOGGER = LogManager.getLogger();

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
                
                //LOGGER.debug( "internalGain = " + internalGain );
                setGain( internalGain );

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
        
        isPaused = isPlaying = false;
    }

    @Override
    public void play()
    {
        //LOGGER.debug( "JLAudioPlayer.play() - Begin Play." );

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

                //LOGGER.debug( "JLAudioPlayer.play() - Gain = " + getGain() );
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
    public void setVolume( float volume )
    {
        volume = MathHelper.clamp( volume, getMinVolume(), getMaxVolume() );
        
        setGain( MathHelper.lerp( volume, getMinGain(), getMaxGain() ) );
    }
    
    @Override
    public float getVolume()
    {
        float curGain = getGain();
        
        float width = getMaxGain() - getMinGain();
        float relativeGain = Math.abs( curGain - getMinGain() );
        
        return ( width != 0.0f ? relativeGain / Math.abs( width ) : 0.0f );
    }

    @Override
    public void setGain( float gain )
    {
        gain = MathHelper.clamp( gain, getMinGain(), getMaxGain() );
        
        if( player != null )
        {
            player.setInternalGain( gain );
            
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
                finally
                {
                    internalGain = gain;
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
            /*else if( device == null )
                LOGGER.debug( "Audio device was null!");
            else if( !(device instanceof JavaSoundAudioDevice ) )
                LOGGER.debug( "Audio device is NOT JavaSoundAudioDevice!");*/
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
        LOGGER.debug( "JLAudioPlayer.onPlaybackStarted() - PlaybackStarted." );
        isPlaying = true;
        isPaused = false;
        
        for( IAudioPlaybackListener audioPlaybackListener : audioPlaybackListeners )
            audioPlaybackListener.onPlaybackStarted();
    }
    
    public void onPlaybackFinished( JLayerPlaybackListener.JLAudioPlayerFriend friendClassHandshake, PlaybackEvent playbackEvent )
    {
        LOGGER.debug( "JLAudioPlayer.onPlaybackFinished() - PlaybackFinished." );
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
