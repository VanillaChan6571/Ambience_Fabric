package com.heavenssword.ambience_remixed;

// Java
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

// Minecraft
import net.minecraft.world.biome.Biome;
import net.minecraft.util.ResourceLocation;

// MinecraftForge
import net.minecraftforge.common.BiomeDictionary;

public final class SongLoader
{
    // Private Static Fields
    private static File mainMusicDir;
    private static boolean isEnabled = false;
    
    // Public Static Accessors/Mutators
    public static Boolean getIsEnabled()
    {
        return isEnabled;
    }
    
    public static void setIsEnabled( Boolean _isEnabled )
    {
        isEnabled = _isEnabled;
    }

    // Public Static Methods    
    public static void loadConfigIntoDB( File rootDirectorypath, SongDatabase songDB )
    {
        File config = new File( rootDirectorypath, "ambience_remixed.properties" );
        if( !config.exists() )
            initConfig( config );

        Properties props = new Properties();
        try
        {
            props.load( new FileReader( config ) );
            isEnabled = props.getProperty( "enabled" ).equals( "true" );

            if( isEnabled )
            {
                songDB.clear();
                
                Set<Object> keys = props.keySet();
                for( Object obj : keys )
                {
                    String s = (String)obj;

                    String[] tokens = s.split( "\\." );
                    if( tokens.length < 2 )
                        continue;

                    String keyType = tokens[0];
                    if( keyType.equals( "event" ) )
                    {
                        String eventKeyStr = tokens[1].toUpperCase();
                        
                        // Check to see if this is a custom event
                        if( SongEvents.hasValue( eventKeyStr ) )
                        {
                            SongEvents eventKey = SongEvents.valueOf( eventKeyStr );    
                            songDB.addSongsForEvent( eventKey, props.getProperty( s ).split( "," ) );
                        }
                        else
                            songDB.addSongsForCustomEvent( eventKeyStr, props.getProperty( s ).split( "," ) );
                    }
                    else if( keyType.equals( "biome" ) )
                    {                        
                        String biomeName = joinTokensExceptFirst( tokens );
                        Biome biome = BiomeMapper.getBiome( new ResourceLocation( biomeName ) );
                        
                        if( biome != null )
                        {
                            AmbienceRemixed.getLogger().debug( "SongLoader.LoadFrom() - Biome for \"" + biomeName + "\" = " + biome.toString() );
                            songDB.addSongsForBiome( biome.getRegistryName(), props.getProperty( s ).split( "," ) );
                        }
                        else
                            AmbienceRemixed.getLogger().debug( "SongLoader.LoadFrom() - Biome was NULL!" );
                    }
                    else if( keyType.matches( "primarytag|secondarytag" ) )
                    {
                        boolean primary = keyType.equals( "primarytag" );
                        String tagName = tokens[1].toUpperCase();
                        BiomeDictionary.Type type = BiomeMapper.getBiomeType( tagName );

                        if( type != null )
                        {
                            if( primary )
                                songDB.addSongsForPrimaryTag( type, props.getProperty( s ).split( "," ) );
                            else
                                songDB.addSongsForSecondaryTag( type, props.getProperty( s ).split( "," ) );
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        File musicDir = new File( rootDirectorypath, "music" );
        if( !musicDir.exists() )
            musicDir.mkdir();

        mainMusicDir = musicDir;
    }    

    public static InputStream loadSongStream( String songName )
    {
        AmbienceRemixed.getLogger().debug( "SongLoader.loadSongStream() - Loading song: " + ( songName != null ? songName : "NULL" ) );
        
        if( songName == null || songName.equals( "" ) || songName.equals( "null" ) )
            return null;

        File songFile = new File( mainMusicDir, songName + ".mp3" );
        if( songFile.getName().equals( "null.mp3" ) )
            return null;

        try
        {
            return new FileInputStream( songFile );
        }
        catch( FileNotFoundException e )
        {
            AmbienceRemixed.getLogger().error( "AmbienceRemixed::SongLoader.loadSongStream() - File \"" + songFile + "\" was not found. Please verify that your Ambience Remixed properties file exists and is properly configured!" );
            e.printStackTrace();
            
            return null;
        }
    }

    // Private Static Methods
    private static void initConfig( File configFile )
    {
        try
        {
            configFile.createNewFile();
            
            try( BufferedWriter writer = new BufferedWriter( new FileWriter( configFile ) ) )
            {
                writer.write( "# Ambience Remixed Config\n" );
                writer.write( "enabled=false" );
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }
    
    private static String joinTokensExceptFirst( String[] tokens )
    {
        String joinedTokensStr = "";
        int i = 0;
        for( String token : tokens )
        {
            if( i <= 0 )
            {
                ++i;
                continue;                
            }
            
            joinedTokensStr += token.toLowerCase();
            
            ++i;
        }
        
        return joinedTokensStr;
    }
}
