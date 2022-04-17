package com.hamusuke.tw4mc2.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public abstract class ParentalScreen extends Screen {
    @Nullable
    protected final Screen parent;

    protected ParentalScreen(ITextComponent title, @Nullable Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.parent != null) {
            this.parent.resize(this.minecraft, this.width, this.height);
        }

        super.init();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
