
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class CatalogoTemas {

    private static final String[] CONSTRUCOES = {
            "Casa", "Castelo", "Mansao", "Palacio",
            "Torre", "Fortaleza", "Prisao", "Escola",
            "Hospital", "Hotel", "Restaurante", "Loja",
            "Shopping", "Estadio", "Arena", "Museu",
            "Biblioteca", "Laboratorio", "Fabrica",
            "Fazenda", "Celeiro", "Moinho", "Farol",
            "Ponte", "Templo", "Igreja", "Catedral",
            "Pirâmide", "Bunker", "Base Secreta",
            "Cidade", "Vila", "Parque", "Jardim",
            "Navio", "Barco", "Submarino", "Aviao",
            "Helicoptero", "Foguete", "Trem", "Carro",
            "Caminhao", "Onibus", "Nave Espacial",
            "Robo", "Dragao", "Dinossauro", "Estatua",
            "Portal", "Montanha", "Ilha", "Aquario",
            "Zoologico", "Parque Aquatico", "Cinema",
            "Teatro", "Circo", "Montanha Russa"
    };

    private static final String[] CATEGORIAS = {
            "Arquitetura",
            "Fantasia",
            "Tecnologia",
            "Natureza",
            "Veiculos",
            "Criaturas",
            "Monumentos",
            "Entretenimento",
            "Sobrevivencia",
            "Aventura"
    };

    private static final String[] ESTILOS = {
            "Simples",
            "Moderno",
            "Medieval",
            "Futurista",
            "Antigo",
            "Luxuoso",
            "Rustico",
            "Industrial",
            "Fantasia",
            "Magico",
            "Steampunk",
            "Cyberpunk",
            "Minimalista",
            "Gigante",
            "Colorido",
            "Sombrio",
            "Tropical",
            "Oriental",
            "Europeu",
            "Espacial"
    };

    private static final String[] AMBIENTES = {
            "Superplano",
            "Floresta",
            "Deserto",
            "Neve",
            "Selva",
            "Oceano",
            "Montanha",
            "Caverna",
            "Ceu",
            "Espaco",
            "Vulcao",
            "Pantano",
            "Praia",
            "Ilha",
            "Subterraneo",
            "Cidade",
            "Campo",
            "Gelo",
            "Lava",
            "Outro Mundo"
    };

    private CatalogoTemas() {
    }

    public static int quantidadeTotal() {
        return CONSTRUCOES.length
                * CATEGORIAS.length
                * ESTILOS.length
                * AMBIENTES.length;
    }

    public static TemaConstrucao obter(int indice) {
        int total = quantidadeTotal();

        if (indice < 0 || indice >= total) {
            throw new IndexOutOfBoundsException(
                    "Indice de tema invalido: " + indice
            );
        }

        int ambienteIndice = indice % AMBIENTES.length;
        indice /= AMBIENTES.length;

        int estiloIndice = indice % ESTILOS.length;
        indice /= ESTILOS.length;

        int categoriaIndice = indice % CATEGORIAS.length;
        indice /= CATEGORIAS.length;

        int construcaoIndice = indice;

        return new TemaConstrucao(
                CONSTRUCOES[construcaoIndice],
                CATEGORIAS[categoriaIndice],
                ESTILOS[estiloIndice],
                AMBIENTES[ambienteIndice]
        );
    }

    public static List<TemaConstrucao> obterPagina(
            int pagina,
            int tamanhoPagina
    ) {
        if (pagina < 0 || tamanhoPagina <= 0) {
            throw new IllegalArgumentException(
                    "Pagina ou tamanho invalido"
            );
        }

        int total = quantidadeTotal();
        long inicio = (long) pagina * tamanhoPagina;

        if (inicio >= total) {
            return List.of();
        }

        int fim = (int) Math.min(
                inicio + tamanhoPagina,
                total
        );

        List<TemaConstrucao> resultado = new ArrayList<>();

        for (int i = (int) inicio; i < fim; i++) {
            resultado.add(obter(i));
        }

        return resultado;
    }

    public static List<TemaConstrucao> pesquisar(
            String texto,
            int limite
    ) {
        Objects.requireNonNull(texto);

        if (limite <= 0) {
            return List.of();
        }

        String busca = texto.trim().toLowerCase(Locale.ROOT);

        if (busca.isEmpty()) {
            return obterPagina(0, limite);
        }

        List<TemaConstrucao> resultado = new ArrayList<>();

        for (int i = 0; i < quantidadeTotal(); i++) {
            TemaConstrucao tema = obter(i);

            if (tema.getDescricaoCompleta()
                    .toLowerCase(Locale.ROOT)
                    .contains(busca)) {

                resultado.add(tema);

                if (resultado.size() >= limite) {
                    break;
                }
            }
        }

        return resultado;
    }
}
