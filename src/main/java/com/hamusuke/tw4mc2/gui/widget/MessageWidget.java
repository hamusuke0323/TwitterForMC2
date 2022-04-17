package com.hamusuke.tw4mc2.gui.widget;

import com.hamusuke.tw4mc2.gui.screen.twitter.AbstractTwitterScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;
import java.util.List;

public class MessageWidget extends Widget {
    private final Minecraft client;
    private final Screen parent;
    @Nullable
    private List<IReorderingProcessor> messageLines;
    private int fade;

    public MessageWidget(Screen parent, Minecraft client, int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width + 12, height + 12, message);
        this.parent = parent;
        this.client = client;
        this.fade = 100;
    }

    public void tick() {
        if (this.fade <= 0) {
            AbstractTwitterScreen.messageWidget = null;
        }

        this.fade--;
    }

    public void init(int width, int height) {
        this.messageLines = this.client.font.split(this.getMessage(), width / 2);
        int messageWidth = AbstractTwitterScreen.getMaxWidth(this.client.font, this.messageLines);
        this.setPosition((width - messageWidth) / 2, height - 20 - this.messageLines.size() * 9, messageWidth, this.messageLines.size() * 9);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.messageLines != null) {
            this.parent.renderToolTip(matrices, this.messageLines, this.x - 12, this.y + 12, this.client.font);
        }
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        Style style = this.getStyleAt(mouseX, mouseY);
        return super.clicked(mouseX, mouseY) && style != null && style.getClickEvent() != null;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.parent.handleComponentClicked(this.getStyleAt(mouseX, mouseY));
    }

    @Nullable
    private Style getStyleAt(double mouseX, double mouseY) {
        if (this.messageLines != null) {
            int i = MathHelper.floor(mouseX) - this.x;
            int j = (MathHelper.floor(mouseY) - this.y) / 9;
            if (i >= 0 && j >= 0 && j < this.messageLines.size()) {
                return this.client.font.getSplitter().componentStyleAtWidth(this.messageLines.get(j), i);
            }
        }

        return null;
    }

    public void setPosition(int x, int y, int width, int height) {
        width += 12;
        height += 12;

        if (x != this.x || y != this.y || width != this.width || height != this.height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
