package pl.sebcel.minecraft.mcclientmap.mod;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
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
    
    private TerrainScanner terrainScanner = new TerrainScanner();
    private ChunkRenderer chunkRenderer = new ChunkRenderer();

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
            String serverName = getServerName();
            List<ChunkPos> chunkPositionsNearPlayer = get3x3chunkPositionsAroundCentralChunk(event.getNewChunkX(), event.getNewChunkZ());
            
            for (ChunkPos chunkPosition : chunkPositionsNearPlayer) {
                if (terrainScanner.chunkRenderedRecently(chunkPosition)) {
                    System.out.println("Skipping chunk (" + chunkPosition.x + "," + chunkPosition.z + ")");
                    continue;
                }

                ChunkTerrainData chunkTerrainData = terrainScanner.scanTerrain(world, chunkPosition);
                BufferedImage chunkImage = chunkRenderer.renderChunk(chunkTerrainData, chunkPosition);
                chunkRenderer.saveChunkImage(serverName, chunkImage, chunkPosition);
            }
        }
    }

    private List<ChunkPos> get3x3chunkPositionsAroundCentralChunk(int chunkX, int chunkZ) {
        List<ChunkPos> result = new ArrayList<>();
        result.add(new ChunkPos(chunkX - 1, chunkZ - 1));
        result.add(new ChunkPos(chunkX - 1, chunkZ));
        result.add(new ChunkPos(chunkX - 1, chunkZ + 1));
        result.add(new ChunkPos(chunkX, chunkZ - 1));
        result.add(new ChunkPos(chunkX, chunkZ));
        result.add(new ChunkPos(chunkX, chunkZ + 1));
        result.add(new ChunkPos(chunkX + 1, chunkZ - 1));
        result.add(new ChunkPos(chunkX + 1, chunkZ));
        result.add(new ChunkPos(chunkX + 1, chunkZ + 1));
        return result;
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

}