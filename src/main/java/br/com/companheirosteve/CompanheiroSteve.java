package br.com.companheirosteve;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CompanheiroSteve implements ModInitializer {

    public static final String MOD_ID = "companheirosteve";

    @Override
    public void onInitialize() {

        SteveEntities.registrar();

        System.out.println(
            "[Companheiro Steve] Mod inicializado!"
        );

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

                    SteveEntity npc = SteveEntities.STEVE.create(
                        jogador.getEntityWorld(),
                        net.minecraft.entity.SpawnReason.COMMAND
                    );

                    if (npc == null) {
                        source.sendError(Text.literal(
                            "Nao foi possivel criar o Steve."
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

                    boolean criado =
                        jogador.getEntityWorld().spawnEntity(npc);

                    if (!criado) {
                        source.sendError(Text.literal(
                            "Falha ao adicionar Steve ao mundo."
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
