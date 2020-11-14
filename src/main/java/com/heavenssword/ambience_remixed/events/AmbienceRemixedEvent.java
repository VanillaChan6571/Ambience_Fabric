package com.heavenssword.ambience_remixed.events;

// Minecraft
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// MinecraftForge
import net.minecraftforge.eventbus.api.Event;

// Used to add custom events in Ambience Remixed; can be used in other mods for extension or compatibility.
public class AmbienceRemixedEvent extends Event
{
    // Private Fields
    
    // The name of a custom event with an associated playlist.
    private String eventName = "";

    private World world;
    private BlockPos position;

    // Construction
    AmbienceRemixedEvent( World _world, BlockPos _position )
    {
        world = _world;
        position = _position;
    }
    
    // Public Accessors/Mutators
    public String getEventName()
    {
        return eventName;
    }
    
    public void setEventName( String _eventName )
    {
        eventName = _eventName;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public void setWorld( World _world )
    {
        world = _world;
    }
    
    public BlockPos getPosition()
    {
        return position;
    }
    
    public void setPosition( BlockPos _position )
    {
        position = _position;
    }
    
    // 
    public static class PreEventCheck extends AmbienceRemixedEvent
    {
        public PreEventCheck( World world, BlockPos position )
        {
            super( world, position );
        }
    }

    public static class PostEventCheck extends AmbienceRemixedEvent
    {
        public PostEventCheck( World world, BlockPos position )
        {
            super( world, position );
        }
    }
}
