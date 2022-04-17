package com.hamusuke.tw4mc2.gui.screen.settings;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.gui.widget.TextWidget;
import com.hamusuke.tw4mc2.gui.widget.list.WidgetList;
import com.hamusuke.tw4mc2.license.License;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;

import java.util.List;

public class ViewLicenseScreen extends ParentalScreen {
    protected final License license;
    protected List<ITextProperties> lines = Lists.newArrayList();
    protected WidgetList list;

    public ViewLicenseScreen(ITextComponent title, Screen parent, License license) {
        super(title, parent);
        this.license = license;
    }

    @Override
    protected void init() {
        super.init();

        this.lines.clear();
        this.addButton(new Button(this.width / 4, this.height - 20, this.width / 2, 20, DialogTexts.GUI_BACK, b -> this.onClose()));

        this.list = new WidgetList(this.minecraft, this.width, this.height, 20, this.height - 20, 10) {
            @Override
            public int getRowWidth() {
                return ViewLicenseScreen.this.license.getWidth();
            }

            @Override
            protected int getScrollbarPosition() {
                return this.width - 5;
            }
        };

        for (String s : this.license.getLicenseTextList()) {
            this.lines.addAll(this.font.getSplitter().splitLines(s, this.list.getRowWidth(), Style.EMPTY));
        }

        for (int i = 0; i < this.lines.size(); i++) {
            this.list.addEntry(new TextWidget((this.width - this.list.getRowWidth()) / 2, i * this.font.lineHeight, this.list.getRowWidth(), this.font.lineHeight, new StringTextComponent(this.lines.get(i).getString())) {
                @Override
                public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                    ViewLicenseScreen.this.font.drawShadow(matrices, this.getMessage(), this.x, this.y, 16777215);
                }
            });
        }

        this.addWidget(this.list);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
