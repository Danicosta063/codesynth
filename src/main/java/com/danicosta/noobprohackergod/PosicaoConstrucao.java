
package com.danicosta.noobprohackergod;

public final class PosicaoConstrucao {

    public static final int LARGURA_AREA = 64;
    public static final int PROFUNDIDADE_AREA = 64;
    public static final int ESPACO_ENTRE_AREAS = 16;

    private PosicaoConstrucao() {
    }

    public record Ponto(int x, int y, int z) {
    }

    public static Ponto calcular(
            int xInicial,
            int yInicial,
            int zInicial,
            int direcaoX,
            int direcaoZ,
            PersonagemTipo personagem
    ) {
        if (personagem == null) {
            throw new IllegalArgumentException(
                    "Personagem nao pode ser nulo"
            );
        }

        if (Math.abs(direcaoX) + Math.abs(direcaoZ) != 1) {
            throw new IllegalArgumentException(
                    "A direcao deve ser horizontal e cardinal"
            );
        }

        int distancia =
                personagem.getPosicao()
                * (LARGURA_AREA + ESPACO_ENTRE_AREAS);

        int deslocamentoX = -direcaoZ * distancia;
        int deslocamentoZ = direcaoX * distancia;

        return new Ponto(
                xInicial + deslocamentoX,
                yInicial,
                zInicial + deslocamentoZ
        );
    }
}
