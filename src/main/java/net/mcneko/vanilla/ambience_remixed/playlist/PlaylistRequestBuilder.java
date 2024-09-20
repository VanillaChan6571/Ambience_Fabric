package net.mcneko.vanilla.ambience_remixed.playlist;

// Ambience Remixed
import net.mcneko.vanilla.ambience_remixed.PlayPriority;

@SuppressWarnings( "unchecked" )
public class PlaylistRequestBuilder<T extends PlaylistRequestBuilder<T>>
{
    // Protected Fields
    protected PlayPriority playPriority = PlayPriority.MEDIUM;
    protected Double fadeTime = null;
    protected boolean canBeOverriden = false;
    protected boolean shouldLoop = true;
    protected boolean shouldDeferPlay = false;
    protected boolean shouldAllowMerging = false;
    
    protected IPlaylistStillValidCallback playlistStillValidCallback = null;
    
    // Construction
    public PlaylistRequestBuilder() {}
    
    // Public Methods
    public PlaylistRequest buildPlayRequest()
    {
        return new PlaylistRequest( playPriority, fadeTime, canBeOverriden, shouldLoop, shouldDeferPlay, shouldAllowMerging, playlistStillValidCallback );
    }
    
    public T playPriority( PlayPriority _playPriority )
    {
        playPriority = _playPriority;
        
        return (T)this;
    }
    
    public T fadeTime( Double _fadeTime )
    {
        fadeTime = _fadeTime;
        
        return (T)this;
    }
    
    public T canBeOverriden( boolean _canBeOverriden )
    {
        canBeOverriden = _canBeOverriden;
        
        return (T)this;
    }
    
    public T shouldLoop( boolean _shouldLoop )
    {
        shouldLoop = _shouldLoop;
        
        return (T)this;
    }
    
    public T shouldDeferPlay( boolean _shouldDeferPlay )
    {
        shouldDeferPlay = _shouldDeferPlay;
        
        return (T)this;
    }
    
    public T shouldAllowMerging( boolean _shouldAllowMerging )
    {
        shouldAllowMerging = _shouldAllowMerging;
        
        return (T)this;
    }
    
    public T playlistStillValidCallback( IPlaylistStillValidCallback _playlistStillValidCallback )
    {
        playlistStillValidCallback = _playlistStillValidCallback;
        
        return (T)this;
    }
}
