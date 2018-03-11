package pl.sebcel.minecraft.mcclientmap.mod;

public class ChunkTerrainData {

    private int[] heightMap;

    private int[] colorMap;

    public ChunkTerrainData(int[] heightMap, int[] colorMap) {
        this.heightMap = heightMap;
        this.colorMap = colorMap;
    }

    public int[] getHeightMap() {
        return heightMap;
    }

    public int[] getColorMap() {
        return colorMap;
    }

}