package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class WhoRetweeted extends TweetFramePiece {
    private static final ResourceLocation RETWEET_USER = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweetuser.png");

    WhoRetweeted(TweetFrame parent, TweetSummary retweeted, int rowWidth) {
        super(parent, retweeted);
        this.width = rowWidth - 24;
        this.height = this.parent.twitterScreen.wrapUserNameToWidth(retweeted, this.width).size() * this.font.lineHeight;
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        this.minecraft.textureManager.bind(RETWEET_USER);
        matrices.pushPose();
        matrices.translate(rowLeft + 6, y, 0.0F);
        matrices.scale(0.625F, 0.625F, 0.625F);
        blit(matrices, 0, 0, 0.0F, 0.0F, 16, 16, 16, 16);
        matrices.popPose();
        List<IReorderingProcessor> names = this.parent.twitterScreen.wrapUserNameToWidth(this.summary, rowWidth - 24);
        this.x = rowLeft + 24;
        this.y = y;
        for (int i = 0; i < names.size(); i++) {
            this.parent.twitterScreen.drawWithShadowAndEmoji(matrices, names.get(i), rowLeft + 24, y + i * this.font.lineHeight, 11184810);
        }
        return this.height;
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        return false;
    }
}
