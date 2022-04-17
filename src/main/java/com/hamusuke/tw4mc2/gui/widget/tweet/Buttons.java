package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.twitter.TwitterReplyScreen;
import com.hamusuke.tw4mc2.gui.widget.ChangeableImageButton;
import com.hamusuke.tw4mc2.gui.widget.TwitterButton;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import twitter4j.TwitterException;

import java.util.List;

public class Buttons extends TweetFramePiece {
    protected static final ResourceLocation REPLY = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/reply.png");
    protected static final ResourceLocation RETWEET = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweet.png");
    protected static final ResourceLocation RETWEETED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweeted.png");
    protected static final ResourceLocation FAVORITE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorite.png");
    protected static final ResourceLocation FAVORITED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorited.png");
    protected static final ResourceLocation SHARE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/share.png");

    private final TwitterButton reply;
    private final TwitterButton retweet;
    private final TwitterButton favorite;
    private final TwitterButton share;
    private final List<Button> overlayButtons = Lists.newArrayList();

    public Buttons(TweetFrame parent, TweetSummary summary) {
        super(parent, summary);

        this.reply = new TwitterButton(0, 0, 10, 10, 0, 0, 16, REPLY, 16, 32, 16, 16, p -> {
            this.minecraft.setScreen(new TwitterReplyScreen(parent.twitterScreen, this.summary));
        });
        boolean bl = summary.isRetweeted();
        this.retweet = new TwitterButton(0, 0, 10, 10, 0, 0, bl ? 0 : 16, bl ? RETWEETED : RETWEET, 16, bl ? 16 : 32, 16, 16, p -> {
            if (!summary.getUser().isProtected()) {
                //this.showRetweetButtons();
            }
        });
        this.favorite = new TwitterButton(0, 0, 10, 10, 0, 0, summary.isFavorited() ? 0 : 16, summary.isFavorited() ? FAVORITED : FAVORITE, 16, summary.isFavorited() ? 16 : 32, 16, 16, p -> {
            try {
                ChangeableImageButton changeableImageButton = (ChangeableImageButton) p;

                if (this.summary.isFavorited()) {
                    TwitterForMC2.getInstance().mcTwitter.destroyFavorite(summary.getId());
                    summary.favorite(false);
                    changeableImageButton.setImage(FAVORITE);
                    changeableImageButton.setWhenHovered(16);
                    changeableImageButton.setSize(16, 32);
                } else {
                    TwitterForMC2.getInstance().mcTwitter.createFavorite(summary.getId());
                    summary.favorite(true);
                    changeableImageButton.setImage(FAVORITED);
                    changeableImageButton.setWhenHovered(0);
                    changeableImageButton.setSize(16, 16);
                }
            } catch (TwitterException e) {
                parent.twitterScreen.accept(new TranslationTextComponent("tw.failed.like", e.getErrorMessage()));
            }
        });
        this.share = new TwitterButton(0, 0, 10, 10, 0, 0, 16, SHARE, 16, 32, 16, 16, p -> {
            this.minecraft.keyboardHandler.setClipboard(this.summary.getTweetURL());
            parent.twitterScreen.accept(new TranslationTextComponent("tw.copy.tweeturl.to.clipboard"));
        });
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        return 0;
    }
}
