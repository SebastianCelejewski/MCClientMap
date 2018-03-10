package pl.sebcel.minecraft.clientmaprenderer;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ClientMapRendererMod.MODID, name = ClientMapRendererMod.NAME, version = ClientMapRendererMod.VERSION)
public class ClientMapRendererMod {
    
    public static final String MODID = "clientsidemaprenderer";
    public static final String NAME = "Client-Side Map Renderer";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void handlePlayerEnteringChunkEvent(EnteringChunk event) {
        if (event.getEntity() instanceof EntityPlayer) {
            System.out.println("Player is entering chunk (" + event.getNewChunkX() + "," + event.getNewChunkZ() + ") from chunk (" + event.getOldChunkX() + "," + event.getOldChunkZ() + ") " + event.getPhase());
            World world = event.getEntity().getEntityWorld();
            scanTerrain(world, event.getNewChunkX(), event.getNewChunkZ());
        }
    }

    public void scanTerrain(World world, int chunkX, int chunkZ) {
        System.out.println("Scanning world in chunk (" + chunkX + "," + chunkZ + ")");
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        int[] heightMap = chunk.getHeightMap();
        int[] colorMap = new int[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = heightMap[x * 16 + z];
                IBlockState blockState = null;
                do {
                    blockState = chunk.getBlockState(x, y, z);
                    y--;
                } while (y > 0 && blockState.getBlock().getLocalizedName().equals("Air"));
                colorMap[x * 16 + z] = blockState.getMaterial().getMaterialMapColor().colorValue;

            }
        }

        renderChunk(heightMap, colorMap, chunkX, chunkZ);
    }

    private void renderChunk(int[] heightMap, int[] colorMap, int chunkX, int chunkZ) {
        try {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int blockColor = colorMap[x * 16 + z];
                    image.setRGB(x, z, blockColor);
                }
            }

            String fileName = "chunk_" + chunkX + "_" + chunkZ + ".png";
            File outputFile = new File(fileName);
            System.out.println("Writing chunk to " + outputFile.getAbsolutePath());
            ImageIO.write(image, "png", outputFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}