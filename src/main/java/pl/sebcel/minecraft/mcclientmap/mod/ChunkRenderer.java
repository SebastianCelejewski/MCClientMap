package pl.sebcel.minecraft.mcclientmap.mod;

import java.awt.image.BufferedImage;
import java.io.File;

import net.minecraft.util.math.ChunkPos;
import pl.sebcel.minecraft.mcclientmap.utils.FileUtils;

public class ChunkRenderer {
    
    private FileUtils fileUtils = new FileUtils();

    public BufferedImage renderChunk(ChunkTerrainData chunkTerrainData, ChunkPos chunkPosition) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int blockColor = chunkTerrainData.getColorMap()[x * 16 + z];
                image.setRGB(x, z, blockColor);
            }
        }

        return image;
    }
    
    public void saveChunkImage(BufferedImage chunkImage, ChunkPos chunkPosition) {
        String fileName = "chunk_" + chunkPosition.x + "_" + chunkPosition.z + ".png";
        File outputFile = new File(fileName);
        System.out.println("Writing chunk to " + outputFile.getAbsolutePath());
        fileUtils.saveImage(chunkImage,  outputFile);
    }    
}
