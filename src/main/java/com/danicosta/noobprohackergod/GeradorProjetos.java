
package com.danicosta.noobprohackergod;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class GeradorProjetos {

    private GeradorProjetos() {
    }

    public static ProjetoConstrucao gerar(
            TemaConstrucao tema,
            PersonagemTipo personagem
    ) {
        Objects.requireNonNull(tema);
        Objects.requireNonNull(personagem);

        ProjetoConstrucao projeto = new ProjetoConstrucao(
                tema.getDescricaoCompleta(),
                personagem
        );

        int nivel = personagem.getNivelConstrucao();

        int largura = 7 + nivel * 4;
        int profundidade = 7 + nivel * 4;
        int altura = 4 + nivel * 2;

        long semente = Objects.hash(
                tema.getIdentificador(),
                personagem.name()
        );

        Random aleatorio = new Random(semente);

        Set<String> posicoes = new HashSet<>();

        Block parede = escolherParede(tema, nivel);
        Block piso = escolherPiso(tema, nivel);
        Block teto = escolherTeto(tema, nivel);

        for (int x = 0; x < largura; x++) {
            for (int z = 0; z < profundidade; z++) {
                adicionar(
                        projeto, posicoes,
                        x, 0, z, piso
                );
            }
        }

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
                        adicionar(
                                projeto, posicoes,
                                x, y, z, parede
                        );
                    }
                }
            }
        }

        for (int x = 0; x < largura; x++) {
            for (int z = 0; z < profundidade; z++) {
                adicionar(
                        projeto, posicoes,
                        x, altura + 1, z, teto
                );
            }
        }

        adicionarDetalhes(
                projeto,
                posicoes,
                largura,
                profundidade,
                nivel,
                aleatorio
        );

        OrganizadorBlocos.organizar(projeto);

        return projeto;
    }

    private static void adicionarDetalhes(
            ProjetoConstrucao projeto,
            Set<String> posicoes,
            int largura,
            int profundidade,
            int nivel,
            Random aleatorio
    ) {
        Block detalhe = switch (nivel) {
            case 1 -> Blocks.OAK_LOG;
            case 2 -> Blocks.STONE_BRICKS;
            case 3 -> Blocks.DIAMOND_BLOCK;
            default -> Blocks.GOLD_BLOCK;
        };

        int quantidade = nivel * 4;
        int tentativas = 0;
        int adicionados = 0;

        while (adicionados < quantidade
                && tentativas < quantidade * 20) {

            tentativas++;

            int x = 1 + aleatorio.nextInt(largura - 2);
            int z = 1 + aleatorio.nextInt(profundidade - 2);

            if (adicionar(
                    projeto, posicoes,
                    x, 1, z, detalhe
            )) {
                adicionados++;
            }
        }
    }

    private static boolean adicionar(
            ProjetoConstrucao projeto,
            Set<String> posicoes,
            int x,
            int y,
            int z,
            Block bloco
    ) {
        String chave = x + ":" + y + ":" + z;

        if (!posicoes.add(chave)) {
            return false;
        }

        projeto.adicionarBloco(
                new BlocoConstrucao(x, y, z, bloco)
        );

        return true;
    }

    private static Block escolherParede(
            TemaConstrucao tema,
            int nivel
    ) {
        String estilo = tema.getEstilo()
                .toLowerCase(Locale.ROOT);

        if (estilo.contains("medieval")) {
            return Blocks.STONE_BRICKS;
        }

        if (estilo.contains("futurista")) {
            return Blocks.QUARTZ_BLOCK;
        }

        if (estilo.contains("rustico")) {
            return Blocks.SPRUCE_PLANKS;
        }

        if (estilo.contains("industrial")) {
            return Blocks.IRON_BLOCK;
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
