
package com.danicosta.noobprohackergod;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.server.MinecraftServer;

public final class GerenciadorConstrucoes {

    private final MinecraftServer servidor;

    private final Map<PersonagemTipo, SessaoConstrucao> sessoes =
            new EnumMap<>(PersonagemTipo.class);

    public GerenciadorConstrucoes(MinecraftServer servidor) {
        this.servidor = Objects.requireNonNull(
                servidor,
                "O servidor nao pode ser nulo"
        );
    }

    public boolean adicionarSessao(
            PersonagemTipo personagem,
            SessaoConstrucao sessao
    ) {
        Objects.requireNonNull(personagem);
        Objects.requireNonNull(sessao);

        if (sessoes.containsKey(personagem)) {
            return false;
        }

        sessoes.put(personagem, sessao);
        return true;
    }

    public void atualizar() {
        if (!servidor.isSameThread()) {
            throw new IllegalStateException(
                    "As construcoes devem ser atualizadas "
                    + "na thread principal do servidor"
            );
        }

        for (SessaoConstrucao sessao :
                new ArrayList<>(sessoes.values())) {

            if (!sessao.isConcluida() && !sessao.isFalhou()) {
                sessao.atualizar();
            }
        }
    }

    public SessaoConstrucao getSessao(
            PersonagemTipo personagem
    ) {
        return sessoes.get(personagem);
    }

    public boolean removerSessao(
            PersonagemTipo personagem
    ) {
        return sessoes.remove(personagem) != null;
    }

    public List<PersonagemTipo> getPersonagensAtivos() {
        return List.copyOf(sessoes.keySet());
    }

    public int getQuantidadeSessoes() {
        return sessoes.size();
    }

    public boolean todasConcluidas() {
        if (sessoes.isEmpty()) {
            return false;
        }

        for (SessaoConstrucao sessao : sessoes.values()) {
            if (!sessao.isConcluida()) {
                return false;
            }
        }

        return true;
    }

    public void limpar() {
        sessoes.clear();
    }
}
