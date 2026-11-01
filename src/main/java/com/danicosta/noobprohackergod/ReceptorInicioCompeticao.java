
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

                    NoobProHackerGod.LOGGER.info(
                            "[NPHG] Pacote recebido pelo servidor."
                    );

                    contexto.server().execute(() -> {
                        try {
                            processar(
                                    pacote,
                                    contexto.player()
                            );
                        } catch (Exception erro) {

                            NoobProHackerGod.LOGGER.error(
                                    "[NPHG] Erro ao processar competicao.",
                                    erro
                            );

                            contexto.player().sendSystemMessage(
                                    Component.literal(
                                            "[NPHG] ERRO no servidor: "
                                            + erro.getClass().getSimpleName()
                                    )
                            );
                        }
                    });
                }
        );

        NoobProHackerGod.LOGGER.info(
                "[NPHG] Receptor registrado."
        );
    }

    private static void processar(
            PacoteInicioCompeticao pacote,
            ServerPlayer jogador
    ) {
        if (jogador == null) {
            return;
        }

        jogador.sendSystemMessage(
                Component.literal(
                        "[NPHG] ETAPA 1: Servidor recebeu o pedido."
                )
        );

        DadosInicioCompeticao dados;

        try {
            dados = pacote.obterDados();
        } catch (IllegalArgumentException erro) {
            jogador.sendSystemMessage(
                    Component.literal(
                            "[NPHG] ERRO: Configuracao invalida."
                    )
            );
            return;
        }

        jogador.sendSystemMessage(
                Component.literal(
                        "[NPHG] ETAPA 2: Dados validados."
                )
        );

        if (!jogador.permissions()
                .hasPermission(Permissions.COMMANDS_MODERATOR)) {

            jogador.sendSystemMessage(
                    Component.literal(
                            "[NPHG] ERRO: Sem permissao de operador."
                    )
            );
            return;
        }

        jogador.sendSystemMessage(
                Component.literal(
                        "[NPHG] ETAPA 3: Permissao confirmada."
                )
        );

        ServerLevel mundo = jogador.level();

        SelecaoPersonagens selecao =
                new SelecaoPersonagens();

        List<PersonagemTipo> personagens =
                dados.getPersonagens();

        for (PersonagemTipo personagem : personagens) {
            selecao.selecionar(personagem);
        }

        jogador.sendSystemMessage(
                Component.literal(
                        "[NPHG] ETAPA 4: Personagens preparados."
                )
        );

        if (!ControladorCompeticao.podeIniciar(
                mundo,
                selecao
        )) {
            jogador.sendSystemMessage(
                    Component.literal(
                            "[NPHG] ERRO: Competicao ja ativa."
                    )
            );
            return;
        }

        jogador.sendSystemMessage(
                Component.literal(
                        "[NPHG] ETAPA 5: Iniciando construcoes."
                )
        );

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
                        "[NPHG] ETAPA 6: "
                        + iniciados.size()
                        + " construcao(oes) iniciada(s)."
                )
        );
    }
}
