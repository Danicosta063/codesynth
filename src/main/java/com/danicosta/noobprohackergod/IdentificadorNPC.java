
package com.danicosta.noobprohackergod;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;

public final class IdentificadorNPC {

    private IdentificadorNPC() {
    }

    public static ResourceLocation obter(
            PersonagemTipo personagem
    ) {
        Objects.requireNonNull(
                personagem,
                "O personagem nao pode ser nulo"
        );

        return ResourceLocation.fromNamespaceAndPath(
                NoobProHackerGod.MOD_ID,
                switch (personagem) {
                    case NOOB -> "noob";
                    case PRO -> "pro";
                    case HACKER -> "hacker";
                    case GOD -> "god";
                }
        );
    }
}
