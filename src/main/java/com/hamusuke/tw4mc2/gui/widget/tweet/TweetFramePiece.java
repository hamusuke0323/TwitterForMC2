package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.SoundEvents;

public abstract class TweetFramePiece extends AbstractGui implements IGuiEventListener {
    protected final Minecraft minecraft = Minecraft.getInstance();
    protected final FontRenderer font = this.minecraft.font;
    protected final TweetFrame parent;
    protected final TweetSummary summary;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected TweetFramePiece(TweetFrame parent) {
        this(parent, parent.tweetSummary);
    }

    protected TweetFramePiece(TweetFrame parent, TweetSummary summary) {
        this.parent = parent;
        this.summary = summary;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        return 0;
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        if (p_231044_1_ >= (double) this.x && p_231044_3_ >= (double) this.y && p_231044_1_ < (double) (this.x + this.width) && p_231044_3_ < (double) (this.y + this.height)) {
            this.playDownSound();
            this.onClick(p_231044_1_, p_231044_3_, p_231044_5_);
            return true;
        }

        return false;
    }

    protected void onClick(double x, double y, int button) {
    }

    public void playDownSound() {
        this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public void removed() {
    }

    public int getHeight() {
        return this.height;
    }
}
