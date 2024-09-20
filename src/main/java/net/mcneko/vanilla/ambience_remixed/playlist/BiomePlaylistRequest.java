package net.mcneko.vanilla.ambience_remixed.playlist;

// Minecraft
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Set;

// Ambience Remixed
import net.mcneko.vanilla.ambience_remixed.PlayPriority;

public final class BiomePlaylistRequest extends PlaylistRequest
{
    // Private Fields
    private final ResourceLocation biomeRegistry;
    private final Set<BiomeDictionary.Type> tagSet;
    
    // Construction    
    public BiomePlaylistRequest( ResourceLocation _biomeRegistry, Set<BiomeDictionary.Type> _tagSet, PlayPriority _playPriority, Double _fadeTime, boolean _canBeOverriden, boolean _shouldLoop, boolean _shouldDeferPlay, boolean _shouldAllowMerging, IPlaylistStillValidCallback _playListStillValidCallback )
    {
        super( _playPriority, _fadeTime, _canBeOverriden, _shouldLoop, _shouldDeferPlay, _shouldAllowMerging, _playListStillValidCallback );
        
        biomeRegistry = _biomeRegistry;
        tagSet = _tagSet;
    }
    
    // Public Methods
    public ResourceLocation getBiomeRegistry()
    {
        return biomeRegistry;
    }
    
    public Set<BiomeDictionary.Type> getTagSet()
    {
        return tagSet;
    }
}
