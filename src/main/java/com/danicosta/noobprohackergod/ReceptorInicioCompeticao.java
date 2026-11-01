
package com.danicosta.noobprohackergod;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public final class ReceptorInicioCompeticao {

    private ReceptorInicioCompeticao() {
    }

    public static void registrar() {

        ServerPlayNetworking.registerGlobalReceiver(
                PacoteInicioCompeticao.TYPE,
                (pacote, contexto) -> {

                    contexto.server().execute(() -> {
                        processar(
                                pacote,
                                contexto.player()
                        );
                    });
                }
        );

        NoobProHackerGod.LOGGER.info(
                "Receptor de inicio da competicao registrado."
        );
    }

    private static void processar(
            PacoteInicioCompeticao pacote,
            ServerPlayer jogador
    ) {
        if (jogador == null) {
            return;
        }

        DadosInicioCompeticao dados;

        try {
            dados = pacote.obterDados();
        } catch (IllegalArgumentException erro) {
            jogador.sendSystemMessage(
                    Component.literal(
                            "Configuracao de competicao invalida."
                    )
            );
            return;
        }

        if (!jogador.permissions()
                .hasPermission(Permissions.COMMANDS_MODERATOR)) {
            jogador.sendSystemMessage(
                    Component.literal(
                            "Voce precisa de permissao de operador."
                    )
            );
            return;
        }

        ServerLevel mundo = jogador.level();

        SelecaoPersonagens selecao =
                new SelecaoPersonagens();

        List<PersonagemTipo> personagens =
                dados.getPersonagens();

        for (PersonagemTipo personagem : personagens) {
            selecao.selecionar(personagem);
        }

        if (!ControladorCompeticao.podeIniciar(
                mundo,
                selecao
        )) {
            jogador.sendSystemMessage(
                    Component.literal(
                            "Ja existe uma competicao ativa."
                    )
            );
            return;
        }

        BlockPos origem = jogador.blockPosition();

        int frenteX = 0;
        int frenteZ = 1;

        List<PersonagemTipo> iniciados =
                ControladorCompeticao.iniciar(
                        mundo,
                        dados.getTema(),
                        selecao,
                        origem,
                        frenteX,
                        frenteZ
                );

        jogador.sendSystemMessage(
                Component.literal(
                        "Competicao iniciada! Tema: "
                                + dados.getTema().getNome()
                                + " | Participantes: "
                                + iniciados.size()
                )
        );
    }
}
