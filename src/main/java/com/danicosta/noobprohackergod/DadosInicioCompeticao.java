
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DadosInicioCompeticao {

    private final int indiceTema;
    private final List<PersonagemTipo> personagens;

    public DadosInicioCompeticao(
            int indiceTema,
            List<PersonagemTipo> personagens
    ) {
        if (indiceTema < 0
                || indiceTema >= CatalogoTemas.quantidadeTotal()) {
            throw new IllegalArgumentException(
                    "Indice de tema invalido: " + indiceTema
            );
        }

        if (personagens == null
                || personagens.isEmpty()
                || personagens.size() > 4) {
            throw new IllegalArgumentException(
                    "Selecione de 1 a 4 personagens."
            );
        }

        List<PersonagemTipo> copia =
                new ArrayList<>();

        for (PersonagemTipo personagem : personagens) {
            if (personagem == null
                    || copia.contains(personagem)) {
                throw new IllegalArgumentException(
                        "Personagem invalido ou duplicado."
                );
            }

            copia.add(personagem);
        }

        copia.sort(
                (primeiro, segundo) ->
                        Integer.compare(
                                primeiro.getPosicao(),
                                segundo.getPosicao()
                        )
        );

        this.indiceTema = indiceTema;
        this.personagens =
                Collections.unmodifiableList(copia);
    }

    public int getIndiceTema() {
        return indiceTema;
    }

    public TemaConstrucao getTema() {
        return CatalogoTemas.obter(indiceTema);
    }

    public List<PersonagemTipo> getPersonagens() {
        return personagens;
    }

    public int getQuantidadePersonagens() {
        return personagens.size();
    }

    public boolean possuiPersonagem(
            PersonagemTipo personagem
    ) {
        return personagens.contains(personagem);
    }
}
