package com.hamusuke.tw4mc2.mixin;

import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "close", at = @At("HEAD"))
    private void close(CallbackInfo ci) {
        ImageDataDeliverer.shutdown();
    }
}
