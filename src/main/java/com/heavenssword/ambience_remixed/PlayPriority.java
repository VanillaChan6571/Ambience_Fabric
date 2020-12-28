package com.heavenssword.ambience_remixed;

public enum PlayPriority
{
    // Enum Labels
    OVERRIDE( -1 ),
    
    HIGHEST( 0 ),
    VERY_HIGH( 1 ),
    HIGH( 2 ),
    MID_HIGH( 3 ),
    MEDIUM( 4 ),
    MID_LOW( 5 ),
    LOW( 6 ),
    LOWEST( 7 );
    
    // Public Field
    public final int Value;
    
    // Public Static Methods
    public static int compareTo( PlayPriority leftSide, PlayPriority rightSide )
    {
        if( leftSide.Value < rightSide.Value )
            return -1;
        else if( leftSide.Value > rightSide.Value )
            return 1;
        
        return 0;
    }
    
    // Private Construction
    private PlayPriority( int value )
    {
        Value = value;
    }
}
