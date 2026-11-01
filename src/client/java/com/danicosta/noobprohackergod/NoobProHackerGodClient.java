
package com.danicosta.noobprohackergod;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.lwjgl.glfw.GLFW;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NoobProHackerGodClient
        implements ClientModInitializer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(
                    NoobProHackerGod.MOD_ID
            );

    private static final KeyMapping.Category CATEGORIA =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath(
                            NoobProHackerGod.MOD_ID,
                            "controles"
                    )
            );

    private static KeyMapping teclaInterface;

    @Override
    public void onInitializeClient() {

        teclaInterface = KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                        "key.noobprohackergod.abrir_interface",
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_N,
                        CATEGORIA
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                cliente -> {
                    while (teclaInterface.consumeClick()) {

                        if (cliente.player != null) {
                            cliente.player.displayClientMessage(
                                    Component.literal(
                                            "Noob Pro Hacker God: "
                                            + "tecla N detectada!"
                                    ),
                                    true
                            );
                        }
                    }
                }
        );

        LOGGER.info(
                "Noob Pro Hacker God - Cliente iniciado!"
        );

        LOGGER.info(
                "Tecla N registrada com sucesso."
        );
    }
        }
