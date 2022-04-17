package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.twitter.TwitterReplyScreen;
import com.hamusuke.tw4mc2.gui.widget.ChangeableImageButton;
import com.hamusuke.tw4mc2.gui.widget.FunctionalButtonWidget;
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
    private final Button ret$retweet;
    private final Button ret$quote;
    private final TwitterButton favorite;
    private final TwitterButton share;
    private final List<Button> twButtons = Lists.newArrayList();
    private final List<Button> overlayButtons = Lists.newArrayList();

    public Buttons(TweetFrame parent, TweetSummary summary, int rowWidth) {
        super(parent, summary);

        this.reply = this.add(new TwitterButton(0, 0, 10, 10, 0, 0, 16, REPLY, 16, 32, 16, 16, p -> {
            this.minecraft.setScreen(new TwitterReplyScreen(parent.twitterScreen, this.summary));
        }));
        boolean bl = summary.isRetweeted();
        this.retweet = this.add(new TwitterButton(0, 0, 10, 10, 0, 0, bl ? 0 : 16, bl ? RETWEETED : RETWEET, 16, bl ? 16 : 32, 16, 16, p -> {
            if (!summary.getUser().isProtected()) {
                this.showRetweetButtons();
            }
        }));
        this.ret$retweet = this.addOverlayBtn(new FunctionalButtonWidget(0, 0, rowWidth / 2, 20, bl ? new TranslationTextComponent("tw.unretweet") : new TranslationTextComponent("tw.retweet"), button -> {
            this.hideRetweetButtons();
            try {
                if (this.summary.isRetweeted()) {
                    TwitterForMC2.getInstance().mcTwitter.unRetweetStatus(this.summary.getId());
                    this.summary.retweet(false);
                    button.setMessage(new TranslationTextComponent("tw.retweet"));
                    this.retweet.setImage(RETWEET);
                    this.retweet.setWhenHovered(16);
                    this.retweet.setSize(16, 32);
                } else {
                    TwitterForMC2.getInstance().mcTwitter.retweetStatus(this.summary.getId());
                    this.summary.retweet(true);
                    button.setMessage(new TranslationTextComponent("tw.unretweet"));
                    this.retweet.setImage(RETWEETED);
                    this.retweet.setWhenHovered(0);
                    this.retweet.setSize(16, 16);
                }
            } catch (TwitterException e) {
                parent.twitterScreen.accept(new TranslationTextComponent("tw.failed.retweet", e.getErrorMessage()));
            }
        }, integer -> integer + 10));
        this.ret$quote = this.addOverlayBtn(new FunctionalButtonWidget(0, 0, rowWidth / 2, 20, new TranslationTextComponent("tw.quote.tweet"), button -> {
            this.hideRetweetButtons();
        }, integer -> integer + 30));
        this.favorite = this.add(new TwitterButton(0, 0, 10, 10, 0, 0, summary.isFavorited() ? 0 : 16, summary.isFavorited() ? FAVORITED : FAVORITE, 16, summary.isFavorited() ? 16 : 32, 16, 16, p -> {
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
        }));
        this.share = this.add(new TwitterButton(0, 0, 10, 10, 0, 0, 16, SHARE, 16, 32, 16, 16, p -> {
            this.minecraft.keyboardHandler.setClipboard(this.summary.getTweetURL());
            parent.twitterScreen.accept(new TranslationTextComponent("tw.copy.tweeturl.to.clipboard"));
        }));
        this.hideAllOverlayButtons();
        this.height = 20;
    }

    private <T extends Button> T add(T t) {
        this.twButtons.add(t);
        return t;
    }

    private <T extends Button> T addOverlayBtn(T t) {
        this.overlayButtons.add(t);
        return t;
    }

    protected void hideAllOverlayButtons() {
        this.overlayButtons.forEach(button -> button.active = button.visible = false);
    }

    private void hideOrShowRetweetButtons(boolean flag) {
        if (this.ret$retweet != null && this.ret$quote != null) {
            this.ret$retweet.active = this.ret$quote.active = this.ret$retweet.visible = this.ret$quote.visible = flag;
        }
    }

    private void hideRetweetButtons() {
        this.hideOrShowRetweetButtons(false);
    }

    private void showRetweetButtons() {
        this.hideOrShowRetweetButtons(true);
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        int i = (rowWidth - 64) / 3;
        rowLeft += 24;
        this.reply.x = rowLeft;
        this.retweet.x = rowLeft + i;
        this.favorite.x = rowLeft + i * 2;
        this.share.x = rowLeft + i * 3;

        this.twButtons.forEach(button -> {
            button.y = y;
            button.render(matrices, mouseX, mouseY, delta);
        });
        this.overlayButtons.forEach(button -> {
            if (button instanceof FunctionalButtonWidget) {
                FunctionalButtonWidget widget = (FunctionalButtonWidget) button;
                widget.y = widget.yFunction.apply(y);
            } else {
                button.y = y;
            }

            button.render(matrices, mouseX, mouseY, delta);
        });

        return this.height;
    }

    @Override
    protected void onClick(double x, double y, int button) {
        this.twButtons.forEach(b -> b.mouseClicked(x, y, button));
        this.overlayButtons.forEach(b -> b.mouseClicked(x, y, button));
    }
}
