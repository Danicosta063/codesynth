
package com.danicosta.noobprohackergod;

import java.util.Objects;

public final class ConfiguracaoNPC {

    public static final double VELOCIDADE_MOVIMENTO = 0.25;

    public static final double ALCANCE_CONSTRUCAO = 4.5;

    public static final int INTERVALO_BLOCO_TICKS = 5;

    private final PersonagemTipo personagem;

    public ConfiguracaoNPC(PersonagemTipo personagem) {
        this.personagem = Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );
    }

    public PersonagemTipo getPersonagem() {
        return personagem;
    }

    public double getVelocidadeMovimento() {
        return VELOCIDADE_MOVIMENTO;
    }

    public double getAlcanceConstrucao() {
        return ALCANCE_CONSTRUCAO;
    }

    public int getIntervaloBlocoTicks() {
        return INTERVALO_BLOCO_TICKS;
    }
}
