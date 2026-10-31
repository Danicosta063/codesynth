
package com.danicosta.noobprohackergod;

import java.util.Objects;

public final class SessaoConstrucao {

    private final ExecutorConstrucao executor;
    private final ColocadorBlocos colocador;

    private boolean pausada;
    private boolean falhou;
    private String mensagemErro;

    public SessaoConstrucao(
            ProjetoConstrucao projeto,
            ColocadorBlocos colocador
    ) {
        this.executor = new ExecutorConstrucao(
                Objects.requireNonNull(
                        projeto,
                        "O projeto nao pode ser nulo"
                )
        );

        this.colocador = Objects.requireNonNull(
                colocador,
                "O colocador nao pode ser nulo"
        );

        if (!colocador.getValidador().validarProjeto(projeto)) {
            throw new IllegalArgumentException(
                    "O projeto possui blocos invalidos"
            );
        }

        this.pausada = false;
        this.falhou = false;
        this.mensagemErro = "";
    }

    public void atualizar() {
        if (pausada || falhou || executor.isConcluido()) {
            return;
        }

        BlocoConstrucao bloco = executor.atualizar();

        if (bloco == null) {
            return;
        }

        if (colocador.colocar(bloco)) {
            executor.confirmarBlocoColocado();
        } else {
            falhou = true;
            mensagemErro =
                    "Nao foi possivel colocar o bloco em "
                    + bloco.getX() + ", "
                    + bloco.getY() + ", "
                    + bloco.getZ();
        }
    }

    public void pausar() {
        pausada = true;
    }

    public void continuar() {
        if (!falhou && !executor.isConcluido()) {
            pausada = false;
        }
    }

    public boolean isPausada() {
        return pausada;
    }

    public boolean isConcluida() {
        return executor.isConcluido();
    }

    public boolean isFalhou() {
        return falhou;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public int getBlocosColocados() {
        return executor.getBlocosColocados();
    }

    public int getTotalBlocos() {
        return executor.getTotalBlocos();
    }

    public double getProgresso() {
        return executor.getProgresso();
    }

    public ProjetoConstrucao getProjeto() {
        return executor.getProjeto();
    }
}
