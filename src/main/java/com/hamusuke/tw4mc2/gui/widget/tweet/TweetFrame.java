package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.gui.screen.twitter.AbstractTwitterScreen;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nullable;
import java.util.List;

public class TweetFrame extends AbstractGui implements IGuiEventListener {
    final AbstractTwitterScreen twitterScreen;
    final TweetSummary tweetSummary;
    private final TweetSummary main;
    private final List<TweetFramePiece> pieces = Lists.newArrayList();
    @Nullable
    private final WhoRetweeted retweetedUser;
    private final Icon icon;
    private final NameScreenNameTime nameTime;
    private final MainText mainText;
    private final Photos photos;
    @Nullable
    private final Video video;
    @Nullable
    private final QuotedTweet quotedTweet;
    private final int height;

    public TweetFrame(AbstractTwitterScreen twitterScreen, TweetSummary tweetSummary, int rowWidth) {
        this.twitterScreen = twitterScreen;
        this.tweetSummary = tweetSummary;

        boolean flag = this.tweetSummary.getRetweetedSummary() != null;
        this.main = flag ? this.tweetSummary.getRetweetedSummary() : this.tweetSummary;
        this.retweetedUser = this.register(flag ? new WhoRetweeted(this, this.tweetSummary, rowWidth) : null);
        this.icon = this.register(new Icon(this, this.main));
        this.nameTime = this.register(new NameScreenNameTime(this, this.main));
        this.mainText = this.register(new MainText(this, this.main, rowWidth));
        this.quotedTweet = this.register(this.main.getQuotedTweetSummary() == null ? null : new QuotedTweet(this, this.main.getQuotedTweetSummary(), rowWidth));
        this.photos = this.register(new Photos(this, this.main, rowWidth));
        this.video = this.register(this.main.isIncludeVideo() && !this.main.isVideoURLNull() ? new Video(this, this.main.getVideoURL(), rowWidth) : null);

        MutableInt height = new MutableInt();
        this.pieces.forEach(piece -> height.add(piece.getHeight()));
        this.height = height.intValue();
    }

    @Nullable
    private <T extends TweetFramePiece> T register(@Nullable T piece) {
        if (piece != null) {
            this.pieces.add(piece);
        }

        return piece;
    }

    public void renderFrame(MatrixStack matrices, int itemIndex, int rowTop, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        int y = rowTop;

        if (this.retweetedUser != null) {
            y += this.retweetedUser.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        }

        this.icon.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        y += this.nameTime.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        y += this.mainText.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);

        if (this.video != null) {
            y += this.video.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        } else {
            y += this.photos.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        }
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        boolean bl = false;

        for (TweetFramePiece piece : this.pieces) {
            bl = piece.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
        }

        return bl;
    }

    @Override
    public void mouseMoved(double p_212927_1_, double p_212927_3_) {
        this.pieces.forEach(piece -> piece.mouseMoved(p_212927_1_, p_212927_3_));
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        boolean bl = false;

        for (TweetFramePiece piece : this.pieces) {
            bl = piece.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
        }

        return bl;
    }

    public ImmutableList<TweetFramePiece> getPiecesImmutable() {
        return ImmutableList.copyOf(this.pieces);
    }

    public TweetSummary getMain() {
        return this.main;
    }

    public void removed() {
        this.pieces.forEach(TweetFramePiece::removed);
    }

    public int getHeight() {
        return this.height;
    }
}
