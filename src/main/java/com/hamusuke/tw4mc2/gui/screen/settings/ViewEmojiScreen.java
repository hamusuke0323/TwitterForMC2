package com.hamusuke.tw4mc2.gui.screen.settings;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.text.emoji.Emoji;
import com.hamusuke.tw4mc2.text.emoji.EmojiManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.TranslationTextComponent;

public class ViewEmojiScreen extends ParentalScreen {
    private ViewEmojiScreen.EmojiList emojiList;

    public ViewEmojiScreen(Screen parent) {
        super(new TranslationTextComponent("tw.view.emoji"), parent);
    }

    @Override
    protected void init() {
        super.init();

        this.addButton(new Button(this.width / 4, this.height - 20, this.width / 2, 20, DialogTexts.GUI_BACK, b -> this.onClose()));

        this.emojiList = new EmojiList(TwitterForMC2.getInstance().getEmojiManager(), this.minecraft, this.width, this.height, 30, this.height - 20, 50);
        this.addWidget(this.emojiList);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.emojiList.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 10, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private class EmojiList extends ExtendedList<EmojiList.EmojiEntry> {
        private EmojiList(EmojiManager emojiManager, Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            emojiManager.getAllEmojis().forEach((hex, emoji) -> this.addEntry(new EmojiEntry(emoji)));
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width - 5;
        }

        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected boolean isSelectedItem(int index) {
            return true;
        }

        @Override
        protected boolean isFocused() {
            return true;
        }

        private class EmojiEntry extends ExtendedList.AbstractListEntry<EmojiEntry> {
            private final Emoji emoji;

            private EmojiEntry(Emoji emoji) {
                this.emoji = emoji;
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
                ViewEmojiScreen.this.font.drawShadow(matrices, "Hexadecimal(Character code): " + this.emoji.getHex(), x, y + (float) height / 2 - 4, 16777215);
                ViewEmojiScreen.this.minecraft.textureManager.bind(this.emoji.getId());
                blit(matrices, x + width - 60, y + 2, 0.0F, 0.0F, 42, 42, 42, 42);
            }
        }
    }
}
