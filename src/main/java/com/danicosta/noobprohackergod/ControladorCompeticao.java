
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class ControladorCompeticao {

    private ControladorCompeticao() {
    }

    public static List<PersonagemTipo> iniciar(
            ServerLevel mundo,
            TemaConstrucao tema,
            SelecaoPersonagens selecao,
            BlockPos origem,
            int frenteX,
            int frenteZ
    ) {
        Objects.requireNonNull(mundo);
        Objects.requireNonNull(tema);
        Objects.requireNonNull(selecao);
        Objects.requireNonNull(origem);

        if (!selecao.podeIniciar()) {
            throw new IllegalArgumentException(
                    "Selecione pelo menos um personagem"
            );
        }

        if (Math.abs(frenteX) + Math.abs(frenteZ) != 1) {
            throw new IllegalArgumentException(
                    "A direcao deve ser cardinal"
            );
        }

        List<PersonagemTipo> iniciados =
                new ArrayList<>();

        for (PersonagemTipo personagem :
                selecao.getSelecionados()) {

            boolean sucesso = IniciadorConstrucoes.iniciar(
                    mundo,
                    tema,
                    personagem,
                    origem,
                    frenteX,
                    frenteZ
            );

            if (sucesso) {
                iniciados.add(personagem);
            }
        }

        return List.copyOf(iniciados);
    }

    public static boolean podeIniciar(
            ServerLevel mundo,
            SelecaoPersonagens selecao
    ) {
        Objects.requireNonNull(mundo);
        Objects.requireNonNull(selecao);

        if (!selecao.podeIniciar()) {
            return false;
        }

        GerenciadorConstrucoes gerenciador =
                NoobProHackerGod.getGerenciador(
                        mundo.getServer()
                );

        if (gerenciador == null) {
            return false;
        }

        for (PersonagemTipo personagem :
                selecao.getSelecionados()) {

            if (gerenciador.getSessao(personagem) != null) {
                return false;
            }
        }

        return true;
    }

    public static int contarConcluidos(
            ServerLevel mundo,
            SelecaoPersonagens selecao
    ) {
        Objects.requireNonNull(mundo);
        Objects.requireNonNull(selecao);

        GerenciadorConstrucoes gerenciador =
                NoobProHackerGod.getGerenciador(
                        mundo.getServer()
                );

        if (gerenciador == null) {
            return 0;
        }

        int concluidos = 0;

        for (PersonagemTipo personagem :
                selecao.getSelecionados()) {

            SessaoConstrucao sessao =
                    gerenciador.getSessao(personagem);

            if (sessao != null && sessao.isConcluida()) {
                concluidos++;
            }
        }

        return concluidos;
    }
}
