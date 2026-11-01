
package com.danicosta.noobprohackergod;

import java.util.Objects;

public final class ConstrutorNPC {

    private final DadosNPC dados;
    private final ConfiguracaoNPC configuracao;

    public ConstrutorNPC(PersonagemTipo personagem) {
        Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        this.dados = new DadosNPC(personagem);
        this.configuracao = new ConfiguracaoNPC(personagem);
    }

    public DadosNPC getDados() {
        return dados;
    }

    public ConfiguracaoNPC getConfiguracao() {
        return configuracao;
    }

    public PersonagemTipo getPersonagem() {
        return dados.getPersonagem();
    }

    public String getNome() {
        return dados.getNome();
    }
}
