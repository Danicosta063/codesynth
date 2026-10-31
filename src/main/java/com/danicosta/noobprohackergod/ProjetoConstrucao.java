
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ProjetoConstrucao {

    private final String tema;
    private final PersonagemTipo personagem;
    private final List<BlocoConstrucao> blocos;

    public ProjetoConstrucao(
            String tema,
            PersonagemTipo personagem
    ) {
        if (tema == null || tema.isBlank()) {
            throw new IllegalArgumentException(
                    "O tema nao pode estar vazio"
            );
        }

        this.tema = tema;
        this.personagem = Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        this.blocos = new ArrayList<>();
    }

    public void adicionarBloco(BlocoConstrucao bloco) {
        blocos.add(Objects.requireNonNull(
                bloco,
                "O bloco nao pode ser nulo"
        ));
    }

    public void adicionarBlocos(
            List<BlocoConstrucao> novosBlocos
    ) {
        Objects.requireNonNull(
                novosBlocos,
                "A lista nao pode ser nula"
        );

        for (BlocoConstrucao bloco : novosBlocos) {
            adicionarBloco(bloco);
        }
    }

    public String getTema() {
        return tema;
    }

    public PersonagemTipo getPersonagem() {
        return personagem;
    }

    public int getQuantidadeBlocos() {
        return blocos.size();
    }

    public boolean estaVazio() {
        return blocos.isEmpty();
    }

    public List<BlocoConstrucao> getBlocos() {
        return Collections.unmodifiableList(blocos);
    }

    public void limpar() {
        blocos.clear();
    }
}
