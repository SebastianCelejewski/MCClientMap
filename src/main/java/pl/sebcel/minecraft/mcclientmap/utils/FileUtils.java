package pl.sebcel.minecraft.mcclientmap.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class FileUtils {
    
    public BufferedImage loadImage(File pngFilePath) {
        try {
            return ImageIO.read(pngFilePath);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load image from " + pngFilePath + ": " + ex.getMessage(), ex);
        }
    }

    public void saveImage(BufferedImage image, File pngFilePath) {
        try {
            ImageIO.write(image, "png", pngFilePath);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save image to " + pngFilePath + ": " + ex.getMessage(), ex);
        }
    }
}
