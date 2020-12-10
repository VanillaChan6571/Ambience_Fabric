package com.heavenssword.ambience_remixed.worldScanning;

// Java
import java.util.List;
import java.util.ArrayList;

// Mojang
import com.mojang.datafixers.util.Pair;

// Minecraft
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.CauldronBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class VillageScanner
{
    // Public Constants
    public static final double SCAN_RADIUS_X = 32.0;
    public static final double SCAN_RADIUS_Y = 16.0;
    public static final double SCAN_RADIUS_Z = 32.0;
    
    public static final int VILLAGE_POPULATION_REQUIREMENT = 1;
    public static final int UNDENIABLE_VILLAGE_POPULATION_REQUIREMENT = 3;
    
    public static final float VILLAGE_SCORE_REQUIREMENT_LOW = 10.0f;
    public static final float VILLAGE_SCORE_REQUIREMENT_HIGH = 30.0f;
    public static final float UNDENIABLE_VILLAGE_SCORE_REQUIREMENT = 50.0f;
    
    public static final float VILLAGER_SCORE_WEIGHT = 3.0f;
    public static final float ILLAGER_SCORE_WEIGHT = 2.5f;
    public static final float TRADER_SCORE_WEIGHT = 1.2f;
    public static final float GOLEM_SCORE_WEIGHT = 1.8f;
    public static final float CAT_SCORE_WEIGHT = 1.5f;
    public static final float SHEEP_SCORE_WEIGHT = 0.6f;
    public static final float COW_SCORE_WEIGHT = 0.6f;
    public static final float PIG_SCORE_WEIGHT = 0.6f;
    public static final float CHICKEN_SCORE_WEIGHT = 0.5f;
    public static final float HORSE_SCORE_WEIGHT = 0.55f;
    public static final float ZOMBIE_VILLAGER_SCORE_WEIGHT = 0.6f;
    
    public static final float CRAFTING_SITE_SCORE_WEIGHT = 0.7f;
    public static final float BED_SCORE_WEIGHT = 2.0f;
    public static final float DOOR_SCORE_WEIGHT = 1.75f;
    
    // Private Static Fields
    private static BlockPos previousScanPos = null; 
    
    private static MutableBoundingBox estimatedVillageBounds = new MutableBoundingBox();
    
    private static boolean wasUndeniableVillageFound = false;
    
    private static int numVillagersNearby = 0;
    private static int numIllagersNearby = 0;
    private static int numTradersNearby = 0;
    private static int numGolemsNearby = 0;
    private static int numCatsNearby = 0;
    private static int numPigsNearby = 0;
    private static int numCowsNearby = 0;
    private static int numSheepNearby = 0;
    private static int numChickensNearby = 0;
    private static int numHorsesNearby = 0;
    private static int numZombieVillagersNearby = 0;
    
    private static int numHousesNearby = 0;
    private static int numDoorsNearby = 0;
    private static int numCraftingSitesNearby = 0;
    
    // Public Static Methods
    public static int getNumVillagersNearby()
    {
        return numVillagersNearby;
    }
    
    public static int getNumIllagersNearby()
    {
        return numIllagersNearby;
    }
    
    public static int getNumTradersNearby()
    {
        return numTradersNearby;
    }
    
    public static int getNumGolemsNearby()
    {
        return numGolemsNearby;
    }
    
    public static int getNumCatsNearby()
    {
        return numCatsNearby;
    }
    
    public static int getNumPigsNearby()
    {
        return numPigsNearby;
    }
    
    public static int getNumCowsNearby()
    {
        return numCowsNearby;
    }
    
    public static int getNumSheepNearby()
    {
        return numSheepNearby;
    }
    
    public static int getNumChickensNearby()
    {
        return numChickensNearby;
    }
    
    public static int getNumHorsesNearby()
    {
        return numHorsesNearby;
    }
    
    public static int getNumAnimalsNearby()
    {
        return ( numPigsNearby + numCowsNearby + numSheepNearby + numChickensNearby + numHorsesNearby );
    }
    
    public static int getNumZombieVillagersNearby()
    {
        return numZombieVillagersNearby;
    }
    
    public static int getNumHousesNearby()
    {
        return numHousesNearby;
    }
    
    public static int getNumDoorsNearby()
    {
        return numDoorsNearby;
    }
    
    public static int getNumCraftingSitesNearby()
    {
        return numCraftingSitesNearby;
    }
    
    public static Vector3d getEstimatedVillageCenter()
    {
        return getCenter( estimatedVillageBounds );
    }
    
    public static boolean getIsInsideEstimatedVillageBounds( BlockPos position )
    {
        return estimatedVillageBounds.isVecInside( position );
    }
    
    public static boolean getIsVillagerThresholdMet()
    {
        return ( numVillagersNearby >= VILLAGE_POPULATION_REQUIREMENT );
    }
    
    public static boolean getMetVillageRequirement()
    {
        float estimatedVillageScore = getEstimatedVillageScore();
        
        //AmbienceRemixed.getLogger().debug( "Villager Threshold met = " + ( getIsVillagerThresholdMet() ? "TRUE" : "FALSE" ) );
        //AmbienceRemixed.getLogger().debug( "Estimated VillageScore = " + estimatedVillageScore );
        
        return ( getIsVillagerThresholdMet() && estimatedVillageScore >= VILLAGE_SCORE_REQUIREMENT_LOW ) ||
               ( estimatedVillageScore >= VILLAGE_SCORE_REQUIREMENT_HIGH ) ||
               wasUndeniableVillageFound;
    }
    
    public static void scan( ClientPlayerEntity player )
    {
        if( player.isSpectator() || ( previousScanPos != null && player.getPosition().equals( previousScanPos ) ) )
            return;
        
        clearStats();
        previousScanPos = player.getPosition();
        
        World world = player.world;
        
        List<LivingEntity> nearbyLivingEntities = world.getEntitiesWithinAABB( LivingEntity.class,
                                                                               new AxisAlignedBB( player.getPosX() - SCAN_RADIUS_X, player.getPosY() - SCAN_RADIUS_Y, player.getPosZ() - SCAN_RADIUS_Z,
                                                                                                  player.getPosX() + SCAN_RADIUS_X, player.getPosY() + SCAN_RADIUS_Y, player.getPosZ() + SCAN_RADIUS_Z ) );
        
        for( LivingEntity livingEntity : nearbyLivingEntities )
        {
            if( livingEntity instanceof VillagerEntity )
                ++numVillagersNearby;
            else if( livingEntity instanceof WanderingTraderEntity )
                ++numTradersNearby;
            else if( livingEntity instanceof IronGolemEntity )
                ++numGolemsNearby;
            else if( livingEntity instanceof CatEntity )
                ++numCatsNearby;
            else if( livingEntity instanceof PigEntity )
                ++numPigsNearby;
            else if( livingEntity instanceof SheepEntity )
                ++numSheepNearby;
            else if( livingEntity instanceof CowEntity )
                ++numCowsNearby;
            else if( livingEntity instanceof ChickenEntity )
                ++numChickensNearby;
            else if( livingEntity instanceof HorseEntity )
                ++numHorsesNearby;
            else if( livingEntity instanceof AbstractIllagerEntity )
                ++numIllagersNearby;
            else if( livingEntity instanceof ZombieVillagerEntity )
                ++numZombieVillagersNearby;
            
            recalculateEstimatedVillageBounds( livingEntity.getPosition() );
        }

        List<Pair<BlockPos, Block>> nearbyBlocks = getBlocksInAABB( world,
                                                                    new AxisAlignedBB( player.getPosX() - SCAN_RADIUS_X, player.getPosY() - SCAN_RADIUS_Y, player.getPosZ() - SCAN_RADIUS_Z,
                                                                                       player.getPosX() + SCAN_RADIUS_X, player.getPosY() + SCAN_RADIUS_Y, player.getPosZ() + SCAN_RADIUS_Z ) );
        Block block = null;
        for( Pair<BlockPos, Block> blockPair : nearbyBlocks )
        {
            block = blockPair.getSecond();
            if( block instanceof CraftingTableBlock ||      // FletchingTable - Fletcher, SmithingTable - Toolsmith
                block instanceof CartographyTableBlock ||   // Cartographer
                block instanceof CauldronBlock ||           // Leatherworker
                block instanceof StonecutterBlock ||        // Mason
                block instanceof AbstractFurnaceBlock ||    // Furnace - General, BlastFurnace - Armorer, Smoker - Butcher  
                block instanceof BrewingStandBlock ||       // Cleric
                block instanceof ComposterBlock ||          // Farmer
                block instanceof BarrelBlock ||             // Fisherman
                block instanceof LecternBlock ||            // Librarian
                block instanceof LoomBlock ||               // Shepherd
                block instanceof GrindstoneBlock )          // Weaponsmith
            {
                ++numCraftingSitesNearby;
            }
            else if( block instanceof BedBlock )
                ++numHousesNearby;
            else if( block instanceof DoorBlock )
                ++numDoorsNearby;
            
            recalculateEstimatedVillageBounds( blockPair.getFirst() );
        }
        
        float estimatedVillageScore = getEstimatedVillageScore();
        if( estimatedVillageScore >= UNDENIABLE_VILLAGE_SCORE_REQUIREMENT && getNumVillagersNearby() >= UNDENIABLE_VILLAGE_POPULATION_REQUIREMENT )
            wasUndeniableVillageFound = true;
        else if( estimatedVillageScore <= 0.0f )
            wasUndeniableVillageFound = false;
    }
    
    // Private Methods
    private static void clearStats()
    {
        if( !wasUndeniableVillageFound )
        {
            estimatedVillageBounds.maxX = estimatedVillageBounds.maxY = estimatedVillageBounds.maxZ = 0;
            estimatedVillageBounds.minX = estimatedVillageBounds.minY = estimatedVillageBounds.minZ = 0;
        }
                
        numVillagersNearby = 0;
        numIllagersNearby = 0;
        numTradersNearby = 0;
        numGolemsNearby = 0;
        numCatsNearby = 0;
        numPigsNearby = 0;
        numCowsNearby = 0;
        numSheepNearby = 0;
        numChickensNearby = 0;
        numHorsesNearby = 0;
        numZombieVillagersNearby = 0;
        
        numHousesNearby = 0;
        numDoorsNearby = 0;
        numCraftingSitesNearby = 0;
    }
    
    private static float getEstimatedVillageScore()
    {
        return ( ( numVillagersNearby * VILLAGER_SCORE_WEIGHT ) +
                 ( numIllagersNearby * ILLAGER_SCORE_WEIGHT ) +
                 ( numTradersNearby * TRADER_SCORE_WEIGHT ) +
                 ( numGolemsNearby * GOLEM_SCORE_WEIGHT ) +
                 ( numCatsNearby * CAT_SCORE_WEIGHT ) +
                 ( numPigsNearby * PIG_SCORE_WEIGHT ) +
                 ( numCowsNearby * COW_SCORE_WEIGHT ) +
                 ( numSheepNearby * SHEEP_SCORE_WEIGHT ) +
                 ( numChickensNearby * CHICKEN_SCORE_WEIGHT ) +
                 ( numHorsesNearby * HORSE_SCORE_WEIGHT ) +
                 ( numZombieVillagersNearby * ZOMBIE_VILLAGER_SCORE_WEIGHT ) +
                 ( numHousesNearby * BED_SCORE_WEIGHT ) +
                 ( numDoorsNearby * DOOR_SCORE_WEIGHT ) +
                 ( numCraftingSitesNearby * CRAFTING_SITE_SCORE_WEIGHT ) );
    }
    
    private static List<Pair<BlockPos, Block>> getBlocksInAABB( World world, AxisAlignedBB area ) 
    {
        int i = MathHelper.floor( area.minX );
        int j = MathHelper.floor( area.minY );
        int k = MathHelper.floor( area.minZ );
        int l = MathHelper.floor( area.maxX );
        int i1 = MathHelper.floor( area.maxY );
        int j1 = MathHelper.floor( area.maxZ );
        
        List<Pair<BlockPos, Block>> blocksInAABB = new ArrayList<Pair<BlockPos, Block>>();

        for( int k1 = i; k1 <= l; ++k1 ) 
        {
            for( int l1 = j; l1 <= i1; ++l1 ) 
            {
                for( int i2 = k; i2 <= j1; ++i2 ) 
                {
                    BlockPos blockPos = new BlockPos( k1, l1, i2 );
                    BlockState blockState = world.getBlockState( blockPos );
                    blocksInAABB.add( Pair.of( blockPos, blockState.getBlock() ) );
                }
            }
        }
        
        return blocksInAABB;
    }
    
    private static void recalculateEstimatedVillageBounds( BlockPos position )
    {
        if( position.getX() < estimatedVillageBounds.minX )
            estimatedVillageBounds.minX = position.getX();
        else if( position.getX() > estimatedVillageBounds.maxX )
            estimatedVillageBounds.maxX = position.getX();
        
        if( position.getY() < estimatedVillageBounds.minY )
            estimatedVillageBounds.minY = position.getY();
        else if( position.getY() > estimatedVillageBounds.maxY )
            estimatedVillageBounds.maxY = position.getY();
        
        if( position.getZ() < estimatedVillageBounds.minZ )
            estimatedVillageBounds.minZ = position.getZ();
        else if( position.getZ() > estimatedVillageBounds.maxZ )
            estimatedVillageBounds.maxZ = position.getZ();
    }
    
    private static Vector3d getCenter( MutableBoundingBox boundingBox ) 
    {
        return new Vector3d( MathHelper.lerp( 0.5D, boundingBox.minX, boundingBox.maxX ), 
                             MathHelper.lerp( 0.5D, boundingBox.minY, boundingBox.maxY ), 
                             MathHelper.lerp( 0.5D, boundingBox.minZ, boundingBox.maxZ ) );
    }
}
