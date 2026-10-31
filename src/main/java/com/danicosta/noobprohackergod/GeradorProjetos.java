
package com.danicosta.noobprohackergod;

import java.util.Objects;
import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class GeradorProjetos {

    private GeradorProjetos() {
    }

    public static ProjetoConstrucao gerar(
            TemaConstrucao tema,
            PersonagemTipo personagem
    ) {
        Objects.requireNonNull(
                tema,
                "O tema nao pode ser nulo"
        );

        Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        ProjetoConstrucao projeto = new ProjetoConstrucao(
                tema.getDescricaoCompleta(),
                personagem
        );

        Random aleatorio = new Random();

        int nivel = personagem.getNivelConstrucao();

        int largura = 7 + nivel * 4;
        int profundidade = 7 + nivel * 4;
        int altura = 4 + nivel * 2;

        Block parede = escolherParede(tema, nivel);
        Block piso = escolherPiso(tema, nivel);
        Block teto = escolherTeto(tema, nivel);

        gerarPiso(
                projeto,
                largura,
                profundidade,
                piso
        );

        gerarParedes(
                projeto,
                largura,
                profundidade,
                altura,
                parede
        );

        gerarTeto(
                projeto,
                largura,
                profundidade,
                altura,
                teto
        );

        adicionarDetalhes(
                projeto,
                largura,
                profundidade,
                altura,
                nivel,
                aleatorio
        );

        return projeto;
    }

    private static void gerarPiso(
            ProjetoConstrucao projeto,
            int largura,
            int profundidade,
            Block bloco
    ) {
        for (int x = 0; x < largura; x++) {
            for (int z = 0; z < profundidade; z++) {
                projeto.adicionarBloco(
                        new BlocoConstrucao(x, 0, z, bloco)
                );
            }
        }
    }

    private static void gerarParedes(
            ProjetoConstrucao projeto,
            int largura,
            int profundidade,
            int altura,
            Block bloco
    ) {
        for (int y = 1; y <= altura; y++) {
            for (int x = 0; x < largura; x++) {
                for (int z = 0; z < profundidade; z++) {

                    boolean borda =
                            x == 0
                            || x == largura - 1
                            || z == 0
                            || z == profundidade - 1;

                    boolean entrada =
                            z == 0
                            && x == largura / 2
                            && y <= 2;

                    if (borda && !entrada) {
                        projeto.adicionarBloco(
                                new BlocoConstrucao(
                                        x, y, z, bloco
                                )
                        );
                    }
                }
            }
        }
    }

    private static void gerarTeto(
            ProjetoConstrucao projeto,
            int largura,
            int profundidade,
            int altura,
            Block bloco
    ) {
        int y = altura + 1;

        for (int x = 0; x < largura; x++) {
            for (int z = 0; z < profundidade; z++) {
                projeto.adicionarBloco(
                        new BlocoConstrucao(x, y, z, bloco)
                );
            }
        }
    }

    private static void adicionarDetalhes(
            ProjetoConstrucao projeto,
            int largura,
            int profundidade,
            int altura,
            int nivel,
            Random aleatorio
    ) {
        Block detalhe = switch (nivel) {
            case 1 -> Blocks.OAK_LOG;
            case 2 -> Blocks.STONE_BRICKS;
            case 3 -> Blocks.DIAMOND_BLOCK;
            default -> Blocks.GOLD_BLOCK;
        };

        for (int i = 0; i < nivel * 4; i++) {
            int x = 1 + aleatorio.nextInt(largura - 2);
            int z = 1 + aleatorio.nextInt(profundidade - 2);

            projeto.adicionarBloco(
                    new BlocoConstrucao(
                            x,
                            1,
                            z,
                            detalhe
                    )
            );
        }
    }

    private static Block escolherParede(
            TemaConstrucao tema,
            int nivel
    ) {
        String estilo = tema.getEstilo().toLowerCase(
                java.util.Locale.ROOT
        );

        if (estilo.contains("medieval")) {
            return Blocks.STONE_BRICKS;
        }

        if (estilo.contains("futurista")) {
            return Blocks.QUARTZ_BLOCK;
        }

        return switch (nivel) {
            case 1 -> Blocks.OAK_PLANKS;
            case 2 -> Blocks.STONE_BRICKS;
            case 3 -> Blocks.QUARTZ_BLOCK;
            default -> Blocks.WHITE_CONCRETE;
        };
    }

    private static Block escolherPiso(
            TemaConstrucao tema,
            int nivel
    ) {
        return switch (nivel) {
            case 1 -> Blocks.COBBLESTONE;
            case 2 -> Blocks.SPRUCE_PLANKS;
            case 3 -> Blocks.POLISHED_ANDESITE;
            default -> Blocks.QUARTZ_BLOCK;
        };
    }

    private static Block escolherTeto(
            TemaConstrucao tema,
            int nivel
    ) {
        return switch (nivel) {
            case 1 -> Blocks.OAK_PLANKS;
            case 2 -> Blocks.DARK_OAK_PLANKS;
            case 3 -> Blocks.BLACK_CONCRETE;
            default -> Blocks.GOLD_BLOCK;
        };
    }
}
