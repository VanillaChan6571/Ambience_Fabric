package com.heavenssword.ambience_remixed.events;

// Ambience Remixed
import com.heavenssword.ambience_remixed.SongDJ;

public abstract class AmbienceRemixedEventHandler implements IAmbienceRemixedEventHandler
{
    // Protected Fields
    SongDJ songDJ = null;

    // Public Methods
    @Override
    public void setSongDJ( SongDJ _songDJ )
    {
        songDJ = _songDJ;
    }
}
