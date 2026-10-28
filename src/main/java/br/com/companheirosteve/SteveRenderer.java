package br.com.companheirosteve;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

public class SteveRenderer extends MobEntityRenderer<
        SteveEntity,
        BipedEntityRenderState,
        BipedEntityModel<BipedEntityRenderState>> {

    private static final Identifier TEXTURA =
        Identifier.of(
            "minecraft",
            "textures/entity/player/wide/steve.png"
        );

    public SteveRenderer(EntityRendererFactory.Context context) {
        super(
            context,
            new BipedEntityModel<>(
                context.getPart(EntityModelLayers.ZOMBIE)
            ),
            0.5f
        );
    }

    @Override
    public BipedEntityRenderState createRenderState() {
        return new BipedEntityRenderState();
    }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return TEXTURA;
    }
}
