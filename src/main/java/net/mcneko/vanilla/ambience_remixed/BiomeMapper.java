package net.mcneko.vanilla.ambience_remixed;

// Java
import java.util.HashMap;
import java.util.Map;

// Minecraft
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.util.RegistryKey;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeMapper
{
    private static Map<ResourceLocation, Biome> biomeMap = null;
    private static Map<ResourceLocation, RegistryKey<Biome>> biomeRegistryKeyMap = null;

    public static void applyMappings()
    {
        biomeMap = new HashMap<ResourceLocation, Biome>();
        biomeRegistryKeyMap = new HashMap<ResourceLocation, RegistryKey<Biome>>();
        
        for( ResourceLocation biomeResource : ForgeRegistries.BIOMES.getKeys() )
        {
            Biome biome = ForgeRegistries.BIOMES.getValue( biomeResource );
            biomeMap.put( biome.getRegistryName(), biome );
            
            RegistryKey<Biome> biomeRegKey = RegistryKey.getOrCreateKey( Registry.BIOME_KEY, biomeResource );
            biomeRegistryKeyMap.put( biome.getRegistryName(), biomeRegKey );
        }
    }

    public static Biome getBiome( ResourceLocation registryName )
    {
        if( biomeMap == null )
            applyMappings();
        
        return biomeMap.get( registryName );
    }
    
    public static RegistryKey<Biome> getBiomeRegistryKey( ResourceLocation registryName )
    {
        if( biomeRegistryKeyMap == null )
            applyMappings();
        
        return biomeRegistryKeyMap.get( registryName );
    }

    public static Type getBiomeType( String typeName )
    {
        return BiomeDictionary.Type.getType( typeName );
    }
    
    public static Type getBiomeTypeFromCategory( Category category )
    {
        return BiomeDictionary.Type.fromVanilla( category );
    }
}
