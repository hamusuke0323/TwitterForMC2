package com.hamusuke.tw4mc2.gui.screen.settings;

import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class TwitterSettingsScreen extends ParentalScreen {
    public TwitterSettingsScreen(Screen parent) {
        super(new TranslationTextComponent("tw.settings"), parent);
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.width / 4;
        this.addButton(new Button(j, this.height / 2 - 20, i, 20, new TranslationTextComponent("tw.about.this.mod"), b -> {
            this.minecraft.setScreen(new AboutThisModScreen(this));
        }));

        this.addButton(new Button(j, this.height / 2, i, 20, new TranslationTextComponent("tw.view.emoji"), b -> {
            this.minecraft.setScreen(new ViewEmojiScreen(this));
        }));

        this.addButton(new Button(j, this.height - 20, i, 20, DialogTexts.GUI_BACK, b -> this.onClose()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices, 0);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 10, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
