package br.com.companheirosteve;

import com.mojang.authlib.GameProfile;

import net.fabricmc.fabric.api.entity.FakePlayer;

import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SteveFakePlayerManager {

    private static final String NOME = "CompanheiroSteve";

    private static final UUID UUID_STEVE =
        UUID.nameUUIDFromBytes(
            "companheirosteve:fakeplayer".getBytes(
                StandardCharsets.UTF_8
            )
        );

    public static GameProfile criarPerfil() {
        return new GameProfile(UUID_STEVE, NOME);
    }

    public static SteveFakePlayer criarJogador(
        MinecraftServer servidor,
        ServerWorld mundo
    ) {
        GameProfile perfil = criarPerfil();

        SyncedClientOptions opcoes =
            SyncedClientOptions.createDefault();

        return new SteveFakePlayer(
            servidor,
            mundo,
            perfil,
            opcoes
        );
    }

    public static FakePlayer obterFakePlayerFabric(
        ServerWorld mundo
    ) {
        return FakePlayer.get(
            mundo,
            criarPerfil()
        );
    }
}
