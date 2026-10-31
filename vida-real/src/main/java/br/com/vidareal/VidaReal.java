package br.com.vidareal;

import java.util.function.Function;

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

    private static Item registrarItem(
            String nome,
            Function<Item.Properties, Item> fabrica) {

        Identifier id = Identifier.fromNamespaceAndPath(
            MOD_ID, nome
        );

        ResourceKey<Item> chave = ResourceKey.create(
            Registries.ITEM, id
        );

        Item.Properties propriedades = new Item.Properties()
            .stacksTo(1)
            .setId(chave);

        Item item = fabrica.apply(propriedades);

        return Registry.register(
            BuiltInRegistries.ITEM,
            chave,
            item
        );
    }

    @Override
    public void onInitialize() {

        CHAVE_PREFEITURA = registrarItem(
            "chave_prefeitura",
            Item::new
        );

        System.out.println(
            "[Vida Real] Versao " + VERSION + " iniciada!"
        );

        System.out.println(
            "[Vida Real] Chave da Prefeitura registrada!"
        );
    }
}
