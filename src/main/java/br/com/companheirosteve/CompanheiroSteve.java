package br.com.companheirosteve;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class CompanheiroSteve implements ModInitializer {

    public static final String MOD_ID = "companheirosteve";

    @Override
    public void onInitialize() {

        SteveEntities.registrar();

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                registrarComandos(dispatcher);
            }
        );

        System.out.println(
            "[Companheiro Steve] Mod inicializado!"
        );
    }

    private SteveEntity encontrarSteve(ServerPlayerEntity jogador) {

        Box area = jogador.getBoundingBox().expand(64.0);

        List<SteveEntity> steves =
            jogador.getEntityWorld().getEntitiesByClass(
                SteveEntity.class,
                area,
                npc -> npc.isAlive()
            );

        return steves.stream()
            .min(Comparator.comparingDouble(
                npc -> npc.squaredDistanceTo(jogador)
            ))
            .orElse(null);
    }

    private void registrarComandos(
        CommandDispatcher<ServerCommandSource> dispatcher
    ) {

        dispatcher.register(
            literal("steve")

                .then(literal("criar").executes(context -> {

                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity jogador;

                    try {
                        jogador = source.getPlayerOrThrow();
                    } catch (Exception e) {
                        source.sendError(Text.literal(
                            "Este comando so funciona para jogadores."
                        ));
                        return 0;
                    }

                    SteveEntity existente = encontrarSteve(jogador);

                    if (existente != null) {
                        source.sendError(Text.literal(
                            "Ja existe um Steve proximo!"
                        ));
                        return 0;
                    }

                    SteveEntity npc = SteveEntities.STEVE.create(
                        jogador.getEntityWorld(),
                        net.minecraft.entity.SpawnReason.COMMAND
                    );

                    if (npc == null) {
                        source.sendError(Text.literal(
                            "Nao foi possivel criar Steve."
                        ));
                        return 0;
                    }

                    npc.refreshPositionAndAngles(
                        jogador.getX() + 2,
                        jogador.getY(),
                        jogador.getZ() + 2,
                        jogador.getYaw(),
                        0
                    );

                    npc.setCustomName(
                        Text.literal("Companheiro Steve")
                    );

                    npc.setCustomNameVisible(true);
                    npc.setPersistent();

                    npc.definirDono(jogador);

                    if (!jogador.getEntityWorld().spawnEntity(npc)) {
                        source.sendError(Text.literal(
                            "Falha ao criar Steve no mundo."
                        ));
                        return 0;
                    }

                    source.sendFeedback(
                        () -> Text.literal(
                            "Companheiro Steve criado!"
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("seguir").executes(context -> {

                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity jogador;

                    try {
                        jogador = source.getPlayerOrThrow();
                    } catch (Exception e) {
                        return 0;
                    }

                    SteveEntity npc = encontrarSteve(jogador);

                    if (npc == null) {
                        source.sendError(Text.literal(
                            "Nenhum Steve encontrado em 64 blocos."
                        ));
                        return 0;
                    }

                    npc.definirDono(jogador);
                    npc.seguir();

                    source.sendFeedback(
                        () -> Text.literal(
                            "Steve agora esta seguindo voce!"
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("parar").executes(context -> {

                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity jogador;

                    try {
                        jogador = source.getPlayerOrThrow();
                    } catch (Exception e) {
                        return 0;
                    }

                    SteveEntity npc = encontrarSteve(jogador);

                    if (npc == null) {
                        source.sendError(Text.literal(
                            "Nenhum Steve encontrado em 64 blocos."
                        ));
                        return 0;
                    }

                    npc.parar();

                    source.sendFeedback(
                        () -> Text.literal(
                            "Steve parou de seguir!"
                        ),
                        false
                    );

                    return 1;
                }))
        );
    }
}
