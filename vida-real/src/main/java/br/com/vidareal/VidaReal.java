package br.com.vidareal;

import net.fabricmc.api.ModInitializer;

public class VidaReal implements ModInitializer {

    public static final String MOD_ID = "vidareal";
    public static final String VERSION = "0.0.2";

    @Override
    public void onInitialize() {
        System.out.println(
            "[Vida Real] Versao " + VERSION + " carregada!"
        );

        System.out.println(
            "[Vida Real] Teste de inicializacao concluido!"
        );
    }
}
