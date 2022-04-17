package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import com.mojang.blaze3d.matrix.MatrixStack;

public class Icon extends TweetFramePiece {
    Icon(TweetFrame parent, TweetSummary tweetSummary) {
        super(parent, tweetSummary);
        this.width = 16;
        this.height = 16;
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        this.x = rowLeft;
        this.y = y;

        ImageDataDeliverer icon = this.summary.getUserIconData();
        if (icon.readyToRender()) {
            TwitterForMC2.getInstance().getTextureManager().bindTexture(icon.deliver());
            blit(matrices, this.x, this.y, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        }

        return this.height;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
