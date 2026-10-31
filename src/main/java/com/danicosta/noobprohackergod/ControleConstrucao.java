
package com.danicosta.noobprohackergod;

public final class ControleConstrucao {

    // Todos os personagens usam a mesma velocidade.
    public static final int INTERVALO_BLOCOS_TICKS = 5;

    private int ticksDecorridos;
    private int blocosColocados;
    private boolean concluido;

    public ControleConstrucao() {
        this.ticksDecorridos = 0;
        this.blocosColocados = 0;
        this.concluido = false;
    }

    public boolean atualizar() {
        if (concluido) {
            return false;
        }

        ticksDecorridos++;

        if (ticksDecorridos >= INTERVALO_BLOCOS_TICKS) {
            ticksDecorridos = 0;
            return true;
        }

        return false;
    }

    public void registrarBlocoColocado() {
        if (!concluido) {
            blocosColocados++;
        }
    }

    public int getBlocosColocados() {
        return blocosColocados;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void concluir() {
        concluido = true;
    }

    public void reiniciar() {
        ticksDecorridos = 0;
        blocosColocados = 0;
        concluido = false;
    }
}
