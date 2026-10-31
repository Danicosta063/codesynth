package br.com.vidareal;

import net.fabricmc.api.ModInitializer;

public class VidaReal implements ModInitializer {

    public static final String MOD_ID = "vidareal";
    public static final String MOD_NAME = "Vida Real";
    public static final String VERSION = "0.0.1";

    @Override
    public void onInitialize() {
        System.out.println(
            "[" + MOD_NAME + "] Versao " + VERSION + " iniciada!"
        );
    }
}
