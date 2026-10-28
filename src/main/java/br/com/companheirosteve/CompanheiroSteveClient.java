package br.com.companheirosteve;

import net.fabricmc.api.ClientModInitializer;

public class CompanheiroSteveClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        System.out.println(
            "[Companheiro Steve] Sistema visual inicializado!"
        );

    }
}
