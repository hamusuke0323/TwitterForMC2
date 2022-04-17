package com.hamusuke.tw4mc2.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScalableImageButton extends Button {
    protected final ResourceLocation texture;
    protected final int u;
    protected final int v;
    protected final int hoveredVOffset;
    protected final int textureWidth;
    protected final int textureHeight;
    protected final float scale;
    protected final int renderWidth;
    protected final int renderHeight;

    public ScalableImageButton(int x, int y, int widgetWidth, int widgetHeight, int renderWidth, int renderHeight, float scale, int u, int v, int hoveredVOffset, ResourceLocation texture, Button.IPressable pressAction) {
        this(x, y, widgetWidth, widgetHeight, renderWidth, renderHeight, scale, u, v, hoveredVOffset, texture, 256, 256, pressAction);
    }

    public ScalableImageButton(int x, int y, int widgetWidth, int widgetHeight, int renderWidth, int renderHeight, float scale, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, Button.IPressable pressAction) {
        this(x, y, widgetWidth, widgetHeight, renderWidth, renderHeight, scale, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, NarratorChatListener.NO_TITLE);
    }

    public ScalableImageButton(int x, int y, int widgetWidth, int widgetHeight, int renderWidth, int renderHeight, float scale, int u, int v, int hoveredVOffset, ResourceLocation texture, int textureWidth, int textureHeight, Button.IPressable pressAction, ITextComponent text) {
        super(x, y, widgetWidth, widgetHeight, text, pressAction);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.hoveredVOffset = hoveredVOffset;
        this.texture = texture;
        this.scale = scale;
        this.renderWidth = renderWidth;
        this.renderHeight = renderHeight;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.pushPose();
        RenderSystem.disableDepthTest();
        int i = this.v;
        if (this.isHovered()) {
            i += this.hoveredVOffset;
        }

        matrices.translate(this.x, this.y, 0.0F);
        matrices.scale(this.scale, this.scale, this.scale);
        blit(matrices, 0, 0, (float) this.u, (float) i, this.renderWidth, this.renderHeight, this.textureWidth, this.textureHeight);
        RenderSystem.enableDepthTest();
        matrices.popPose();
    }
}
