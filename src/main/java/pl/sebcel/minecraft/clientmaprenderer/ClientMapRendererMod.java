package pl.sebcel.minecraft.clientmaprenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

            System.out.println(getServerName());

            System.out.println("Player is entering chunk (" + event.getNewChunkX() + "," + event.getNewChunkZ() + ") from chunk (" + event.getOldChunkX() + "," + event.getOldChunkZ() + ") " + event.getPhase());

            try {
                World world = event.getEntity().getEntityWorld();
                scanTerrain(world, new ChunkPos(event.getNewChunkX() - 1, event.getNewChunkZ() - 1));
                scanTerrain(world, new ChunkPos(event.getNewChunkX() - 1, event.getNewChunkZ()));
                scanTerrain(world, new ChunkPos(event.getNewChunkX() - 1, event.getNewChunkZ() + 1));
                scanTerrain(world, new ChunkPos(event.getNewChunkX(), event.getNewChunkZ() - 1));
                scanTerrain(world, new ChunkPos(event.getNewChunkX(), event.getNewChunkZ()));
                scanTerrain(world, new ChunkPos(event.getNewChunkX(), event.getNewChunkZ() + 1));
                scanTerrain(world, new ChunkPos(event.getNewChunkX() + 1, event.getNewChunkZ() - 1));
                scanTerrain(world, new ChunkPos(event.getNewChunkX() + 1, event.getNewChunkZ()));
                scanTerrain(world, new ChunkPos(event.getNewChunkX() + 1, event.getNewChunkZ() + 1));
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getServerName() {
        try {
            if (FMLCommonHandler.instance().getMinecraftServerInstance() != null) {
                return "local_" + FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName();
            } else if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                return "server_ " + Minecraft.getMinecraft().getCurrentServerData().serverName;
            } else {
                return "unknown";
            }
        } catch (Throwable ex) {
            return "unknown";
        }
    }

    private Set<IBlockState> blockStates = new HashSet<>();
    private Map<ChunkPos, Date> lastRenderTime = new HashMap<>();
    private final static int chunkImageTTLinSeconds = 30;

    public void scanTerrain(World world, ChunkPos chunkPosition) {
        if (chunkRenderedRecently(chunkPosition)) {
            System.out.println("Skipping chunk (" + chunkPosition.x + "," + chunkPosition.z + ")");
            return;
        }

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
                    blockStates.add(blockState);
                    y--;
                } while (y > 0 && (blockName.equals("Air") || blockName.equals("Torch") || blockName.equals("Sign"))); // excluding air and torches
                colorMap[x * 16 + z] = blockState.getMaterial().getMaterialMapColor().colorValue;
            }
        }

        renderChunk(heightMap, colorMap, chunkPosition);
        lastRenderTime.put(chunkPosition, new Date());

    }

    private boolean chunkRenderedRecently(ChunkPos chunkPosition) {
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

    private void renderChunk(int[] heightMap, int[] colorMap, ChunkPos chunkPosition) {
        try {
            BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int blockColor = colorMap[x * 16 + z];
                    image.setRGB(x, z, blockColor);
                }
            }

            String fileName = "chunk_" + chunkPosition.x + "_" + chunkPosition.z + ".png";
            File outputFile = new File(fileName);
            System.out.println("Writing chunk to " + outputFile.getAbsolutePath());
            ImageIO.write(image, "png", outputFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}