package pl.sebcel.minecraft.mcclientmap.mod;

public class ChunkTerrainData {

    private int[] heightMap;

    private int[] colorMap;
    
    private boolean dataValid;

    public ChunkTerrainData(int[] heightMap, int[] colorMap, boolean dataValid) {
        this.heightMap = heightMap;
        this.colorMap = colorMap;
        this.dataValid = dataValid;
    }

    public int[] getHeightMap() {
        return heightMap;
    }

    public int[] getColorMap() {
        return colorMap;
    }
    
    public boolean isDataValid() {
        return dataValid;
    }

}