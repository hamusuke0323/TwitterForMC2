package com.hamusuke.tw4mc2.gui.screen.twitter;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.ClickSpaceToCloseScreen;
import com.hamusuke.tw4mc2.gui.widget.TwitterTweetFieldWidget;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.utils.TwitterThread;
import com.hamusuke.tw4mc2.utils.TwitterUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.TwitterException;
import twitter4j.util.CharacterUtil;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class TwitterReplyScreen extends ClickSpaceToCloseScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TweetSummary replyTo;
    private TwitterTweetFieldWidget tweetText;
    private Button back;
    private Button tweet;

    public TwitterReplyScreen(@Nullable Screen parent, TweetSummary tweetSummary) {
        super(new TranslationTextComponent("tw.reply.to", tweetSummary.getScreenName()), parent);
        this.replyTo = tweetSummary;
    }

    @Override
    public void tick() {
        this.tweetText.tick();
        this.tweet.active = !this.tweetText.getText().isEmpty();

        super.tick();
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 4;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.tweetText = new TwitterTweetFieldWidget(this.font, i, this.height / 4, i * 2, this.height / 2, NarratorChatListener.NO_TITLE);
        this.tweetText.setEditableColor(-1);
        this.tweetText.setMaxLength(CharacterUtil.MAX_TWEET_LENGTH);

        this.back = this.addButton(new Button(i, (this.height / 4 + this.height / 2) + 10, i, 20, DialogTexts.GUI_BACK, a -> this.onClose()));

        this.tweet = this.addButton(new Button(i * 2, (this.height / 4 + this.height / 2) + 10, i, 20, new TranslationTextComponent("tweet"), b -> {
            this.tweet.active = this.back.active = false;
            CompletableFuture.runAsync(() -> {
                try {
                    TweetSummary tweetSummary = new TweetSummary(TwitterForMC2.getInstance().mcTwitter.updateStatus(TwitterUtil.createReplyTweet(this.tweetText.getText(), this.replyTo.getStatus())));
                    TwitterForMC2.getInstance().tweets.add(tweetSummary.getStatus());
                    TwitterForMC2.getInstance().tweetSummaries.add(tweetSummary);
                    this.accept(new TranslationTextComponent("sent.tweet", new TranslationTextComponent("sent.tweet.view").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, AbstractTwitterScreen.PROTOCOL + "://" + AbstractTwitterScreen.HostType.SHOW_STATUS.getHostName() + "/" + tweetSummary.getId())))));
                } catch (TwitterException e) {
                    LOGGER.error("Error occurred while sending tweet", e);
                    this.accept(new TranslationTextComponent("failed.send.tweet", e.getErrorMessage()));
                }
            }, Executors.newCachedThreadPool(TwitterThread::new)).whenComplete((unused, throwable) -> this.onClose());
        }));

        this.addWidget(this.tweetText);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return this.back.active;
    }

    private void accept(ITextComponent msg) {
        if (this.parent instanceof AbstractTwitterScreen) {
            ((AbstractTwitterScreen) this.parent).accept(msg);
        }
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.tweetText.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.tweetText.setText(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void render(MatrixStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
        if (this.parent != null) {
            matrices.pushPose();
            matrices.translate(0.0D, 0.0D, -1.0D);
            this.parent.render(matrices, -1, -1, p_render_3_);
            matrices.popPose();
        }
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        RenderSystem.disableBlend();
        this.tweetText.render(matrices, p_render_1_, p_render_2_, p_render_3_);
        this.font.drawShadow(matrices, this.getTitle(), this.tweetText.x, this.tweetText.y - 10, 16777215);
        super.render(matrices, p_render_1_, p_render_2_, p_render_3_);
    }
}
