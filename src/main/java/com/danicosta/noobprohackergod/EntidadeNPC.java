
package com.danicosta.noobprohackergod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntidadeNPC extends PathfinderMob {

    public EntidadeNPC(
            EntityType<? extends PathfinderMob> tipo,
            Level mundo
    ) {
        super(tipo, mundo);
    }

    public static AttributeSupplier.Builder criarAtributos() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(
                        Attributes.MOVEMENT_SPEED,
                        ConfiguracaoNPC.VELOCIDADE_MOVIMENTO
                )
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        // Os objetivos de movimentacao serao
        // adicionados quando conectarmos o NPC
        // ao sistema de construcao.
    }
}
