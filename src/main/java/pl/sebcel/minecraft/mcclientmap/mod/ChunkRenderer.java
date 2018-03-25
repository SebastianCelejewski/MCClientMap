package pl.sebcel.minecraft.mcclientmap.mod;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import net.minecraft.util.math.ChunkPos;
import pl.sebcel.minecraft.mcclientmap.utils.FileUtils;

public class ChunkRenderer {

    public final static String ROOT_FOLDER = "mcClientMaps";

    private FileUtils fileUtils = new FileUtils();

    public BufferedImage renderChunk(ChunkTerrainData chunkTerrainData, ChunkPos chunkPosition) {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Color color = getColor(chunkTerrainData, x, z);
                image.setRGB(x, z, color.getRGB());
            }
        }

        return image;
    }

    public void saveChunkImage(String serverName, String worldName, BufferedImage chunkImage, ChunkPos chunkPosition) {
        String fileName = ROOT_FOLDER + File.separator + serverName + "_" + worldName + File.separator + "chunk_" + chunkPosition.x + "_" + chunkPosition.z + ".png";
        File outputFile = new File(fileName);
        outputFile.getParentFile().mkdirs();
        System.out.println("Writing chunk to " + outputFile.getAbsolutePath());
        fileUtils.saveImage(chunkImage, outputFile);
    }

    private Color getColor(ChunkTerrainData chunkTerrainData, int x, int z) {
        int blockColor = chunkTerrainData.getColorMap()[x * 16 + z];
        Color color = new Color(blockColor);
        int myHeight = chunkTerrainData.getHeightMap()[x + z * 16];

        if (x > 1) {
            int leftHeight = chunkTerrainData.getHeightMap()[x - 1 + z * 16];
            if (leftHeight < myHeight) {
                color = brighter(color, 0.95);
            }
            if (leftHeight > myHeight) {
                color = darker(color, 0.95);
            }
        }

        if (z > 1) {
            int topHeight = chunkTerrainData.getHeightMap()[x + (z - 1) * 16];
            if (topHeight < myHeight) {
                color = brighter(color, 0.9);
            }
            if (topHeight > myHeight) {
                color = darker(color, 0.9);
            }
        }

        return color;
    }

    private Color brighter(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int alpha = c.getAlpha();

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        int newRed = Math.min((int) (r / factor), 255);
        int newGreen = Math.min((int) (g / factor), 255);
        int newBlue = Math.min((int) (b / factor), 255);

        return new Color(newRed, newGreen, newBlue, alpha);
    }

    private Color darker(Color c, double factor) {
        int newRed = Math.max((int) (c.getRed() * factor), 0);
        int newGreen = Math.max((int) (c.getGreen() * factor), 0);
        int newBlue = Math.max((int) (c.getBlue() * factor), 0);
        return new Color(newRed, newGreen, newBlue, c.getAlpha());
    }
}