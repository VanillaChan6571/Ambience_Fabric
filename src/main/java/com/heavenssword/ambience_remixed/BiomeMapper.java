package com.heavenssword.ambience_remixed;

// Java
import java.util.HashMap;
import java.util.Map;

// Minecraft
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeMapper
{
    private static Map<ResourceLocation, Biome> biomeMap = null;

    public static void applyMappings()
    {
        biomeMap = new HashMap<ResourceLocation, Biome>();
        
        for( ResourceLocation biomeResource : ForgeRegistries.BIOMES.getKeys() )
        {
            Biome biome = ForgeRegistries.BIOMES.getValue( biomeResource );
            biomeMap.put( biome.getRegistryName(), biome );
        }
    }

    public static Biome getBiome( ResourceLocation registryName )
    {
        if( biomeMap == null )
            applyMappings();
        
        return biomeMap.get( registryName );
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
