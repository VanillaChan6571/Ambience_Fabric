package com.heavenssword.ambience_remixed.events;

// Minecraft
import net.minecraft.client.gui.screen.Screen;

import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.PlayPriority;
import com.heavenssword.ambience_remixed.SongEvents;

import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.WinGameScreen;

// MinecraftForge
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AmbienceRemixedGuiOpenHandler extends AmbienceRemixedEventHandler
{
    @SubscribeEvent
    public void OnGuiOpen( GuiOpenEvent guiOpenEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Begin." );
        
        if( guiOpenEvent == null || songDJ == null )
            return;
        
        Screen currentScreen = guiOpenEvent.getGui();
        
        if( currentScreen instanceof MainMenuScreen || currentScreen instanceof MultiplayerScreen )
        {
            AmbienceRemixed.getLogger().debug( "AmbienceRemixedGuiOpenHandler.OnGuiOpen() - Screen is of type MainMenuScreen." );
            songDJ.RequestPlaylistForEvent( SongEvents.MAIN_MENU, PlayPriority.HIGHEST );
        }
        else if( currentScreen instanceof WinGameScreen )
            songDJ.RequestPlaylistForEvent( SongEvents.CREDITS, PlayPriority.HIGHEST );
        else if( currentScreen instanceof DeathScreen )
            songDJ.RequestPlaylistForEvent( SongEvents.DEATH, PlayPriority.HIGHEST );
    }
}
