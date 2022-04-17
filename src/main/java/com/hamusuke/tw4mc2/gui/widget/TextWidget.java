package com.hamusuke.tw4mc2.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

public class TextWidget extends Widget {
    public TextWidget(int x, int y, int width, int height, ITextComponent msg) {
        super(x, y, width, height, msg);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        drawCenteredString(matrices, client.font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 10) / 2, 16777215);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
}
