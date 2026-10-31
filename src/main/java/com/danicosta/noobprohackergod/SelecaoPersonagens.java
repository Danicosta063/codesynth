
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class SelecaoPersonagens {

    private final Set<PersonagemTipo> selecionados =
            EnumSet.noneOf(PersonagemTipo.class);

    public boolean selecionar(PersonagemTipo personagem) {
        Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        return selecionados.add(personagem);
    }

    public boolean desmarcar(PersonagemTipo personagem) {
        Objects.requireNonNull(personagem);

        return selecionados.remove(personagem);
    }

    public boolean alternar(PersonagemTipo personagem) {
        Objects.requireNonNull(personagem);

        if (selecionados.contains(personagem)) {
            selecionados.remove(personagem);
            return false;
        }

        selecionados.add(personagem);
        return true;
    }

    public boolean estaSelecionado(
            PersonagemTipo personagem
    ) {
        return selecionados.contains(personagem);
    }

    public int getQuantidadeSelecionados() {
        return selecionados.size();
    }

    public boolean podeIniciar() {
        return !selecionados.isEmpty()
                && selecionados.size() <= 4;
    }

    public List<PersonagemTipo> getSelecionados() {
        List<PersonagemTipo> resultado =
                new ArrayList<>(selecionados);

        resultado.sort(
                (a, b) -> Integer.compare(
                        a.getPosicao(),
                        b.getPosicao()
                )
        );

        return List.copyOf(resultado);
    }

    public void selecionarTodos() {
        selecionados.addAll(
                EnumSet.allOf(PersonagemTipo.class)
        );
    }

    public void limpar() {
        selecionados.clear();
    }
}
