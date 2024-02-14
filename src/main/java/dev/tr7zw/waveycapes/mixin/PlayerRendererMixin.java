package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.renderlayers.CustomCapeRenderLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
// spotless:off
//#if MC < 11700
//$$import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//#else
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//#endif
//spotless:on

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    // spotless:off
    //#if MC >= 11700
    public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
    }
    //#else
    //$$public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher) {
    //$$    super(entityRenderDispatcher, null, 0);
    //$$}
    //#endif
    //spotless:on

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        addLayer(new CustomCapeRenderLayer(this));
    }

}
