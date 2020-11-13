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
import com.heavenssword.ambience_remixed.AmbienceRemixed;
import com.heavenssword.ambience_remixed.EventPlaylistRequest;
import com.heavenssword.ambience_remixed.IPlaylistStillValidCallback;

public class AmbienceRemixedChangedDimensionHandler extends AmbienceRemixedEventHandler
{
    // Private Fields
    private RegistryKey<World> currentWorldLoaded = null;
    
    // Public Methods
    @SubscribeEvent
    public void onChangedDimensionHandler( PlayerEvent.PlayerChangedDimensionEvent changedDimensionEvent )
    {
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedChangedDimensionHandler.OnChangedDimensionHandler() - Begin." );
        
        if( changedDimensionEvent == null || songDJ == null )
            return;
        
        currentWorldLoaded = changedDimensionEvent.getTo();
        
        AmbienceRemixed.getLogger().debug( "AmbienceRemixedChangedDimensionHandler.OnChangedDimensionHandler() - newDimension = " + currentWorldLoaded.toString() );
        if( currentWorldLoaded.equals( World.THE_NETHER ) )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.IN_THE_NETHER, PlayPriority.HIGH, true, new TheNetherStillValid() ) );
        else if( currentWorldLoaded.equals( World.THE_END ) )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.IN_THE_END, PlayPriority.HIGH, true, new TheEndStillValid() ) );
        else if( currentWorldLoaded.equals( World.OVERWORLD ) )
            songDJ.requestPlaylistForEvent( new EventPlaylistRequest( SongEvents.GENERIC, PlayPriority.LOWEST ) );
    }
    
    // Callback Classes
    public final class TheNetherStillValid implements IPlaylistStillValidCallback
    {
        @Override
        public boolean isPlaylistStillValid()
        {            
            AmbienceRemixed.getLogger().debug( "TheNetherStillValid.isPlaylistStillValid() - dimensionKey = " + ( currentWorldLoaded != null ? currentWorldLoaded.toString() : "NULL" ) );
            
            return ( currentWorldLoaded != null && currentWorldLoaded.equals( World.THE_NETHER ) );
        }        
    }
    
    public final class TheEndStillValid implements IPlaylistStillValidCallback
    {
        @Override
        public boolean isPlaylistStillValid()
        {             
            return ( currentWorldLoaded != null && currentWorldLoaded.equals( World.THE_END ) );
        }        
    }
}
