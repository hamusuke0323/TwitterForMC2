package com.hamusuke.tw4mc2.gui.screen;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public abstract class ClickSpaceToCloseScreen extends ParentalScreen {
    protected ClickSpaceToCloseScreen(ITextComponent title, @Nullable Screen parent) {
        super(title, parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean bl = super.mouseClicked(mouseX, mouseY, button);
        boolean bl2 = false;

        for (IGuiEventListener element : this.children()) {
            if (element instanceof Widget && ((Widget) element).isHovered()) {
                bl2 = true;
                break;
            }
        }

        if (!bl && !bl2) {
            this.onClose();
        }

        return bl;
    }
}
