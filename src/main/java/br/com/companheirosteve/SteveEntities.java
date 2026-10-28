package br.com.companheirosteve;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.util.Identifier;

public class SteveEntities {

    private static final Identifier STEVE_ID =
        Identifier.of("companheirosteve", "steve");

    private static final RegistryKey<EntityType<?>> STEVE_KEY =
        RegistryKey.of(
            RegistryKeys.ENTITY_TYPE,
            STEVE_ID
        );

    public static final EntityType<SteveEntity> STEVE =
        Registry.register(
            Registries.ENTITY_TYPE,
            STEVE_ID,
            EntityType.Builder.create(
                SteveEntity::new,
                SpawnGroup.CREATURE
            )
            .dimensions(0.6f, 1.8f)
            .maxTrackingRange(10)
            .build(STEVE_KEY)
        );

    public static void registrar() {

        FabricDefaultAttributeRegistry.register(
            STEVE,
            SteveEntity.criarAtributos()
        );

        System.out.println(
            "[Companheiro Steve] Entidade registrada!"
        );
    }
}
