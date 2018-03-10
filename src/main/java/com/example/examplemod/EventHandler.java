package com.example.examplemod;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    private BlockPos lastPosition;
    private MapData mapData;
    private TerrainRenderer terrainRenderer;

    public EventHandler() {
        mapData = new MapData();
        terrainRenderer = new TerrainRenderer();
    }

    private long lastRenderTime = 0;
    private long renderDelay = 10 * 1000;

    @SubscribeEvent
    public void pickupItem(PlayerEvent event) {
        if (event != null && event.getEntity() != null && event.getEntity().getEntityWorld() != null) {
            Entity player = event.getEntity();
            BlockPos playerPosition = player.getPosition();
            World world = player.getEntityWorld();
            if (playerPosition != null) {
                if (lastPosition == null || !lastPosition.equals(playerPosition)) {
                    lastPosition = playerPosition;

                    if (new Date().getTime() - lastRenderTime > renderDelay) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                world.getMapStorage();

                                for (int x = 0; x < 1024; x++) {
                                    for (int z = 0; z < 1024; z++) {
                                        BlockPos blockPosition = world.getTopSolidOrLiquidBlock(new BlockPos(x, 64, z));
                                        int blockColor = event.getEntity().getEntityWorld().getBlockState(blockPosition.down()).getMapColor(world, blockPosition).colorValue;
                                        mapData.setData(blockPosition.getX(), blockPosition.getZ(), blockColor);
                                    }
                                }

                                File imageFile = new File("terrain.png");
                                imageFile.delete();
                                System.out.println("Rendering terrain image to " + imageFile.getAbsolutePath());
                                lastRenderTime = new Date().getTime();
                                BufferedImage image = terrainRenderer.renderTerrain(mapData);
                                try {
                                    ImageIO.write(image, "png", imageFile);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        }
    }
}