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

                .then(
                    literal("criar")
                        .executes(context -> {

                            context.getSource().sendFeedback(
                                () -> Text.literal(
                                    "Companheiro Steve: comando criar recebido!"
                                ),
                                false
                            );

                            return 1;
                        })
                )

                .then(
                    literal("seguir")
                        .executes(context -> {

                            context.getSource().sendFeedback(
                                () -> Text.literal(
                                    "Companheiro Steve: comando seguir recebido!"
                                ),
                                false
                            );

                            return 1;
                        })
                )

                .then(
                    literal("parar")
                        .executes(context -> {

                            context.getSource().sendFeedback(
                                () -> Text.literal(
                                    "Companheiro Steve: comando parar recebido!"
                                ),
                                false
                            );

                            return 1;
                        })
                )
        );
    }
}
