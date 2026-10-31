
package com.danicosta.noobprohackergod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoobProHackerGod implements ModInitializer {

    public static final String MOD_ID = "noobprohackergod";

    public static final Logger LOGGER =
            LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("================================");
        LOGGER.info("Noob Pro Hacker God iniciado!");
        LOGGER.info("Minecraft Java 1.21.11");
        LOGGER.info("Fabric Mod carregado!");
        LOGGER.info("================================");
    }
}
