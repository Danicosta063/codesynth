package br.com.companheirosteve;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class CompanheiroSteveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(
            SteveEntities.STEVE,
            SteveRenderer::new
        );

        System.out.println(
            "[Companheiro Steve] Renderizador registrado!"
        );
    }
}
