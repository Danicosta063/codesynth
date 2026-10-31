
package com.danicosta.noobprohackergod;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.core.BlockPos;

public final class ValidadorConstrucao {

    private final AreaConstrucao area;
    private final TransformadorConstrucao transformador;

    public ValidadorConstrucao(
            AreaConstrucao area,
            TransformadorConstrucao transformador
    ) {
        this.area = Objects.requireNonNull(
                area,
                "A area nao pode ser nula"
        );

        this.transformador = Objects.requireNonNull(
                transformador,
                "O transformador nao pode ser nulo"
        );
    }

    public boolean podeColocar(BlocoConstrucao bloco) {
        if (bloco == null) {
            return false;
        }

        BlockPos posicao = transformador.transformar(bloco);

        return area.contem(
                posicao.getX(),
                posicao.getY(),
                posicao.getZ()
        );
    }

    public boolean validarProjeto(ProjetoConstrucao projeto) {
        if (projeto == null || projeto.estaVazio()) {
            return false;
        }

        Set<BlockPos> posicoes = new HashSet<>();

        for (BlocoConstrucao bloco : projeto.getBlocos()) {
            if (!podeColocar(bloco)) {
                return false;
            }

            BlockPos posicao = transformador.transformar(bloco);

            if (!posicoes.add(posicao)) {
                return false;
            }
        }

        return true;
    }

    public int contarBlocosInvalidos(
            ProjetoConstrucao projeto
    ) {
        if (projeto == null) {
            return 0;
        }

        int invalidos = 0;
        Set<BlockPos> posicoes = new HashSet<>();

        for (BlocoConstrucao bloco : projeto.getBlocos()) {
            if (!podeColocar(bloco)) {
                invalidos++;
                continue;
            }

            BlockPos posicao = transformador.transformar(bloco);

            if (!posicoes.add(posicao)) {
                invalidos++;
            }
        }

        return invalidos;
    }

    public AreaConstrucao getArea() {
        return area;
    }

    public TransformadorConstrucao getTransformador() {
        return transformador;
    }
}
