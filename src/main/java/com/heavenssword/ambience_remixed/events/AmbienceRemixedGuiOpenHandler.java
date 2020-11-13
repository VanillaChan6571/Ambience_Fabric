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
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.EventPlaylistRequest;
import com.heavenssword.ambience_remixed.IPlaylistStillValidCallback;
import com.heavenssword.ambience_remixed.PlayPriority;
import com.heavenssword.ambience_remixed.SongEvents;

public class AmbienceRemixedGuiOpenHandler extends AmbienceRemixedEventHandler
{
    @SubscribeEvent
    public void onGuiOpen( GuiOpenEvent guiOpenEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Begin." );
        
        if( guiOpenEvent == null || songDJ == null )
            return;
        
        Screen currentScreen = guiOpenEvent.getGui();
        
        if( currentScreen instanceof MainMenuScreen || currentScreen instanceof MultiplayerScreen )
        {
            AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Screen is of type MainMenuScreen." );
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.MAIN_MENU, PlayPriority.HIGHEST, true, new MainMenuStillValid() ) );
        }
        else if( currentScreen instanceof WinGameScreen )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.CREDITS, PlayPriority.HIGHEST, true, new CreditsStillValid() ) );
        else if( currentScreen instanceof DeathScreen )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.DEATH, PlayPriority.HIGHEST, true, new DeathScreenStillValid() ) );
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
