
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PacoteInicioCompeticao(
        int indiceTema,
        int mascaraPersonagens
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacoteInicioCompeticao>
            TYPE = new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath(
                            NoobProHackerGod.MOD_ID,
                            "iniciar_competicao"
                    )
            );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            PacoteInicioCompeticao
            > CODEC = StreamCodec.of(
                    PacoteInicioCompeticao::escrever,
                    PacoteInicioCompeticao::ler
            );

    private static void escrever(
            FriendlyByteBuf buffer,
            PacoteInicioCompeticao pacote
    ) {
        buffer.writeVarInt(pacote.indiceTema());
        buffer.writeVarInt(pacote.mascaraPersonagens());
    }

    private static PacoteInicioCompeticao ler(
            FriendlyByteBuf buffer
    ) {
        int tema = buffer.readVarInt();
        int mascara = buffer.readVarInt();

        return new PacoteInicioCompeticao(
                tema,
                mascara
        );
    }

    @Override
    public CustomPacketPayload.Type<
            ? extends CustomPacketPayload
            > type() {
        return TYPE;
    }

    public static PacoteInicioCompeticao criar(
            int indiceTema,
            List<PersonagemTipo> personagens
    ) {
        DadosInicioCompeticao dados =
                new DadosInicioCompeticao(
                        indiceTema,
                        personagens
                );

        int mascara = 0;

        for (PersonagemTipo personagem :
                dados.getPersonagens()) {

            mascara |= 1 << personagem.getPosicao();
        }

        return new PacoteInicioCompeticao(
                dados.getIndiceTema(),
                mascara
        );
    }

    public DadosInicioCompeticao obterDados() {
        if ((mascaraPersonagens & ~15) != 0
                || mascaraPersonagens == 0) {
            throw new IllegalArgumentException(
                    "Selecao de personagens invalida."
            );
        }

        List<PersonagemTipo> personagens =
                new ArrayList<>();

        for (PersonagemTipo personagem :
                PersonagemTipo.values()) {

            int bit = 1 << personagem.getPosicao();

            if ((mascaraPersonagens & bit) != 0) {
                personagens.add(personagem);
            }
        }

        return new DadosInicioCompeticao(
                indiceTema,
                personagens
        );
    }
}
