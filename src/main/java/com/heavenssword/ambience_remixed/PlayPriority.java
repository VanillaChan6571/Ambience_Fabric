package com.heavenssword.ambience_remixed;

public enum PlayPriority
{
    // Enum Labels
    OVERRIDE( -1 ),
    
    HIGHEST( 0 ),
    HIGH( 1 ),
    MEDIUM( 2 ),
    MID_LOW( 3 ),
    LOW( 4 ),
    LOWEST( 5 );
    
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
