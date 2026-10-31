
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;

public final class IniciadorConstrucoes {

    private static final int TAMANHO_AREA = 64;
    private static final int ESPACAMENTO = 16;
    private static final int DISTANCIA_SLOTS =
            TAMANHO_AREA + ESPACAMENTO;

    private IniciadorConstrucoes() {
    }

    public static boolean iniciar(
            ServerLevel mundo,
            TemaConstrucao tema,
            PersonagemTipo personagem,
            BlockPos origemPrincipal,
            int frenteX,
            int frenteZ
    ) {
        Objects.requireNonNull(mundo);
        Objects.requireNonNull(tema);
        Objects.requireNonNull(personagem);
        Objects.requireNonNull(origemPrincipal);

        if (Math.abs(frenteX) + Math.abs(frenteZ) != 1) {
            throw new IllegalArgumentException(
                    "A direcao deve ser cardinal"
            );
        }

        MinecraftServer servidor = mundo.getServer();

        GerenciadorConstrucoes gerenciador =
                NoobProHackerGod.getGerenciador(servidor);

        if (gerenciador == null) {
            return false;
        }

        if (gerenciador.getSessao(personagem) != null) {
            return false;
        }

        BlockPos origemPersonagem = calcularOrigem(
                origemPrincipal,
                personagem,
                frenteX,
                frenteZ
        );

        SessaoConstrucao sessao =
                FabricaSessoesConstrucao.criar(
                        mundo,
                        tema,
                        personagem,
                        origemPersonagem,
                        frenteX,
                        frenteZ
                );

        return gerenciador.adicionarSessao(
                personagem,
                sessao
        );
    }

    public static BlockPos calcularOrigem(
            BlockPos origemPrincipal,
            PersonagemTipo personagem,
            int frenteX,
            int frenteZ
    ) {
        Objects.requireNonNull(origemPrincipal);
        Objects.requireNonNull(personagem);

        if (Math.abs(frenteX) + Math.abs(frenteZ) != 1) {
            throw new IllegalArgumentException(
                    "A direcao deve ser cardinal"
            );
        }

        int deslocamento =
                personagem.getPosicao() * DISTANCIA_SLOTS;

        int lateralX = frenteZ;
        int lateralZ = -frenteX;

        return origemPrincipal.offset(
                lateralX * deslocamento,
                0,
                lateralZ * deslocamento
        );
    }
}
