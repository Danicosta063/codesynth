
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class BlocoConstrucao {

    private final int x;
    private final int y;
    private final int z;
    private final BlockState estado;

    public BlocoConstrucao(
            int x,
            int y,
            int z,
            BlockState estado
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.estado = Objects.requireNonNull(
                estado,
                "O estado do bloco nao pode ser nulo"
        );
    }

    public BlocoConstrucao(
            int x,
            int y,
            int z,
            Block bloco
    ) {
        this(
                x,
                y,
                z,
                Objects.requireNonNull(
                        bloco,
                        "O bloco nao pode ser nulo"
                ).defaultBlockState()
        );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public BlockState getEstado() {
        return estado;
    }

    public BlockPos getPosicaoRelativa() {
        return new BlockPos(x, y, z);
    }
}
