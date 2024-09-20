package net.mcneko.vanilla.ambience_remixed.events;

// Java
import java.util.ArrayList;

// MinecraftForge
import net.minecraftforge.eventbus.api.IEventBus;

public class AmbienceRemixedEventHub
{
    // Private Fields
    ArrayList<IAmbienceRemixedEventHandler> eventHandlers = new ArrayList<IAmbienceRemixedEventHandler>();
    
    // Construction
    public AmbienceRemixedEventHub( IAmbienceRemixedEventHandler[] _eventHandlers )
    {
        for( IAmbienceRemixedEventHandler eventHandler : _eventHandlers )
            eventHandlers.add( eventHandler );
    }
    
    // Public Methods
    public void RegisterHandlers( IEventBus eventBus )
    {
        if( eventBus != null )
        {
            for( IAmbienceRemixedEventHandler eventHandler : eventHandlers )
                eventBus.register( eventHandler );
        }
    }
    
    public void UnregisterHandlers( IEventBus eventBus )
    {
        if( eventBus != null )
        {
            for( IAmbienceRemixedEventHandler eventHandler : eventHandlers )
                eventBus.unregister( eventHandler );
        }
    }
}
