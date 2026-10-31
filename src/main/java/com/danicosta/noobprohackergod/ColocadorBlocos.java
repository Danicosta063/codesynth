
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class ColocadorBlocos {

    private final ServerLevel mundo;
    private final ValidadorConstrucao validador;

    public ColocadorBlocos(
            ServerLevel mundo,
            ValidadorConstrucao validador
    ) {
        this.mundo = Objects.requireNonNull(
                mundo,
                "O mundo nao pode ser nulo"
        );

        this.validador = Objects.requireNonNull(
                validador,
                "O validador nao pode ser nulo"
        );
    }

    public boolean colocar(BlocoConstrucao bloco) {
        if (!validador.podeColocar(bloco)) {
            return false;
        }

        BlockPos posicao = validador
                .getTransformador()
                .transformar(bloco);

        if (mundo.isOutsideBuildHeight(posicao)) {
            return false;
        }

        if (!mundo.hasChunkAt(posicao)) {
            return false;
        }

        BlockState estado = bloco.getEstado();

        if (mundo.getBlockState(posicao).equals(estado)) {
            return true;
        }

        return mundo.setBlock(posicao, estado, 3);
    }

    public ServerLevel getMundo() {
        return mundo;
    }

    public ValidadorConstrucao getValidador() {
        return validador;
    }
}
