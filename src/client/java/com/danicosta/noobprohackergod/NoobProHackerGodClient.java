
package com.danicosta.noobprohackergod;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NoobProHackerGodClient
        implements ClientModInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    NoobProHackerGod.MOD_ID
            );

    @Override
    public void onInitializeClient() {

        LOGGER.info(
                "================================"
        );

        LOGGER.info(
                "Noob Pro Hacker God - Cliente iniciado!"
        );

        LOGGER.info(
                "Preparando interface de selecao."
        );

        LOGGER.info(
                "================================"
        );
    }
        }
