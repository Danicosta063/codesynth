
package com.danicosta.noobprohackergod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class TelaSelecaoPersonagens extends Screen {

    private static final int TOTAL_TEMAS =
            CatalogoTemas.quantidadeTotal();

    private final ConfiguracaoCompeticao configuracao =
            new ConfiguracaoCompeticao();

    private int indiceTema = 0;

    private Button botaoTema;
    private Button botaoIniciar;

    private String mensagemStatus = "";

    public TelaSelecaoPersonagens() {
        super(Component.literal(
                "Noob Pro Hacker God"
        ));
    }

    @Override
    protected void init() {
        super.init();

        int centroX = this.width / 2;
        int inicioY = this.height / 2 - 95;

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
                        Component.literal("< Anterior"),
                        botao -> mudarTema(-1)
                ).bounds(
                        centroX - 102,
                        inicioY + 80,
                        100,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Proximo >"),
                        botao -> mudarTema(1)
                ).bounds(
                        centroX + 2,
                        inicioY + 80,
                        100,
                        20
                ).build()
        );

        botaoTema = addRenderableWidget(
                Button.builder(
                        Component.literal("Tema"),
                        botao -> mudarTema(1)
                ).bounds(
                        centroX - 102,
                        inicioY + 106,
                        204,
                        20
                ).build()
        );

        atualizarBotaoTema();

        botaoIniciar = addRenderableWidget(
                Button.builder(
                        Component.literal(
                                "Iniciar competicao"
                        ),
                        botao -> iniciarCompeticao()
                ).bounds(
                        centroX - 100,
                        inicioY + 158,
                        200,
                        20
                ).build()
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Fechar"),
                        botao -> onClose()
                ).bounds(
                        centroX - 100,
                        inicioY + 184,
                        200,
                        20
                ).build()
        );

        atualizarBotaoIniciar();
    }

    private void adicionarBotao(
            PersonagemTipo personagem,
            int x,
            int y
    ) {
        Button botao = Button.builder(
                textoPersonagem(personagem),
                pressionado -> {
                    configuracao.alternarPersonagem(
                            personagem
                    );

                    pressionado.setMessage(
                            textoPersonagem(personagem)
                    );

                    atualizarBotaoIniciar();
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

        return Component.literal(
                (selecionado ? "[X] " : "[ ] ")
                        + personagem.getNome()
        );
    }

    private void mudarTema(int direcao) {
        indiceTema = Math.floorMod(
                indiceTema + direcao,
                TOTAL_TEMAS
        );

        configuracao.selecionarTema(indiceTema);

        atualizarBotaoTema();

        mensagemStatus = "";
    }

    private void atualizarBotaoTema() {
        if (botaoTema != null) {
            botaoTema.setMessage(
                    Component.literal(
                            "Tema: "
                            + configuracao.getTema().getNome()
                    )
            );
        }
    }

    private void atualizarBotaoIniciar() {
        if (botaoIniciar != null) {
            botaoIniciar.active =
                    configuracao.podeIniciar();
        }
    }

    private void iniciarCompeticao() {
        if (!configuracao.podeIniciar()) {
            mensagemStatus =
                    "Selecione pelo menos um personagem.";
            return;
        }

        mensagemStatus =
                "Configuracao pronta! "
                + configuracao.getQuantidadePersonagens()
                + " personagem(ns).";

        // A comunicacao com o servidor
        // sera implementada posteriormente.
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

        int centroX = this.width / 2;
        int inicioY = this.height / 2 - 95;

        graficos.drawCenteredString(
                this.font,
                "NOOB PRO HACKER GOD",
                centroX,
                inicioY - 30,
                0xFFFFFF
        );

        graficos.drawCenteredString(
                this.font,
                "Selecione os personagens",
                centroX,
                inicioY - 15,
                0xAAAAAA
        );

        graficos.drawCenteredString(
                this.font,
                "Selecionados: "
                        + configuracao.getQuantidadePersonagens()
                        + "/4",
                centroX,
                inicioY + 55,
                0x55FF55
        );

        graficos.drawCenteredString(
                this.font,
                "Tema "
                        + (indiceTema + 1)
                        + " de "
                        + TOTAL_TEMAS,
                centroX,
                inicioY + 68,
                0xFFFF55
        );

        TemaConstrucao tema = configuracao.getTema();

        graficos.drawCenteredString(
                this.font,
                tema.getCategoria()
                        + " | "
                        + tema.getEstilo(),
                centroX,
                inicioY + 132,
                0xAAAAAA
        );

        graficos.drawCenteredString(
                this.font,
                "Ambiente: " + tema.getAmbiente(),
                centroX,
                inicioY + 143,
                0xAAAAAA
        );

        if (!mensagemStatus.isEmpty()) {
            graficos.drawCenteredString(
                    this.font,
                    mensagemStatus,
                    centroX,
                    inicioY + 210,
                    0x55FF55
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
