package com.hamusuke.tw4mc2.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class ErrorScreen extends ParentalScreen {
    private final String errorMsg;

    public ErrorScreen(ITextComponent text, @Nullable Screen parent, String errorMsg) {
        super(text, parent);
        this.errorMsg = errorMsg;
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 2;
        this.addButton(new Button(i / 2, this.height - 20, i, 20, DialogTexts.GUI_BACK, b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.parent != null) {
            matrices.pushPose();
            matrices.translate(0.0D, 0.0D, -1.0D);
            this.parent.render(matrices, -1, -1, delta);
            matrices.popPose();
            this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderBackground(matrices);
        }

        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 20, 16777215);
        List<IReorderingProcessor> list = this.font.split(ITextComponent.nullToEmpty(this.errorMsg), this.width / 2);
        for (int i = 0; i < list.size(); i++) {
            this.font.drawShadow(matrices, list.get(i), (float) this.width / 4, 50 + i * this.font.lineHeight, 16777215);
        }
    }
}
