
package com.danicosta.noobprohackergod;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class RegistroEntidades {

    private RegistroEntidades() {
    }

    public static final EntityType<EntidadeNPC> CONSTRUTOR =
            registrar(
                    "construtor",
                    EntityType.Builder
                            .<EntidadeNPC>of(
                                    EntidadeNPC::new,
                                    MobCategory.MISC
                            )
                            .sized(0.6f, 1.8f)
            );

    private static EntityType<EntidadeNPC> registrar(
            String nome,
            EntityType.Builder<EntidadeNPC> construtor
    ) {
        ResourceKey<EntityType<?>> chave =
                ResourceKey.create(
                        Registries.ENTITY_TYPE,
                        Identifier.fromNamespaceAndPath(
                                NoobProHackerGod.MOD_ID,
                                nome
                        )
                );

        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                chave,
                construtor.build(chave)
        );
    }

    public static void registrarAtributos() {
        FabricDefaultAttributeRegistry.register(
                CONSTRUTOR,
                EntidadeNPC.criarAtributos()
        );
    }

    public static void inicializar() {
        registrarAtributos();

        NoobProHackerGod.LOGGER.info(
                "Tipo de entidade construtor registrado."
        );
    }
}
