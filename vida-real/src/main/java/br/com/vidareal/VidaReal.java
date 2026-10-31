package br.com.vidareal;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class VidaReal implements ModInitializer {

    public static final String MOD_ID = "vidareal";
    public static final String VERSION = "0.0.2";

    public static Item CHAVE_PREFEITURA;

    @Override
    public void onInitialize() {
        Identifier id = Identifier.fromNamespaceAndPath(
            MOD_ID, "chave_prefeitura"
        );

        ResourceKey<Item> key = ResourceKey.create(
            Registries.ITEM, id
        );

        CHAVE_PREFEITURA = Registry.register(
            BuiltInRegistries.ITEM,
            key,
            new Item(new Item.Properties().setId(key).stacksTo(1))
        );

        System.out.println("[Vida Real] Versao " + VERSION + " iniciada!");
        System.out.println("[Vida Real] Chave da Prefeitura registrada!");
    }
}
