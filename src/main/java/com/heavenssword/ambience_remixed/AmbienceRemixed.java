package com.heavenssword.ambience_remixed;

// Java
import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.SoundCategory;

// MinecraftForge
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod( "ambience_remixed" )
public class AmbienceRemixed
{
    public static final String MOD_ID = "ambience_remixed";
    public static final String MOD_NAME = MOD_ID;
    public static final String BUILD = "GRADLE:BUILD";
    public static final String VERSION = "GRADLE:VERSION-" + BUILD;
    public static final String DEPENDENCIES = "";

    private static final int WAIT_DURATION = 40;
    public static final int FADE_DURATION = 40;
    public static final int SILENCE_DURATION = 20;

    public static final String[] OBF_MC_MUSIC_TICKER = { "aM", "field_147126_aw", "mcMusicTicker" };
    public static final String[] OBF_MAP_BOSS_INFOS = { "g", "field_184060_g", "mapBossInfos" };

    public static MusicPlayerThread thread;

    String currentSong;
    String nextSong;
    int waitTick = WAIT_DURATION;
    int fadeOutTicks = FADE_DURATION;
    int fadeInTicks = 0;
    int silenceTicks = 0;
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Construction
    public AmbienceRemixed()
    {
        // Register method for modloading.
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::initialize );
        
        // Register our mod class for server and game events.
        MinecraftForge.EVENT_BUS.register( this );
    }
    
    // Public Static Methods
    public static Logger getLogger()
    {
        return LOGGER;
    }
    
    // Public Methods
    public void initialize( final FMLClientSetupEvent event )
    {
        Minecraft mc = Minecraft.getInstance();
        
        File ambienceDir = new File( mc.gameDir, "ambience_music" );
        if( !ambienceDir.exists() )
            ambienceDir.mkdir();

        SongLoader.loadFrom( ambienceDir );

        if( SongLoader.getIsEnabled() )
            thread = new MusicPlayerThread();        
        
        MusicTicker ticker = new NilMusicTicker( mc );
        ObfuscationReflectionHelper.setPrivateValue( Minecraft.class, mc, ticker, OBF_MC_MUSIC_TICKER[1] );
    }

    @SubscribeEvent
    public void onTick( ClientTickEvent event )
    {
        if( thread == null )
            return;

        if( event.phase == Phase.END )
        {
            String songs = SongPicker.getSongsString();
            String song = null;

            if( songs != null )
            {
                if( nextSong == null || !songs.contains( nextSong ) )
                {
                    do
                    {
                        song = SongPicker.getRandomSong();
                    }
                    while( song.equals( currentSong ) && songs.contains( "," ) );
                }
                else
                    song = nextSong;
            }

            if( songs != null && ( !songs.equals( MusicPlayerThread.currentSongChoices ) || ( song == null && MusicPlayerThread.currentSong != null ) || !thread.isPlaying ) )
            {
                if( nextSong != null && nextSong.equals( song ) )
                    --waitTick;

                if( !song.equals( currentSong ) )
                {
                    if( currentSong != null && MusicPlayerThread.currentSong != null && !MusicPlayerThread.currentSong.equals( song ) && songs.equals( MusicPlayerThread.currentSongChoices ) )
                        currentSong = MusicPlayerThread.currentSong;
                    else
                        nextSong = song;
                }
                else if( nextSong != null && !songs.contains( nextSong ) )
                    nextSong = null;

                if( waitTick <= 0 )
                {
                    if( MusicPlayerThread.currentSong == null )
                    {
                        currentSong = nextSong;
                        nextSong = null;
                        MusicPlayerThread.currentSongChoices = songs;
                        changeSongTo( song );
                        fadeOutTicks = 0;
                        waitTick = WAIT_DURATION;
                    }
                    else if( fadeOutTicks < FADE_DURATION )
                    {
                        thread.setGain( MusicPlayerThread.fadeGains[fadeOutTicks] );
                        ++fadeOutTicks;
                        silenceTicks = 0;
                    }
                    else
                    {
                        if( silenceTicks < SILENCE_DURATION )
                        {
                            ++silenceTicks;
                        }
                        else
                        {
                            nextSong = null;
                            MusicPlayerThread.currentSongChoices = songs;
                            changeSongTo( song );
                            fadeOutTicks = 0;
                            waitTick = WAIT_DURATION;
                        }
                    }
                }
            }
            else
            {
                nextSong = null;
                thread.setGain( MusicPlayerThread.fadeGains[0] );
                silenceTicks = 0;
                fadeOutTicks = 0;
                waitTick = WAIT_DURATION;
            }

            if( thread != null )
                thread.setRealGain();
        }
    }

    @SuppressWarnings( "resource" )
    @SubscribeEvent
    public void onRenderOverlay( RenderGameOverlayEvent.Text event )
    {
        if( !Minecraft.getInstance().gameSettings.showDebugInfo )
            return;

        event.getRight().add( null );
        if( MusicPlayerThread.currentSong != null )
        {
            String name = "Now Playing: " + SongPicker.getSongName( MusicPlayerThread.currentSong );
            event.getRight().add( name );
        }
        
        if( nextSong != null )
        {
            String name = "Next Song: " + SongPicker.getSongName( nextSong );
            event.getRight().add( name );
        }
    }

    @SubscribeEvent
    public void onBackgroundMusic( PlaySoundEvent event )
    {
        if( SongLoader.getIsEnabled() && event.getSound().getCategory().equals( SoundCategory.MUSIC ) )
        {
            if( event.isCancelable() )
                event.setCanceled( true );

            event.setResultSound( null );
        }
    }

    public void changeSongTo( String song )
    {
        currentSong = song;
        thread.play( song );
    }
}
