
package com.danicosta.noobprohackergod;

public final class AreaConstrucao {

    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private final int minZ;
    private final int maxZ;

    public AreaConstrucao(
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ
    ) {
        if (minX > maxX || minY > maxY || minZ > maxZ) {
            throw new IllegalArgumentException(
                    "Limites da area de construcao invalidos"
            );
        }

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public boolean contem(int x, int y, int z) {
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    public boolean sobrepoe(AreaConstrucao outra) {
        if (outra == null) {
            return false;
        }

        return minX <= outra.maxX
                && maxX >= outra.minX
                && minY <= outra.maxY
                && maxY >= outra.minY
                && minZ <= outra.maxZ
                && maxZ >= outra.minZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getLargura() {
        return maxX - minX + 1;
    }

    public int getAltura() {
        return maxY - minY + 1;
    }

    public int getProfundidade() {
        return maxZ - minZ + 1;
    }
}
