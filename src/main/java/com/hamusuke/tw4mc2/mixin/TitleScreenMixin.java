package com.hamusuke.tw4mc2.mixin;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.widget.ScalableImageButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MainMenuScreen.class)
public class TitleScreenMixin extends Screen {
    private static final ResourceLocation TWITTER_ICON = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/twbtn.png");

    private TitleScreenMixin() {
        super(NarratorChatListener.NO_TITLE);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo info) {
        if (!TwitterForMC2.getInstance().twitterScreen.isInitialized()) {
            TwitterForMC2.getInstance().twitterScreen.init(this.minecraft, this.width, this.height);
        }

        this.addButton(new ScalableImageButton(this.width / 2 + 104, this.height / 4 + 48, 20, 20, 40, 40, 0.5F, 0, 0, 40, TWITTER_ICON, 40, 80, (b) -> {
            TwitterForMC2.getInstance().twitterScreen.setParentScreen(this);
            this.minecraft.setScreen(TwitterForMC2.getInstance().twitterScreen);
        }, new StringTextComponent("Twitter")));
    }

    @Inject(cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/MainMenuScreen;blit(Lcom/mojang/blaze3d/matrix/MatrixStack;IIFFIIII)V", shift = At.Shift.AFTER), method = "render")
    private void render(MatrixStack matrices, int mouseX, int mouseY, float tickDelta, CallbackInfo info) {
        if (this.minecraft.screen != this) {
            info.cancel();
        }
    }
}
