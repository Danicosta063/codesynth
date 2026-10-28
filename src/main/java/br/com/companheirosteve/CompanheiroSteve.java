package br.com.companheirosteve;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CompanheiroSteve implements ModInitializer {

    public static final String MOD_ID = "companheirosteve";

    @Override
    public void onInitialize() {

        System.out.println("[Companheiro Steve] Inicializado!");

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

                    if (source.getPlayer() == null) {
                        source.sendError(Text.literal(
                            "Este comando precisa ser usado por um jogador."
                        ));
                        return 0;
                    }

                    String comando =
                        "summon minecraft:villager ~ ~ ~ " +
                        "{CustomName:'{\"text\":\"Companheiro Steve\"}'," +
                        "CustomNameVisible:1b," +
                        "PersistenceRequired:1b," +
                        "NoAI:1b}";

                    source.getServer()
                        .getCommandManager()
                        .executeWithPrefix(source, comando);

                    source.sendFeedback(
                        () -> Text.literal(
                            "Protótipo do companheiro criado!"
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("seguir").executes(context -> {

                    context.getSource().sendFeedback(
                        () -> Text.literal(
                            "Sistema de seguir ainda em desenvolvimento."
                        ),
                        false
                    );

                    return 1;
                }))

                .then(literal("parar").executes(context -> {

                    context.getSource().sendFeedback(
                        () -> Text.literal(
                            "Sistema de parar ainda em desenvolvimento."
                        ),
                        false
                    );

                    return 1;
                }))
        );
    }
}
