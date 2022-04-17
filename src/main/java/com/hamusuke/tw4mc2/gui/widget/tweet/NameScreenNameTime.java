package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.text.TweetText;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class NameScreenNameTime extends TweetFramePiece {
    private static final ITextComponent THREE_PERIOD = new StringTextComponent("...").withStyle(TextFormatting.BOLD);
    private static final ITextComponent THREE_PERIOD_GRAY = new StringTextComponent("...").withStyle(TextFormatting.GRAY);

    NameScreenNameTime(TweetFrame parent, TweetSummary tweetSummary) {
        super(parent, tweetSummary);
        this.height = 10;
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        boolean p = this.summary.getUser().isProtected();
        boolean v = this.summary.getUser().isVerified();

        this.x = rowLeft + 24;
        this.y = y;
        this.width = rowWidth - 24;

        int threeBoldWidth = this.font.width(THREE_PERIOD);
        int threeWidth = this.font.width(THREE_PERIOD_GRAY);
        ITextComponent time = new StringTextComponent("・" + this.summary.getDifferenceTime()).withStyle(TextFormatting.GRAY);
        int timeWidth = this.font.width(time);
        ITextComponent screenName = new TweetText(this.summary.getScreenName()).withStyle(TextFormatting.GRAY);
        ITextComponent name = new TweetText(this.summary.getUser().getName()).withStyle(TextFormatting.BOLD);

        int protectedVerifiedWidth = (p ? 10 : 0) + (v ? 10 : 0);
        List<IReorderingProcessor> nameFormatted = this.parent.twitterScreen.wrapLines(name, this.width - protectedVerifiedWidth - timeWidth);
        boolean isOver = nameFormatted.size() > 1;
        List<IReorderingProcessor> nameFormatted2 = isOver ? this.parent.twitterScreen.wrapLines(name, this.width - protectedVerifiedWidth - timeWidth - threeBoldWidth) : nameFormatted;

        IReorderingProcessor formattedName = nameFormatted2.size() == 1 ? nameFormatted2.get(0) : IReorderingProcessor.composite(nameFormatted2.get(0), THREE_PERIOD.getVisualOrderText());
        int formattedNameWidth = this.parent.twitterScreen.getWidthWithEmoji(formattedName);
        int x = this.x;
        this.parent.twitterScreen.drawWithShadowAndEmoji(matrices, formattedName, x, y, 16777215);
        x += formattedNameWidth;
        if (p) {
            x += this.parent.twitterScreen.renderProtected(matrices, x, y);
        }
        if (v) {
            x += this.parent.twitterScreen.renderVerified(matrices, x, y);
        }

        List<IReorderingProcessor> screenNameFormatted = this.parent.twitterScreen.wrapLines(screenName, this.width - formattedNameWidth - protectedVerifiedWidth - timeWidth - threeWidth);
        if (!isOver) {
            IReorderingProcessor text = screenNameFormatted.size() == 1 ? screenNameFormatted.get(0) : IReorderingProcessor.composite(screenNameFormatted.get(0), THREE_PERIOD_GRAY.getVisualOrderText());
            this.parent.twitterScreen.drawWithShadowAndEmoji(matrices, text, x, y, 11184810);
            x += this.parent.twitterScreen.getWidthWithEmoji(text);
        }
        this.parent.twitterScreen.drawWithShadowAndEmoji(matrices, time, x, y, 11184810);

        return this.height;
    }
}
