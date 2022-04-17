package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.gui.screen.twitter.TwitterShowStatusScreen;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;

import javax.annotation.Nullable;

public class QuotedTweet extends TweetFramePiece {
    private final Icon icon;
    private final NameScreenNameTime nameTime;
    private final MainText mainText;
    private final Photos photos;
    @Nullable
    private final Video video;

    QuotedTweet(TweetFrame parent, TweetSummary quotedTweetSummary, int rowWidth) {
        super(parent, quotedTweetSummary);

        this.icon = new Icon(parent, quotedTweetSummary);
        this.icon.setSize(10, 10);
        this.nameTime = new NameScreenNameTime(parent, quotedTweetSummary);
        this.mainText = new MainText(parent, quotedTweetSummary, rowWidth);
        this.photos = new Photos(parent, quotedTweetSummary, rowWidth);
        this.video = quotedTweetSummary.isVideoURLNull() ? null : new Video(parent, quotedTweetSummary.getVideoURL(), rowWidth);

        this.height = this.nameTime.getHeight() + this.mainText.getHeight() + (this.video == null ? this.photos.getHeight() : this.video.getHeight());
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        this.icon.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        y += this.nameTime.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        y += this.mainText.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        if (this.video == null) {
            this.photos.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        } else {
            this.video.render(matrices, itemIndex, y, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
        }

        return this.height;
    }

    @Override
    protected void onClick(double x, double y, int button) {
        this.minecraft.setScreen(new TwitterShowStatusScreen(this.parent.twitterScreen, this.summary));
    }
}
