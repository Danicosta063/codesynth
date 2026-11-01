
package com.danicosta.noobprohackergod;

import java.util.Objects;

public final class IdentificadorNPC {

    private IdentificadorNPC() {
    }

    public static String obter(
            PersonagemTipo personagem
    ) {
        Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        return NoobProHackerGod.MOD_ID + ":" +
                switch (personagem) {
                    case NOOB -> "noob";
                    case PRO -> "pro";
                    case HACKER -> "hacker";
                    case GOD -> "god";
                };
    }
}
