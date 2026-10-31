package br.com.vidareal;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class VidaReal implements ModInitializer {

    public static final String MOD_ID = "vidareal";
    public static final String VERSION = "0.0.2";

    public static Item CHAVE_PREFEITURA;

    private static Item registrarItem(String nome) {
        Identifier id = Identifier.fromNamespaceAndPath(
            MOD_ID, nome
        );

        ResourceKey<Item> key = ResourceKey.create(
            Registries.ITEM, id
        );

        Item.Properties propriedades = new Item.Properties()
            .setId(key)
            .stacksTo(1);

        Item item = new Item(propriedades);

        return Registry.register(
            BuiltInRegistries.ITEM,
            key,
            item
        );
    }

    @Override
    public void onInitialize() {
        CHAVE_PREFEITURA = registrarItem("chave_prefeitura");

        ItemGroupEvents.modifyEntriesEvent(
            CreativeModeTabs.INGREDIENTS
        ).register(entries -> {
            entries.accept(CHAVE_PREFEITURA);
        });

        System.out.println(
            "[Vida Real] Versao " + VERSION + " iniciada!"
        );
    }
}
