package br.com.companheirosteve;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.SyncedClientOptions;
import net.minecraft.server.world.ServerWorld;

public class SteveFakePlayer extends ServerPlayerEntity {

    public SteveFakePlayer(
        MinecraftServer servidor,
        ServerWorld mundo,
        GameProfile perfil,
        SyncedClientOptions opcoes
    ) {
        super(servidor, mundo, perfil, opcoes);
    }
}
