
package com.danicosta.noobprohackergod;

import java.util.List;
import java.util.Objects;

public final class ExecutorConstrucao {

    private final ProjetoConstrucao projeto;
    private final List<BlocoConstrucao> blocos;
    private final ControleConstrucao controle;

    private int indiceAtual;

    public ExecutorConstrucao(ProjetoConstrucao projeto) {
        this.projeto = Objects.requireNonNull(
                projeto,
                "O projeto nao pode ser nulo"
        );

        this.blocos = OrganizadorBlocos.organizar(projeto);
        this.controle = new ControleConstrucao();
        this.indiceAtual = 0;

        if (blocos.isEmpty()) {
            controle.concluir();
        }
    }

    public BlocoConstrucao atualizar() {
        if (controle.isConcluido()) {
            return null;
        }

        if (!controle.atualizar()) {
            return null;
        }

        return blocos.get(indiceAtual);
    }

    public void confirmarBlocoColocado() {
        if (controle.isConcluido()) {
            return;
        }

        controle.registrarBlocoColocado();
        indiceAtual++;

        if (indiceAtual >= blocos.size()) {
            controle.concluir();
        }
    }

    public boolean isConcluido() {
        return controle.isConcluido();
    }

    public int getBlocosColocados() {
        return controle.getBlocosColocados();
    }

    public int getTotalBlocos() {
        return blocos.size();
    }

    public double getProgresso() {
        if (blocos.isEmpty()) {
            return 100.0;
        }

        return (indiceAtual * 100.0) / blocos.size();
    }

    public ProjetoConstrucao getProjeto() {
        return projeto;
    }
}
