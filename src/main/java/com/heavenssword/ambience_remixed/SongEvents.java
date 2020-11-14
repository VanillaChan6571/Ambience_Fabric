package com.heavenssword.ambience_remixed;

public enum SongEvents
{
    MAIN_MENU,
    CREDITS,
    
    BOSS,
    BOSS_WITHER,
    BOSS_DRAGON,
    
    GENERIC,
    IN_THE_NETHER,
    IN_THE_END,
    
    BATTLE,
    BATTLE_HORDE,
    
    NIGHT,
    RAIN,
    UNDERWATER,
    UNDERGROUND,
    DEEP_UNDERGROUND,
    HIGH_ALTITUDE,
    
    VILLAGE,
    VILLAGE_NIGHT,
    
    RIDING_MINECART,
    RIDING_BOAT,
    RIDING_HORSE,
    RIDING_PIG,
    
    SLEEPING,
    FISHING,
    
    DYING,
    DEATH,
    
    WEARING_PUMPKIN_HEAD;
    
    // Public Methods
    public static boolean hasValue( String value )
    {
        SongEvents foundValue = null;
        
        try
        {
            foundValue = valueOf( value );
        }
        catch( IllegalArgumentException e )
        {
            return false;
        }
        
        return ( foundValue != null );
    }
}
