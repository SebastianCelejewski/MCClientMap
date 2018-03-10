package com.example.examplemod;

import java.awt.image.BufferedImage;

public class TerrainRenderer {
    public BufferedImage renderTerrain(MapData mapData) {
        BufferedImage image = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);

        System.out.println("Rendering");
        int nonZeroBytes = 0;

        for (int x = 0; x < 1024; x++) {
            for (int z = 0; z < 1024; z++) {
                int blockColor = mapData.getData(x, z);
                image.setRGB(x, z, blockColor);
                if (blockColor != 0) {
                    nonZeroBytes++;
                }
            }
        }
        
        System.out.println(nonZeroBytes);

        return image;
    }
}