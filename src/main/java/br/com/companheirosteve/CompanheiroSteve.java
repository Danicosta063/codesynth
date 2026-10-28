package br.com.companheirosteve;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CompanheiroSteve implements ModInitializer {

    public static final String MOD_ID = "companheirosteve";

    @Override
    public void onInitialize() {
        SteveEntities.registrar();

        System.out.println("[Companheiro Steve] Mod inicializado!");

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                registrarComandos(dispatcher);
            }
        );
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
                            "Este comando so pode ser usado por jogadores."
                        ));
                        return 0;
                    }

                    VillagerEntity npc = new VillagerEntity(
                        EntityType.VILLAGER,
                        jogador.getEntityWorld()
                    );

                    npc.refreshPositionAndAngles(
                        jogador.getX() + 2,
                        jogador.getY(),
                        jogador.getZ() + 2,
                        0,
                        0
                    );

                    npc.setCustomName(
                        Text.literal("Companheiro Steve")
                    );

                    npc.setCustomNameVisible(true);

                    npc.setPersistent();

                    jogador.getEntityWorld().spawnEntity(npc);

                    source.sendFeedback(
                        () -> Text.literal(
                            "Companheiro Steve criado com sucesso!"
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("seguir").executes(context -> {

                    context.getSource().sendFeedback(
                        () -> Text.literal(
                            "Sistema de seguir em desenvolvimento."
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("parar").executes(context -> {

                    context.getSource().sendFeedback(
                        () -> Text.literal(
                            "Sistema de parar em desenvolvimento."
                        ),
                        false
                    );

                    return 1;
                }))
        );
    }
}
