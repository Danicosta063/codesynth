package br.com.companheirosteve;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;

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
