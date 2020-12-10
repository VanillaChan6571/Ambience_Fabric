package com.heavenssword.ambience_remixed.events;

// Minecraft
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;

// MinecraftForge
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Ambience Remixed
import com.heavenssword.ambience_remixed.PlayPriority;
import com.heavenssword.ambience_remixed.SongEvents;
import com.heavenssword.ambience_remixed.playlist.EventPlaylistRequestBuilder;
import com.heavenssword.ambience_remixed.playlist.IPlaylistStillValidCallback;

public class AmbienceRemixedGuiOpenHandler extends AmbienceRemixedEventHandler
{
    // Private Fields
    private MainMenuStillValid mainMenuStillValidCallback = new MainMenuStillValid();
    private CreditsStillValid creditsStillValidCallback = new CreditsStillValid();
    private DeathScreenStillValid deathScreenStillValidCallback = new DeathScreenStillValid();
    
    @SubscribeEvent
    public void onGuiOpen( GuiOpenEvent guiOpenEvent )
    {
        //AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Begin." );
        
        if( guiOpenEvent == null || songDJ == null )
            return;
        
        Screen currentScreen = guiOpenEvent.getGui();
        
        if( currentScreen instanceof MainMenuScreen || currentScreen instanceof MultiplayerScreen )
        {
            //AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Screen is of type MainMenuScreen." );
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().canBeOverriden( true )
                                                                             .playlistStillValidCallback( mainMenuStillValidCallback )
                                                                             .playPriority( PlayPriority.HIGHEST )
                                                                             .fadeTime( 0.5 )
                                                                             .buildEventPlayRequest( SongEvents.MAIN_MENU ) );
        }
        else if( currentScreen instanceof WinGameScreen )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().canBeOverriden( true )
                                                                             .playlistStillValidCallback( creditsStillValidCallback )
                                                                             .playPriority( PlayPriority.HIGHEST )
                                                                             .fadeTime( 0.5 )
                                                                             .buildEventPlayRequest( SongEvents.CREDITS ) );
        }
        else if( currentScreen instanceof DeathScreen )
        {
            songDJ.requestPlaylistForEvent( new EventPlaylistRequestBuilder().canBeOverriden( true )
                                                                             .playlistStillValidCallback( deathScreenStillValidCallback )
                                                                             .playPriority( PlayPriority.HIGHEST )
                                                                             .fadeTime( 0.05 )
                                                                             .buildEventPlayRequest( SongEvents.DEATH ) );
        }
    }
    
    // Callback Classes
    public final class MainMenuStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {            
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            
            return ( currentScreen != null && ( currentScreen instanceof WinGameScreen || currentScreen instanceof MultiplayerScreen ) );
        }        
    }
    
    public final class CreditsStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            
            return ( currentScreen != null && currentScreen instanceof WinGameScreen );
        }        
    }
    
    public final class DeathScreenStillValid implements IPlaylistStillValidCallback
    {
        @SuppressWarnings( "resource" )
        @Override
        public boolean isPlaylistStillValid()
        {
            Screen currentScreen = Minecraft.getInstance().currentScreen;
            
            return ( currentScreen != null && currentScreen instanceof DeathScreen );
        }        
    }
}
