
package com.danicosta.noobprohackergod;

public enum PersonagemTipo {

    NOOB("Noob", 0, 1),
    PRO("Pro", 1, 2),
    HACKER("Hacker", 2, 3),
    GOD("God", 3, 4);

    private final String nome;
    private final int posicao;
    private final int nivelConstrucao;

    PersonagemTipo(
            String nome,
            int posicao,
            int nivelConstrucao
    ) {
        this.nome = nome;
        this.posicao = posicao;
        this.nivelConstrucao = nivelConstrucao;
    }

    public String getNome() {
        return nome;
    }

    public int getPosicao() {
        return posicao;
    }

    public int getNivelConstrucao() {
        return nivelConstrucao;
    }

    public static PersonagemTipo buscar(String nome) {
        for (PersonagemTipo tipo : values()) {
            if (tipo.nome.equalsIgnoreCase(nome)) {
                return tipo;
            }
        }
        return null;
    }
}
