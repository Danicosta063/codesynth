
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.core.BlockPos;

public final class TransformadorConstrucao {

    private final BlockPos origem;
    private final int frenteX;
    private final int frenteZ;

    public TransformadorConstrucao(
            BlockPos origem,
            int frenteX,
            int frenteZ
    ) {
        this.origem = Objects.requireNonNull(
                origem,
                "A origem nao pode ser nula"
        );

        if (Math.abs(frenteX) + Math.abs(frenteZ) != 1) {
            throw new IllegalArgumentException(
                    "A frente deve ser uma direcao cardinal"
            );
        }

        this.frenteX = frenteX;
        this.frenteZ = frenteZ;
    }

    public BlockPos transformar(BlocoConstrucao bloco) {
        Objects.requireNonNull(
                bloco,
                "O bloco nao pode ser nulo"
        );

        return transformar(
                bloco.getX(),
                bloco.getY(),
                bloco.getZ()
        );
    }

    public BlockPos transformar(int x, int y, int z) {

        int direitaX = -frenteZ;
        int direitaZ = frenteX;

        int mundoX = origem.getX()
                + direitaX * x
                + frenteX * z;

        int mundoY = origem.getY() + y;

        int mundoZ = origem.getZ()
                + direitaZ * x
                + frenteZ * z;

        return new BlockPos(mundoX, mundoY, mundoZ);
    }

    public BlockPos getOrigem() {
        return origem;
    }
}
