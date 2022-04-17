package com.hamusuke.tw4mc2.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ChangeableImageButton extends Button {
    protected ResourceLocation resourceLocation;
    protected final int xTexStart;
    protected final int yTexStart;
    protected int yDiffText;
    protected int textureSizeX;
    protected int textureSizeY;

    public ChangeableImageButton(int x, int y, int width, int height, int u, int v, int whenHovered, ResourceLocation image, ITextComponent msg, Button.IPressable iPressable) {
        this(x, y, width, height, u, v, whenHovered, image, 256, 256, msg, iPressable);
    }

    public ChangeableImageButton(int x, int y, int width, int height, int u, int v, int whenHovered, ResourceLocation image, int sizex, int sizey, Button.IPressable iPressable) {
        this(x, y, width, height, u, v, whenHovered, image, sizex, sizey, NarratorChatListener.NO_TITLE, iPressable);
    }

    public ChangeableImageButton(int x, int y, int width, int height, int u, int v, int whenHovered, ResourceLocation image, int sizex, int sizey, ITextComponent msg, Button.IPressable iPressable) {
        super(x, y, width, height, msg, iPressable);
        this.textureSizeX = sizex;
        this.textureSizeY = sizey;
        this.xTexStart = u;
        this.yTexStart = v;
        this.yDiffText = whenHovered;
        this.resourceLocation = image;
    }

    public void setPosition(int xIn, int yIn) {
        this.x = xIn;
        this.y = yIn;
    }

    public void setSize(int x, int y) {
        this.textureSizeX = x;
        this.textureSizeY = y;
    }

    public void setImage(ResourceLocation image) {
        this.resourceLocation = image;
    }

    public void setWhenHovered(int i) {
        this.yDiffText = i;
    }

    @Override
    public void renderButton(MatrixStack matrices, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft.getInstance().textureManager.bind(this.resourceLocation);
        RenderSystem.disableDepthTest();
        int i = this.yTexStart;

        if (this.isHovered()) {
            i += this.yDiffText;
        }

        blit(matrices, this.x, this.y, (float) this.xTexStart, (float) i, this.width, this.height, this.textureSizeX, this.textureSizeY);
        RenderSystem.enableDepthTest();
    }
}
