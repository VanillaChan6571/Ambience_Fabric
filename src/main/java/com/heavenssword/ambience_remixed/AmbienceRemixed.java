package com.heavenssword.ambience_remixed;

// Java
import java.io.File;

// Log4J
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Minecraft
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.SoundCategory;

// MinecraftForge
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// Ambience Remixed
import com.heavenssword.ambience_remixed.audio.JukeboxRunnable;
import com.heavenssword.ambience_remixed.events.AmbienceRemixedChangedDimensionHandler;
import com.heavenssword.ambience_remixed.events.AmbienceRemixedEventHub;
import com.heavenssword.ambience_remixed.events.AmbienceRemixedGuiOpenHandler;
import com.heavenssword.ambience_remixed.events.AmbienceRemixedPlayerHandler;
import com.heavenssword.ambience_remixed.events.IAmbienceRemixedEventHandler;
import com.heavenssword.ambience_remixed.audio.IAudioPlayer;
import com.heavenssword.ambience_remixed.thirdparty.javazoom.jl.player.JLAudioPlayer;

@Mod( "ambience_remixed" )
public class AmbienceRemixed
{
    // Public Constants
    public static final String MOD_ID = "ambience_remixed";
    public static final String MOD_NAME = MOD_ID;
    public static final String BUILD = "GRADLE:BUILD";
    public static final String VERSION = "GRADLE:VERSION-" + BUILD;
    public static final String DEPENDENCIES = "";

    public static final String[] OBF_MC_MUSIC_TICKER = { "aM", "field_147126_aw", "mcMusicTicker" };
    public static final String[] OBF_MAP_BOSS_INFOS = { "g", "field_184060_g", "mapBossInfos" };
    
    public static final int UNDERGROUND_HEIGHT = 55;
    public static final int DEEP_UNDERGROUND_HEIGHT = 20;
    public static final int HIGH_ALITUDE_HEIGHT = 128;

    public static volatile double DeltaTime = 0.0;
    
    // Private Fields
    private FrameTimer frameTimer = null;
    private AmbienceRemixedEventHub eventHub = null;
    
    private SongDatabase songDB = new SongDatabase();
    
    private JukeboxRunnable jukebox;
    private IAudioPlayer audioPlayer = new JLAudioPlayer();
    
    private SongDJ songDJ = null;
    
    private IAmbienceRemixedEventHandler[] modEventHandlers = new IAmbienceRemixedEventHandler[]
                                                              {
                                                                  new AmbienceRemixedGuiOpenHandler(),
                                                                  new AmbienceRemixedPlayerHandler(),
                                                                  new AmbienceRemixedChangedDimensionHandler()
                                                              };
    
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

        SongLoader.loadConfigIntoDB( ambienceDir, songDB );

        if( SongLoader.getIsEnabled() )
        {
            frameTimer = Minecraft.getInstance().getFrameTimer();
            
            jukebox = new JukeboxRunnable( audioPlayer );
            updateFromGameVolume();
        
            MusicTicker ticker = new NilMusicTicker( mc );
            ObfuscationReflectionHelper.setPrivateValue( Minecraft.class, mc, ticker, OBF_MC_MUSIC_TICKER[1] );
            
            songDJ = new SongDJ( jukebox, songDB );
            
            // Inject the songDJ into each of our Mod Event Handlers.
            for( IAmbienceRemixedEventHandler eventHandler : modEventHandlers )
                eventHandler.setSongDJ( songDJ );
            
            // Register our internal event handlers.
            eventHub = new AmbienceRemixedEventHub( modEventHandlers );
            eventHub.RegisterHandlers( MinecraftForge.EVENT_BUS );
        }
    }
    
    @SubscribeEvent
    public void onTick( ClientTickEvent event )
    {
        if( !SongLoader.getIsEnabled() || event == null )
            return;

        switch( event.phase )
        {
            case START:
                updateFromGameVolume();
            break;
            case END:
            {
                if( songDJ != null )
                    songDJ.tick( DeltaTime );
                
                DeltaTime = (double)frameTimer.getFrames()[ frameTimer.getIndex() ] * 0.000000001;// Convert from ns to s
            }
            break;
        }
    }

    @SuppressWarnings( "resource" )
    @SubscribeEvent
    public void onRenderOverlay( RenderGameOverlayEvent.Text event )
    {
        if( !SongLoader.getIsEnabled() || !Minecraft.getInstance().gameSettings.showDebugInfo || jukebox == null )
            return;

        event.getRight().add( null );
        
        String currentSongName = jukebox.getCurrentSongName();
        if( currentSongName != null )
        {
            String name = "Now Playing: " + currentSongName;
            event.getRight().add( name );
        }
        
        String nextSongName = jukebox.getNextSongName();
        if( nextSongName != null )
        {
            String name = "Next Song: " + nextSongName;
            event.getRight().add( name );
        }
    }

    @SubscribeEvent
    public void onBackgroundMusic( PlaySoundEvent event )
    {
        // We want to override the game's default music choices.
        if( SongLoader.getIsEnabled() && event.getSound().getCategory().equals( SoundCategory.MUSIC ) )
        {
            if( event.isCancelable() )
                event.setCanceled( true );

            event.setResultSound( null );
        }
    }
    
    @Override
    protected void finalize()
    {
        if( jukebox != null )
        {
            jukebox.forceKill();
            jukebox.cleanup();
        }
    }
    
    // Private Fields
    @SuppressWarnings( "resource" )
    private void updateFromGameVolume()
    {
        if( jukebox == null )
            return;
        
        GameSettings settings = Minecraft.getInstance().gameSettings;
        
        jukebox.setVolume( settings.getSoundLevel( SoundCategory.MUSIC ) * settings.getSoundLevel( SoundCategory.MASTER ) );
    }
}
