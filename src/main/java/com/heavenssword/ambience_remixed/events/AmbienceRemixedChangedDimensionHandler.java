package com.heavenssword.ambience_remixed.events;

// Minecraft
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

// MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

// Ambience Remixed
import com.heavenssword.ambience_remixed.PlayPriority;
import com.heavenssword.ambience_remixed.SongEvents;

public class AmbienceRemixedChangedDimensionHandler extends AmbienceRemixedEventHandler
{
    @SubscribeEvent
    public void OnChangedDimensionHandler( PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent )
    {
        if( changedDimensionEvent == null || songDJ == null )
            return;
        
        RegistryKey<World> newDimension = changedDimensionEvent.getTo();
        if( newDimension.equals( World.THE_NETHER ) )
            songDJ.RequestPlaylistForEvent( SongEvents.IN_THE_NETHER, PlayPriority.HIGH );
        else if( newDimension.equals( World.THE_END ) )
            songDJ.RequestPlaylistForEvent( SongEvents.IN_THE_END, PlayPriority.HIGH );
    }
}
