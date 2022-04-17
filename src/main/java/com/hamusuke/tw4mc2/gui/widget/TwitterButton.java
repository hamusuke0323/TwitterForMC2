package com.hamusuke.tw4mc2.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class TwitterButton extends ChangeableImageButton {
    private final int sizeX;
    private final int sizeY;

    public TwitterButton(int x, int y, int width, int height, int u, int v, int whenHovered, ResourceLocation image, int sizeX, int sizeY, int renderSizeX, int renderSizeY, Button.IPressable iPressable) {
        super(x, y, width, height, u, v, whenHovered, image, sizeX, sizeY, iPressable);
        this.sizeX = renderSizeX;
        this.sizeY = renderSizeY;
    }

    @Override
    public void renderButton(MatrixStack matrices, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft.getInstance().textureManager.bind(this.resourceLocation);
        matrices.pushPose();
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;

        if (this.isHovered()) {
            i += this.yDiffText;
        }

        matrices.translate(this.x, this.y, 0.0F);
        matrices.scale(0.625F, 0.625F, 0.625F);
        blit(matrices, 0, 0, (float) this.xTexStart, (float) i, this.sizeX, this.sizeY, this.textureSizeX, this.textureSizeY);
        RenderSystem.enableDepthTest();
        matrices.popPose();
    }
}
