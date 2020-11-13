package com.heavenssword.ambience_remixed.events;

// MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Ambience Remixed
import com.heavenssword.ambience_remixed.SongEvents;
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.EventPlaylistRequest;
import com.heavenssword.ambience_remixed.PlayPriority;

public class AmbienceRemixedPlayerHandler extends AmbienceRemixedEventHandler
{
    @SubscribeEvent
    public void onPlayerSleep( PlayerSleepInBedEvent playerSleepEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerSleep() - Begin." );
        
        if( playerSleepEvent == null || songDJ == null )
            return;
        
        // Make sure the player can actually sleep.
        if( playerSleepEvent.getResultStatus() == null )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.SLEEPING, PlayPriority.HIGHEST ) );
    }
    
    @SubscribeEvent
    public void onPlayerWakeUp( PlayerWakeUpEvent playerWakeUpEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.onPlayerWakeUp() - Begin." );
        
        if( playerWakeUpEvent == null || songDJ == null )
            return;
        
        songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.GENERIC, PlayPriority.LOWEST, true, true ) );
    }
    
    @SubscribeEvent
    public void onPlayerRespawn( PlayerEvent.PlayerRespawnEvent playerRespawnEvent )    
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedPlayerHandler.OnPlayerRespawn() - Begin." );
        
        if( playerRespawnEvent == null || songDJ == null )
            return;
        
        songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.GENERIC, PlayPriority.LOWEST ) );
    }
}
