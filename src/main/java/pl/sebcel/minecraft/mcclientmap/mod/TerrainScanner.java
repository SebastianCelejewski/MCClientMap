package pl.sebcel.minecraft.mcclientmap.mod;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TerrainScanner {

    private Map<ChunkPos, Date> lastRenderTime = new HashMap<>();
    private final static int chunkImageTTLinSeconds = 30;
    private Set<String> ignoredBlocks = new HashSet<>();

    public TerrainScanner() {
        ignoredBlocks.add("Barrier");
        ignoredBlocks.add("Air");
        ignoredBlocks.add("Torch");
        ignoredBlocks.add("Sign");
    }

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
        boolean dataValid = false;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = heightMap[x * 16 + z];
                if (y > 0) {
                    dataValid = true;
                }
                y = 255;
                IBlockState blockState = null;
                String blockName = null;
                do {
                    blockState = chunk.getBlockState(x, y, z);
                    blockName = blockState.getBlock().getLocalizedName();
                    y--;
                } while (y > 0 && ignoredBlocks.contains(blockName));
                colorMap[x * 16 + z] = blockState.getMaterial().getMaterialMapColor().colorValue;
            }
        }

        lastRenderTime.put(chunkPosition, new Date());
        return new ChunkTerrainData(heightMap, colorMap, dataValid);
    }
}