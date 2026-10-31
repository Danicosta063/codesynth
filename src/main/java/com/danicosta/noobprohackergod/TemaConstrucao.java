
package com.danicosta.noobprohackergod;

import java.util.Locale;
import java.util.Objects;

public final class TemaConstrucao {

    private final String nome;
    private final String categoria;
    private final String estilo;
    private final String ambiente;

    public TemaConstrucao(
            String nome,
            String categoria,
            String estilo,
            String ambiente
    ) {
        this.nome = validar(nome, "nome");
        this.categoria = validar(categoria, "categoria");
        this.estilo = validar(estilo, "estilo");
        this.ambiente = validar(ambiente, "ambiente");
    }

    private static String validar(
            String valor,
            String campo
    ) {
        Objects.requireNonNull(
                valor,
                "O campo " + campo + " nao pode ser nulo"
        );

        String resultado = valor.trim();

        if (resultado.isEmpty()) {
            throw new IllegalArgumentException(
                    "O campo " + campo + " nao pode estar vazio"
            );
        }

        return resultado;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEstilo() {
        return estilo;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public String getIdentificador() {
        return normalizar(categoria)
                + "_"
                + normalizar(nome)
                + "_"
                + normalizar(estilo)
                + "_"
                + normalizar(ambiente);
    }

    private static String normalizar(String texto) {
        return texto.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    public String getDescricaoCompleta() {
        return nome
                + " | Categoria: " + categoria
                + " | Estilo: " + estilo
                + " | Ambiente: " + ambiente;
    }

    @Override
    public String toString() {
        return getDescricaoCompleta();
    }
}
