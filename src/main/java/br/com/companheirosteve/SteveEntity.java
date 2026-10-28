package br.com.companheirosteve;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class SteveEntity extends PathAwareEntity {

    private UUID dono;
    private boolean seguindo = false;

    public SteveEntity(
        EntityType<? extends PathAwareEntity> type,
        World world
    ) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder criarAtributos() {

        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.MAX_HEALTH, 20.0)
            .add(EntityAttributes.MOVEMENT_SPEED, 0.30)
            .add(EntityAttributes.ATTACK_DAMAGE, 1.0)
            .add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    public void definirDono(ServerPlayerEntity jogador) {
        this.dono = jogador.getUuid();
    }

    public UUID obterDono() {
        return dono;
    }

    public void seguir() {
        seguindo = true;
    }

    public void parar() {
        seguindo = false;
        getNavigation().stop();
    }

    public boolean estaSeguindo() {
        return seguindo;
    }

    @Override
    public void tick() {
        super.tick();

        if (getEntityWorld().isClient()) {
            return;
        }

        if (!seguindo || dono == null) {
            return;
        }

        if (!(getEntityWorld()
            instanceof net.minecraft.server.world.ServerWorld mundo)) {
            return;
        }

        ServerPlayerEntity jogador =
            mundo.getServer()
                .getPlayerManager()
                .getPlayer(dono);

        if (jogador == null) {
            getNavigation().stop();
            return;
        }

        if (jogador.getEntityWorld() != getEntityWorld()) {
            getNavigation().stop();
            return;
        }

        double distancia = squaredDistanceTo(jogador);

        if (distancia > 9.0) {
            getNavigation().startMovingTo(
                jogador,
                1.2
            );
        } else {
            getNavigation().stop();
        }
    }
}
