package br.com.vidareal;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VidaReal implements ModInitializer {

    public static final String MOD_ID = "vidareal";
    public static final String VERSION = "0.0.3";

    public static Item CHAVE_PREFEITURA;
    public static Block PREFEITURA;

    private static Identifier id(String nome) {
        return Identifier.fromNamespaceAndPath(MOD_ID, nome);
    }

    private static Item registrarChave() {
        ResourceKey<Item> chave = ResourceKey.create(
            Registries.ITEM, id("chave_prefeitura")
        );

        return Registry.register(
            BuiltInRegistries.ITEM,
            chave,
            new Item(
                new Item.Properties()
                    .setId(chave)
                    .stacksTo(1)
            )
        );
    }

    private static Block registrarPrefeitura() {
        ResourceKey<Block> chaveBloco = ResourceKey.create(
            Registries.BLOCK, id("prefeitura")
        );

        Block bloco = new Block(
            BlockBehaviour.Properties.of()
                .strength(2.0f)
                .setId(chaveBloco)
        );

        Registry.register(
            BuiltInRegistries.BLOCK,
            chaveBloco,
            bloco
        );

        ResourceKey<Item> chaveItem = ResourceKey.create(
            Registries.ITEM, id("prefeitura")
        );

        Registry.register(
            BuiltInRegistries.ITEM,
            chaveItem,
            new BlockItem(
                bloco,
                new Item.Properties()
                    .setId(chaveItem)
                    .useBlockDescriptionPrefix()
            )
        );

        return bloco;
    }

    @Override
    public void onInitialize() {
        CHAVE_PREFEITURA = registrarChave();
        PREFEITURA = registrarPrefeitura();

        System.out.println(
            "[Vida Real] Versao " + VERSION + " iniciada!"
        );
        System.out.println(
            "[Vida Real] Prefeitura registrada!"
        );
    }
}
