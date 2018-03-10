package com.example.examplemod;

public class MapData {
    private int[][] data = new int[1024][1024];

    public void setData(int x, int z, int blockId) {
        data[x][z] = blockId;
    }

    public int getData(int x, int z) {
        return data[x][z];
    }
}