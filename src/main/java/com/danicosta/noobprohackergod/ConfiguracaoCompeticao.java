
package com.danicosta.noobprohackergod;

import java.util.List;
import java.util.Objects;

public final class ConfiguracaoCompeticao {

    private final SelecaoPersonagens selecao;
    private TemaConstrucao tema;

    public ConfiguracaoCompeticao() {
        this.selecao = new SelecaoPersonagens();
        this.selecao.selecionarTodos();
        this.tema = CatalogoTemas.obter(0);
    }

    public boolean selecionarPersonagem(
            PersonagemTipo personagem
    ) {
        return selecao.selecionar(
                Objects.requireNonNull(personagem)
        );
    }

    public boolean desmarcarPersonagem(
            PersonagemTipo personagem
    ) {
        return selecao.desmarcar(
                Objects.requireNonNull(personagem)
        );
    }

    public boolean alternarPersonagem(
            PersonagemTipo personagem
    ) {
        return selecao.alternar(
                Objects.requireNonNull(personagem)
        );
    }

    public boolean personagemSelecionado(
            PersonagemTipo personagem
    ) {
        return selecao.estaSelecionado(personagem);
    }

    public void selecionarTema(int indice) {
        this.tema = CatalogoTemas.obter(indice);
    }

    public void definirTema(TemaConstrucao tema) {
        this.tema = Objects.requireNonNull(tema);
    }

    public TemaConstrucao getTema() {
        return tema;
    }

    public List<PersonagemTipo> getPersonagens() {
        return selecao.getSelecionados();
    }

    public SelecaoPersonagens getSelecao() {
        return selecao;
    }

    public int getQuantidadePersonagens() {
        return selecao.getQuantidadeSelecionados();
    }

    public boolean podeIniciar() {
        return tema != null && selecao.podeIniciar();
    }

    public void selecionarTodos() {
        selecao.selecionarTodos();
    }

    public void limparSelecao() {
        selecao.limpar();
    }

    public void restaurarPadrao() {
        selecao.limpar();
        selecao.selecionarTodos();
        tema = CatalogoTemas.obter(0);
    }
}
