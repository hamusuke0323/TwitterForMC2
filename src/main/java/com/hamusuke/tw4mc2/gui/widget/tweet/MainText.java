package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.text.TweetText;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.IReorderingProcessor;

import java.util.List;

public class MainText extends TweetFramePiece {
    private final List<IReorderingProcessor> texts;

    MainText(TweetFrame parent, TweetSummary tweetSummary, int rowWidth) {
        super(parent, tweetSummary);
        this.texts = parent.twitterScreen.wrapLines(new TweetText(tweetSummary.getText()), rowWidth - 25);
        this.height = this.texts.size() * this.font.lineHeight;
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        this.x = rowLeft + 24;
        this.y = y;
        this.width = rowWidth - 25;

        for (int i = 0; i < this.texts.size(); i++) {
            this.parent.twitterScreen.drawWithShadowAndEmoji(matrices, this.texts.get(i), (float) this.x, (float) (this.y + i * this.font.lineHeight), 16777215);
        }

        return this.height;
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        return false;
    }
}
