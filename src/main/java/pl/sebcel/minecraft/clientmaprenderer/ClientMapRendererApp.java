package pl.sebcel.minecraft.clientmaprenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;

import javax.imageio.ImageIO;

import net.minecraft.util.math.ChunkPos;

public class ClientMapRendererApp {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage: java -jar cmr.jara <chunk_png_directory_path>");
            System.exit(-1);
        }

        String inputDirectoryPath = args[0];

        new ClientMapRendererApp().run(inputDirectoryPath);
        ;
    }

    public void run(String inputDirectoryPath) {
        File inputDirectory = new File(inputDirectoryPath);
        System.out.println("Looking for chunk png files in " + inputDirectory.getAbsolutePath());

        File[] pngFiles = inputDirectory.listFiles((FileFilter) pathname -> pathname.getName().endsWith(".png"));

        Integer minX = null;
        Integer maxX = null;
        Integer minZ = null;
        Integer maxZ = null;
        for (File pngFile : pngFiles) {
            ChunkPos chunkCoordinates = getChunkCoordinatesFromFileName(pngFile.getName());
            if (minX == null || minX > chunkCoordinates.x) {
                minX = chunkCoordinates.x;
            }
            if (maxX == null || maxX < chunkCoordinates.x) {
                maxX = chunkCoordinates.x;
            }
            if (minZ == null || minZ > chunkCoordinates.z) {
                minZ = chunkCoordinates.z;
            }
            if (maxZ == null || maxZ < chunkCoordinates.z) {
                maxZ = chunkCoordinates.z;
            }
        }

        System.out.println("x: " + minX + " - " + maxX);
        System.out.println("z: " + minZ + " - " + maxZ);

        int width = 16 * (maxX - minX + 1);
        int height = 16 * (maxZ - minZ + 1);
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (File pngFile : pngFiles) {
            System.out.println("Rendering image " + pngFile.getName());
            ChunkPos chunkCoordinates = getChunkCoordinatesFromFileName(pngFile.getName());
            BufferedImage chunkImage = loadImage(pngFile);

            int dx = (chunkCoordinates.x - minX) * 16;
            int dz = (chunkCoordinates.z - minZ) * 16;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    try {
                        result.setRGB(x + dx, z + dz, chunkImage.getRGB(x, z));
                    } catch (Exception ex) {
                        System.out.println("Source: ("+x+","+z+"), destination: ("+(x+dx)+","+(z+dz)+")");
                    }
                }
            }
        }

        saveImage(result, new File("output.png"));
    }

    private ChunkPos getChunkCoordinatesFromFileName(String fileName) {
        int x = Integer.parseInt(fileName.split("[_\\.]")[1]);
        int z = Integer.parseInt(fileName.split("[_\\.]")[2]);
        return new ChunkPos(x, z);
    }

    private BufferedImage loadImage(File pngFilePath) {
        try {
            return ImageIO.read(pngFilePath);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load image from " + pngFilePath + ": " + ex.getMessage(), ex);
        }
    }

    private void saveImage(BufferedImage image, File pngFilePath) {
        try {
            ImageIO.write(image, "png", pngFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}