package com.hamusuke.tw4mc2.gui.screen.settings;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.gui.widget.TextWidget;
import com.hamusuke.tw4mc2.gui.widget.list.WidgetList;
import com.hamusuke.tw4mc2.license.License;
import com.hamusuke.tw4mc2.license.LicenseManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.List;

public class AboutThisModScreen extends ParentalScreen {
    private WidgetList list;

    public AboutThisModScreen(Screen parent) {
        super(new TranslationTextComponent("tw.about.this.mod"), parent);
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.width / 4;

        this.addButton(new Button(j, this.height - 20, i, 20, DialogTexts.GUI_BACK, b -> this.onClose()));

        this.list = new WidgetList(this.minecraft, this.width, this.height, 20, this.height - 20, 20) {
            @Override
            public int getRowWidth() {
                return i;
            }

            @Override
            protected int getScrollbarPosition() {
                return this.width - 5;
            }
        };

        ModList.get().getModContainerById(TwitterForMC2.MOD_ID).ifPresent(mod -> {
            IModInfo data = mod.getModInfo();
            this.list.addEntry(new TextWidget(j, 0, i, 20, new TranslationTextComponent("tw.mod.id", data.getModId())));
            this.list.addEntry(new TextWidget(j, 30, i, 20, new TranslationTextComponent("tw.mod.name", data.getDisplayName())));
            this.list.addEntry(new TextWidget(j, 60, i, 20, new TranslationTextComponent("tw.mod.version", data.getVersion())));
        });

        this.list.addEntry(new TextWidget(j, 90, i, 20, new TranslationTextComponent("tw.open.source.license")));

        List<License> licenses = LicenseManager.getLicenseList();
        for (int index = 0; index < licenses.size(); index++) {
            License license = licenses.get(index);
            this.list.addEntry(new Button(j, 120 + index * 30, i, 20, new TranslationTextComponent(license.getTranslationKey()), b -> {
                this.minecraft.setScreen(new ViewLicenseScreen(license.getTranslationText(), this, license));
            }));
        }

        this.addWidget(this.list);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices, 0);
        this.list.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
