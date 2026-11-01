
package com.danicosta.noobprohackergod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class TelaSelecaoPersonagens extends Screen {

    private final ConfiguracaoCompeticao configuracao =
            new ConfiguracaoCompeticao();

    public TelaSelecaoPersonagens() {
        super(Component.literal(
                "Noob Pro Hacker God"
        ));
    }

    @Override
    protected void init() {
        super.init();

        int centroX = this.width / 2;
        int inicioY = this.height / 2 - 65;

        adicionarBotao(
                PersonagemTipo.NOOB,
                centroX - 102,
                inicioY
        );

        adicionarBotao(
                PersonagemTipo.PRO,
                centroX + 2,
                inicioY
        );

        adicionarBotao(
                PersonagemTipo.HACKER,
                centroX - 102,
                inicioY + 26
        );

        adicionarBotao(
                PersonagemTipo.GOD,
                centroX + 2,
                inicioY + 26
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Fechar"),
                        botao -> onClose()
                ).bounds(
                        centroX - 100,
                        inicioY + 85,
                        200,
                        20
                ).build()
        );
    }

    private void adicionarBotao(
            PersonagemTipo personagem,
            int x,
            int y
    ) {
        Button botao = Button.builder(
                textoPersonagem(personagem),
                botaoPressionado -> {
                    configuracao.alternarPersonagem(
                            personagem
                    );

                    botaoPressionado.setMessage(
                            textoPersonagem(personagem)
                    );
                }
        ).bounds(x, y, 100, 20).build();

        addRenderableWidget(botao);
    }

    private Component textoPersonagem(
            PersonagemTipo personagem
    ) {
        boolean selecionado =
                configuracao.personagemSelecionado(
                        personagem
                );

        String marcador = selecionado
                ? "[X] "
                : "[ ] ";

        return Component.literal(
                marcador + personagem.getNome()
        );
    }

    @Override
    public void render(
            GuiGraphics graficos,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.render(
                graficos,
                mouseX,
                mouseY,
                delta
        );

        graficos.drawCenteredString(
                this.font,
                "NOOB PRO HACKER GOD",
                this.width / 2,
                this.height / 2 - 105,
                0xFFFFFF
        );

        graficos.drawCenteredString(
                this.font,
                "Selecione os personagens",
                this.width / 2,
                this.height / 2 - 90,
                0xAAAAAA
        );

        graficos.drawCenteredString(
                this.font,
                "Selecionados: "
                        + configuracao.getQuantidadePersonagens()
                        + "/4",
                this.width / 2,
                this.height / 2 + 3,
                0x55FF55
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
