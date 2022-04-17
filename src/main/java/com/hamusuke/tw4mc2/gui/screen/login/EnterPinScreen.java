package com.hamusuke.tw4mc2.gui.screen.login;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public class EnterPinScreen extends Screen {
    private final Consumer<String> callback;
    private TextFieldWidget pin;

    public EnterPinScreen(Consumer<String> callback) {
        super(new TranslationTextComponent("tw.enter.pin"));
        this.callback = callback;
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 3;
        this.pin = new TextFieldWidget(this.font, i, this.height / 2, i, 20, this.pin, NarratorChatListener.NO_TITLE);
        this.addButton(this.pin);

        this.addButton(new Button(i, this.height - 20, i, 20, DialogTexts.GUI_DONE, b -> {
            b.active = false;
            this.pin.setEditable(false);
            this.callback.accept(this.pin.getValue());
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices, 0);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, this.height / 2 - 15, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
