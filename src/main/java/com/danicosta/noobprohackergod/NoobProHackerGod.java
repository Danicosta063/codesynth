
package com.danicosta.noobprohackergod;

import java.util.Map;
import java.util.WeakHashMap;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.server.MinecraftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NoobProHackerGod
        implements ModInitializer {

    public static final String MOD_ID =
            "noobprohackergod";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    private static final Map<
            MinecraftServer,
            GerenciadorConstrucoes
            > GERENCIADORES = new WeakHashMap<>();

    @Override
    public void onInitialize() {

        PayloadTypeRegistry.playC2S().register(
                PacoteInicioCompeticao.TYPE,
                PacoteInicioCompeticao.CODEC
        );

        ServerLifecycleEvents.SERVER_STARTED.register(
                servidor -> {
                    GERENCIADORES.put(
                            servidor,
                            new GerenciadorConstrucoes(servidor)
                    );

                    LOGGER.info(
                            "Gerenciador de construcoes iniciado."
                    );
                }
        );

        ServerTickEvents.END_SERVER_TICK.register(
                servidor -> {
                    GerenciadorConstrucoes gerenciador =
                            GERENCIADORES.get(servidor);

                    if (gerenciador != null) {
                        gerenciador.atualizar();
                    }
                }
        );

        ServerLifecycleEvents.SERVER_STOPPED.register(
                servidor -> {
                    GerenciadorConstrucoes gerenciador =
                            GERENCIADORES.remove(servidor);

                    if (gerenciador != null) {
                        gerenciador.limpar();
                    }

                    LOGGER.info(
                            "Gerenciador de construcoes encerrado."
                    );
                }
        );

        LOGGER.info(
                "Noob Pro Hacker God iniciado!"
        );

        LOGGER.info(
                "Pacote de inicio da competicao registrado."
        );
    }

    public static GerenciadorConstrucoes getGerenciador(
            MinecraftServer servidor
    ) {
        return GERENCIADORES.get(servidor);
    }
        }
