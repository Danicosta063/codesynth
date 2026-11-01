
package com.danicosta.noobprohackergod;

import java.util.Objects;
import java.util.UUID;

public final class DadosNPC {

    private final UUID identificador;
    private final PersonagemTipo personagem;

    public DadosNPC(PersonagemTipo personagem) {
        this.identificador = UUID.randomUUID();
        this.personagem = Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );
    }

    public UUID getIdentificador() {
        return identificador;
    }

    public PersonagemTipo getPersonagem() {
        return personagem;
    }

    public String getNome() {
        return personagem.getNome();
    }

    public String getIdentificadorEntidade() {
        return IdentificadorNPC.obter(personagem);
    }
}
