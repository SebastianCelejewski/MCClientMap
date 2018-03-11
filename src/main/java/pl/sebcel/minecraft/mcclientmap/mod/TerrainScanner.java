package pl.sebcel.minecraft.mcclientmap.mod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TerrainScanner {

    private Map<ChunkPos, Date> lastRenderTime = new HashMap<>();
    private final static int chunkImageTTLinSeconds = 30;

    public boolean chunkRenderedRecently(ChunkPos chunkPosition) {
        Date lastRenderingTime = lastRenderTime.get(chunkPosition);
        Date now = new Date();
        if (lastRenderingTime != null) {
            long dt = now.getTime() - lastRenderingTime.getTime();
            System.out.println("Time: " + dt);
            return dt < chunkImageTTLinSeconds * 1000;
        } else {
            return false;
        }
    }
    
    public ChunkTerrainData scanTerrain(World world, ChunkPos chunkPosition) {
        
        System.out.println("Scanning world in chunk (" + chunkPosition.x + "," + chunkPosition.z + ")");
        Chunk chunk = world.getChunkFromChunkCoords(chunkPosition.x, chunkPosition.z);
        int[] heightMap = chunk.getHeightMap();
        int[] colorMap = new int[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = heightMap[x * 16 + z] + 5;
                if (y < 255) {
                    y = 255;
                }
                IBlockState blockState = null;
                String blockName = null;
                do {
                    blockState = chunk.getBlockState(x, y, z);
                    blockName = blockState.getBlock().getLocalizedName();
                    y--;
                } while (y > 0 && (blockName.equals("Air") || blockName.equals("Torch") || blockName.equals("Sign"))); // excluding air and torches
                colorMap[x * 16 + z] = blockState.getMaterial().getMaterialMapColor().colorValue;
            }
        }

        lastRenderTime.put(chunkPosition, new Date());

        return new ChunkTerrainData(heightMap, colorMap);
    }
}