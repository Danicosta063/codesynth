
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class FabricaSessoesConstrucao {

    private FabricaSessoesConstrucao() {
    }

    public static SessaoConstrucao criar(
            ServerLevel mundo,
            TemaConstrucao tema,
            PersonagemTipo personagem,
            BlockPos origem,
            int frenteX,
            int frenteZ
    ) {
        Objects.requireNonNull(mundo);
        Objects.requireNonNull(tema);
        Objects.requireNonNull(personagem);
        Objects.requireNonNull(origem);

        if (Math.abs(frenteX) + Math.abs(frenteZ) != 1) {
            throw new IllegalArgumentException(
                    "A direcao deve ser cardinal"
            );
        }

        ProjetoConstrucao projeto =
                GeradorProjetos.gerar(tema, personagem);

        TransformadorConstrucao transformador =
                new TransformadorConstrucao(
                        origem,
                        frenteX,
                        frenteZ
                );

        AreaConstrucao area = criarArea(
                origem,
                frenteX,
                frenteZ
        );

        ValidadorConstrucao validador =
                new ValidadorConstrucao(
                        area,
                        transformador
                );

        if (!validador.validarProjeto(projeto)) {
            throw new IllegalArgumentException(
                    "O projeto nao cabe na area reservada "
                    + "ou possui blocos invalidos"
            );
        }

        ColocadorBlocos colocador =
                new ColocadorBlocos(
                        mundo,
                        validador
                );

        ExecutorConstrucao executor =
                new ExecutorConstrucao(projeto);

        return new SessaoConstrucao(
                executor,
                colocador
        );
    }

    private static AreaConstrucao criarArea(
            BlockPos origem,
            int frenteX,
            int frenteZ
    ) {
        int lateralX = frenteZ;
        int lateralZ = -frenteX;

        int xFinal = origem.getX()
                + lateralX * 63
                + frenteX * 63;

        int zFinal = origem.getZ()
                + lateralZ * 63
                + frenteZ * 63;

        return new AreaConstrucao(
                Math.min(origem.getX(), xFinal),
                origem.getY(),
                Math.min(origem.getZ(), zFinal),
                Math.max(origem.getX(), xFinal),
                origem.getY() + 63,
                Math.max(origem.getZ(), zFinal)
        );
    }
}
