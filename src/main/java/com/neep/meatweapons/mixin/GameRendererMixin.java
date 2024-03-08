package com.neep.meatweapons.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
//    @Shadow @Final private Camera camera;
//
//    @Shadow @Final private MinecraftClient client;
//
//    @Inject(method = "renderWorld", at = @At("HEAD"))
//    void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci)
//    {
//        MWKeys.playerPitch = client.player.getPitch();
//        MWKeys.playerYaw = client.player.getYaw();
//    }
}
