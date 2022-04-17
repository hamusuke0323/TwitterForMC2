package com.hamusuke.tw4mc2.gui.screen.twitter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.DownloadTwitterVideoScreen;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.gui.screen.ReturnableGame;
import com.hamusuke.tw4mc2.gui.widget.FunctionalButtonWidget;
import com.hamusuke.tw4mc2.gui.widget.MessageWidget;
import com.hamusuke.tw4mc2.gui.widget.TwitterButton;
import com.hamusuke.tw4mc2.gui.widget.list.AbstractTwitterTweetList;
import com.hamusuke.tw4mc2.gui.widget.list.ExtendedTwitterTweetList;
import com.hamusuke.tw4mc2.gui.widget.tweet.TweetFrame;
import com.hamusuke.tw4mc2.invoker.TextRendererInvoker;
import com.hamusuke.tw4mc2.text.TweetText;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.User;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTwitterScreen extends ParentalScreen implements ReturnableGame {
    protected static final String PROTOCOL = TwitterForMC2.MOD_ID;

    protected static final ResourceLocation PROTECTED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/protected.png");
    protected static final ResourceLocation VERIFIED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/verified.png");
    protected static final ResourceLocation REPLY = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/reply.png");
    protected static final ResourceLocation RETWEET = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweet.png");
    protected static final ResourceLocation RETWEETED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweeted.png");

    protected static final ResourceLocation FAVORITE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorite.png");
    protected static final ResourceLocation FAVORITED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorited.png");
    protected static final ResourceLocation SHARE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/share.png");

    protected static final ITextComponent FOLLOW = new TranslationTextComponent("tw.follow").withStyle(TextFormatting.GRAY);
    protected static final ITextComponent FOLLOWER = new TranslationTextComponent("tw.follower").withStyle(TextFormatting.GRAY);
    protected static final ITextComponent THREE_PERIOD = new StringTextComponent("...").withStyle(TextFormatting.BOLD);
    protected static final ITextComponent THREE_PERIOD_GRAY = new StringTextComponent("...").withStyle(TextFormatting.GRAY);
    private static final Logger LOGGER = LogManager.getLogger();

    protected final List<Widget> renderLaterButtons = Lists.newArrayList();
    @Nullable
    protected AbstractTwitterScreen.TweetList list;
    @Nullable
    Screen previousScreen;
    @Nullable
    public static MessageWidget messageWidget;

    protected AbstractTwitterScreen(ITextComponent title, @Nullable Screen parent) {
        super(title, parent);
    }

    protected static void renderMessage(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        getMessageWidget().ifPresent(messageWidget -> messageWidget.render(matrices, mouseX, mouseY, delta));
    }

    public static int getMaxWidth(FontRenderer textRenderer, List<IReorderingProcessor> messageLines) {
        MutableInt mutableInt = new MutableInt();
        messageLines.forEach(orderedText -> mutableInt.setValue(Math.max(mutableInt.getValue(), textRenderer.width(orderedText))));
        return mutableInt.getValue();
    }

    protected <T extends Widget> T addRenderLaterButton(T button) {
        this.renderLaterButtons.add(button);
        this.addWidget(button);
        return button;
    }

    public void renderButtonLater(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        for (Widget abstractButtonWidget : this.renderLaterButtons) {
            abstractButtonWidget.render(matrices, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public void init(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
        this.renderLaterButtons.clear();
        if (this.list != null) {
            this.list.clearEntries();
        }
        super.init(p_231158_1_, p_231158_2_, p_231158_3_);
    }

    public static Optional<MessageWidget> getMessageWidget() {
        return Optional.ofNullable(messageWidget);
    }

    @Override
    public void tick() {
        if (this.list != null) {
            this.list.tick();
        }

        getMessageWidget().ifPresent(MessageWidget::tick);
        super.tick();
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - this.width / 4, this.height - 20, this.width / 2, 20, new TranslationTextComponent("menu.returnToGame"), button -> this.returnToGame()));
        getMessageWidget().ifPresent(messageWidget -> messageWidget.init(this.width, this.height));
        super.init();
    }

    public void accept(ITextComponent text) {
        List<IReorderingProcessor> messageLines = this.font.split(text, this.width / 2);
        if (messageLines.size() > 0) {
            int width = getMaxWidth(this.font, messageLines);
            messageWidget = new MessageWidget(this, this.minecraft, (this.width - width) / 2, this.height - 20 - messageLines.size() * this.font.lineHeight, width, messageLines.size() * 9, text);
            messageWidget.init(this.width, this.height);
        }
    }

    @Override
    public void returnToGame() {
        TwitterForMC2.getInstance().twitterScreen.previousScreen = this;
        this.minecraft.setScreen(null);
    }

    protected final Optional<Screen> getPreviousScreen() {
        Optional<Screen> screen = Optional.ofNullable(this.previousScreen);
        this.previousScreen = null;
        return screen;
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (this.list != null && this.list.hoveringEntry != null && this.list.hoveringEntry.mayClickIcon(p_mouseClicked_1_, p_mouseClicked_3_)) {
            this.displayTwitterUser(this, this.list.hoveringEntry.frame.getMain().getUser());
            return true;
        } else if (this.list != null && !this.list.isHovering) {
            boolean bl = false;
            if (messageWidget != null) {
                bl = messageWidget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }

            if (!bl) {
                bl = super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }

            return bl;
        }

        return false;
    }

    public boolean renderTwitterUser(MatrixStack matrices, TweetSummary summary, int x, int y, int mouseX, int mouseY) {
        User user = summary.getUser();
        ImageDataDeliverer icon = summary.getUserIconData();
        List<IReorderingProcessor> desc = this.wrapLines(new TweetText(user.getDescription()), Math.min(this.width / 2, 150));
        ITextComponent follow = new StringTextComponent(user.getFriendsCount() + "").withStyle(TextFormatting.BOLD);
        ITextComponent space = ITextComponent.nullToEmpty(" ");
        ITextComponent follower = new StringTextComponent(user.getFollowersCount() + "").withStyle(TextFormatting.BOLD);
        List<IReorderingProcessor> ff = this.wrapLines(ITextProperties.composite(follow, space, FOLLOW, ITextComponent.nullToEmpty("  "), follower, space, FOLLOWER), 150);

        RenderSystem.disableDepthTest();
        int i = 0;

        for (IReorderingProcessor s : desc) {
            int j = this.getWidthWithEmoji(s);
            if (j > i) {
                i = j;
            }
        }

        for (IReorderingProcessor s1 : ff) {
            int j2 = this.font.width(s1);
            if (j2 > i) {
                i = j2;
            }
        }

        int i2 = y;
        int k = 0;
        k += icon.readyToRender() ? 22 : 0;
        k += user.getName().isEmpty() ? 0 : 10;
        k += user.getScreenName().isEmpty() ? 0 : 10;
        k += 4 + (desc.size() * (this.font.lineHeight + 1)) + 4;
        k += ff.size() == 1 ? 10 : 20 + 2;

        if (i2 + k + 6 > this.height - 20) {
            i2 = this.height - 20 - k - 6;
        }

        this.fillGradient(matrices, x - 3, i2 - 4, x + i + 3, i2 - 3, -267386864, -267386864);
        this.fillGradient(matrices, x - 3, i2 + k + 3, x + i + 3, i2 + k + 4, -267386864, -267386864);
        this.fillGradient(matrices, x - 3, i2 - 3, x + i + 3, i2 + k + 3, -267386864, -267386864);
        this.fillGradient(matrices, x - 4, i2 - 3, x - 3, i2 + k + 3, -267386864, -267386864);
        this.fillGradient(matrices, x + i + 3, i2 - 3, x + i + 4, i2 + k + 3, -267386864, -267386864);
        this.fillGradient(matrices, x - 3, i2 - 3 + 1, x - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(matrices, x + i + 2, i2 - 3 + 1, x + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
        this.fillGradient(matrices, x - 3, i2 - 3, x + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
        this.fillGradient(matrices, x - 3, i2 + k + 2, x + i + 3, i2 + k + 3, 1344798847, 1344798847);
        MatrixStack matrixstack = new MatrixStack();
        IRenderTypeBuffer.Impl vertexConsumerProvider$immediate = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        Matrix4f matrix4f = matrixstack.last().pose();

        int yy = i2;
        if (icon.readyToRender()) {
            TwitterForMC2.getInstance().getTextureManager().bindTexture(icon.deliver());
            blit(matrices, x, i2, 0.0F, 0.0F, 20, 20, 20, 20);
            i2 += 20;
        }

        int yyy = i2;
        boolean p = user.isProtected();
        boolean v = user.isVerified();
        int m = (p ? 10 : 0) + (v ? 10 : 0);
        ITextProperties name = new TweetText(user.getName()).withStyle(TextFormatting.BOLD);
        List<IReorderingProcessor> nameFormatted = this.wrapLines(name, i - this.getWidthWithEmoji(THREE_PERIOD.getVisualOrderText()) - m);
        int n = ((TextRendererInvoker) this.font).drawWithEmoji(nameFormatted.size() == 1 ? nameFormatted.get(0) : IReorderingProcessor.composite(nameFormatted.get(0), THREE_PERIOD.getVisualOrderText()), (float) x, (float) i2 + 2, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);
        ((TextRendererInvoker) this.font).drawWithEmoji(new TweetText(summary.getScreenName()).withStyle(TextFormatting.GRAY), (float) x, (float) i2 + 12, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);

        for (IReorderingProcessor s1 : desc) {
            if (s1 != null) {
                ((TextRendererInvoker) this.font).drawWithEmoji(s1, (float) x, (float) i2 + 26, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);
            }

            i2 += 10;
        }

        if (ff.size() == 1) {
            ((TextRendererInvoker) this.font).drawWithEmoji(ff.get(0), (float) x, (float) i2 + 30, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);
        } else {
            ((TextRendererInvoker) this.font).drawWithEmoji(IReorderingProcessor.composite(Lists.newArrayList(follow.getVisualOrderText(), space.getVisualOrderText(), FOLLOW.getVisualOrderText())), (float) x, (float) i2 + 30, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);
            ((TextRendererInvoker) this.font).drawWithEmoji(IReorderingProcessor.composite(Lists.newArrayList(follower.getVisualOrderText(), space.getVisualOrderText(), FOLLOWER.getVisualOrderText())), (float) x, (float) i2 + 30 + 10, -1, true, matrix4f, vertexConsumerProvider$immediate, false, 0, 15728880);
        }

        vertexConsumerProvider$immediate.endBatch();

        if (p) {
            n += this.renderProtected(matrices, n, yyy + 2);
        }
        if (v) {
            this.renderVerified(matrices, n, yyy + 2);
        }

        RenderSystem.enableDepthTest();

        return x - 4 < mouseX && x + i + 4 > mouseX && yy - 4 < mouseY && yy + k + 4 > mouseY;
    }

    protected void displayStatus(@Nullable Screen parent, TweetSummary summary) {
        this.minecraft.setScreen(new TwitterShowStatusScreen(parent, summary));
    }

    protected void displayTwitterUser(@Nullable Screen parent, User user) {
        this.minecraft.setScreen(new TwitterShowUserScreen(parent, user));
    }

    public List<IReorderingProcessor> wrapUserNameToWidth(TweetSummary summary, int width) {
        return this.wrapLines(new TranslationTextComponent("tw.retweeted.user", new TweetText(summary.getUser().getName())), width);
    }

    public List<IReorderingProcessor> wrapLines(ITextProperties visitable, int width) {
        return ((TextRendererInvoker) this.font).wrapLinesWithEmoji(visitable, width);
    }

    public int drawWithEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color) {
        return ((TextRendererInvoker) this.font).drawWithEmoji(matrices, text, x, y, color);
    }

    public int drawWithShadowAndEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color) {
        return ((TextRendererInvoker) this.font).drawWithShadowAndEmoji(matrices, text, x, y, color);
    }

    public int drawWithEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color) {
        return ((TextRendererInvoker) this.font).drawWithEmoji(matrices, text, x, y, color);
    }

    public int drawWithShadowAndEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color) {
        return ((TextRendererInvoker) this.font).drawWithShadowAndEmoji(matrices, text, x, y, color);
    }

    public int getWidthWithEmoji(IReorderingProcessor text) {
        return ((TextRendererInvoker) this.font).getWidthWithEmoji(text);
    }

    public int renderProtected(MatrixStack matrices, int x, int y) {
        this.minecraft.textureManager.bind(PROTECTED);
        matrices.pushPose();
        matrices.translate(x, y, 0.0F);
        matrices.scale(0.625F, 0.625F, 0.625F);
        blit(matrices, 0, 0, 0.0F, 0.0F, 16, 16, 16, 16);
        matrices.popPose();
        return 10;
    }

    public int renderVerified(MatrixStack matrices, int x, int y) {
        this.minecraft.textureManager.bind(VERIFIED);
        matrices.pushPose();
        matrices.translate(x, y, 0.0F);
        matrices.scale(0.625F, 0.625F, 0.625F);
        blit(matrices, 0, 0, 0, 0, 16, 16, 16, 16);
        matrices.popPose();
        return 10;
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        if (style != null && style.getClickEvent() != null && style.getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
            try {
                URI uri = new URI(style.getClickEvent().getValue());
                if (uri.getScheme().equalsIgnoreCase(PROTOCOL)) {
                    String path = uri.getPath().substring(1);
                    switch (HostType.from(uri.getHost())) {
                        case SHOW_STATUS:
                            long id = Long.parseLong(path);

                            if (this instanceof TwitterShowStatusScreen && ((TwitterShowStatusScreen) this).getTweetSummary().getId() == id) {
                                return true;
                            }

                            for (TweetSummary summary : TwitterForMC2.getInstance().tweetSummaries) {
                                if (summary.getId() == id) {
                                    this.displayStatus(this, summary);
                                    return true;
                                }
                            }

                            TweetSummary tweetSummary = new TweetSummary(TwitterForMC2.getInstance().mcTwitter.showStatus(id));
                            TwitterForMC2.getInstance().tweets.add(tweetSummary.getStatus());
                            TwitterForMC2.getInstance().tweetSummaries.add(tweetSummary);
                            this.displayStatus(this, tweetSummary);
                            return true;
                        case SHOW_USER:
                            this.displayTwitterUser(this, TwitterForMC2.getInstance().mcTwitter.showUser(path));
                            return true;
                    }
                } else {
                    return super.handleComponentClicked(style);
                }
            } catch (Exception e) {
                LOGGER.warn("Error occurred while handling text click", e);
                this.accept(new TranslationTextComponent("tw.simple.error", e.getLocalizedMessage()));
            }
        }

        return false;
    }

    protected enum HostType {
        UNKNOWN(""),
        SHOW_STATUS("status"),
        SHOW_USER("screenname"),
        HASHTAG("hashtag");

        private final String hostName;

        HostType(String hostName) {
            this.hostName = hostName;
        }

        public static HostType from(String hostName) {
            for (HostType hostType : values()) {
                if (hostType.hostName.equals(hostName)) {
                    return hostType;
                }
            }

            return UNKNOWN;
        }

        public String getHostName() {
            return this.hostName;
        }
    }

    protected class TweetList extends ExtendedTwitterTweetList<TweetList.ParentEntry> {
        @Nullable
        protected AbstractTwitterScreen.TweetList.TweetEntry hoveringEntry;
        protected boolean isHovering;
        protected int fade;

        protected TweetList(Minecraft mcIn, int width, int height, int top, int bottom) {
            super(mcIn, width, height, top, bottom);
        }

        @Override
        public void tick() {
            this.fade = this.isHovering ? 10 : this.fade - 1;
            this.children().forEach(AbstractTwitterScreen.TweetList.ParentEntry::tick);
            super.tick();
        }

        @Override
        protected int getScrollbarPositionX() {
            return AbstractTwitterScreen.this.width - 5;
        }

        @Override
        public int getRowWidth() {
            return AbstractTwitterScreen.this.width / 2;
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
        }

        @Override
        protected void renderHoleBackground(MatrixStack matrices, int top, int bottom, int alphaTop, int alphaBottom) {
            this.fillGradient(matrices, this.left + this.width, bottom, this.left, top, -15392725, -15392725);
        }

        @Override
        public void render(MatrixStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
            super.render(matrices, p_render_1_, p_render_2_, p_render_3_);

            boolean bl = false;
            matrices.pushPose();
            matrices.translate(0.0D, 0.0D, 0.5D);
            for (ImmutableList<Widget> Widgets : this.children().stream().map(AbstractTwitterTweetList.AbstractTwitterListEntry::getOverlayButtons).collect(Collectors.toList())) {
                for (Widget Widget : Widgets.stream().filter(Widget -> Widget.active && Widget.visible).collect(Collectors.toList())) {
                    Widget.render(matrices, p_render_1_, p_render_2_, p_render_3_);
                    bl = true;
                }
            }
            matrices.popPose();

            if (bl) {
                return;
            }

            AbstractTwitterScreen.TweetList.ParentEntry parentEntry = this.getEntryAtPosition(p_render_1_, p_render_2_);
            if (parentEntry instanceof TweetEntry) {
                TweetEntry e = (TweetEntry) parentEntry;
                if (this.hoveringEntry != null) {
                    this.isHovering = AbstractTwitterScreen.this.renderTwitterUser(matrices, this.hoveringEntry.frame.getMain(), this.getRowLeft() - 60, this.hoveringEntry.getY() + this.hoveringEntry.retweetedUserNameHeight + 2 + 22, p_render_1_, p_render_2_);
                    if (!this.isHovering && this.fade < 0) {
                        this.hoveringEntry = null;
                        this.fade = 0;
                    }
                } else if (e.mayClickIcon(p_render_1_, p_render_2_)) {
                    this.hoveringEntry = e;
                    this.isHovering = AbstractTwitterScreen.this.renderTwitterUser(matrices, e.frame.getMain(), this.getRowLeft() - 60, e.getY() + e.retweetedUserNameHeight + 2 + 22, p_render_1_, p_render_2_);
                    this.fade = 10;
                }
            }
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            for (ImmutableList<Widget> Widgets : this.children().stream().map(AbstractTwitterTweetList.AbstractTwitterListEntry::getOverlayButtons).collect(Collectors.toList())) {
                for (Widget Widget : Widgets.stream().filter(Widget -> Widget.active && Widget.visible).collect(Collectors.toList())) {
                    if (Widget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                        this.children().stream().filter(parentEntry -> parentEntry instanceof TweetEntry).map(parentEntry -> (TweetEntry) parentEntry).forEach(TweetEntry::hideAllOverlayButtons);
                        return true;
                    }
                }
            }

            this.children().stream().filter(parentEntry -> parentEntry instanceof TweetEntry).map(parentEntry -> (TweetEntry) parentEntry).forEach(TweetEntry::hideAllOverlayButtons);
            return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }

        @Override
        protected boolean isFocused() {
            return AbstractTwitterScreen.this.getFocused() == this;
        }

        protected class ParentEntry extends ExtendedTwitterTweetList.AbstractTwitterListEntry<ParentEntry> {
            protected int height;
            protected int y;

            @Override
            public void setHeight(int height) {
                this.height = height;
            }

            @Override
            public int getHeight() {
                return this.height;
            }

            @Override
            public int getY() {
                return this.y;
            }

            @Override
            public void setY(int y) {
                this.y = y;
            }
        }

        protected class TweetEntry extends ParentEntry {
            @Nullable
            protected final TweetSummary quoteSourceSummary;
            protected final List<IReorderingProcessor> quotedTweetStrings;
            protected final int retweetedUserNameHeight = 9;
            protected int photoRenderingWidth;
            protected int photoRenderingHeight;
            @Nullable
            protected TwitterButton replyButton;
            @Nullable
            protected TwitterButton retweetButton;
            @Nullable
            protected Button retweetButton$retweet;
            @Nullable
            protected Button retweetButton$quoteRetweet;
            @Nullable
            protected TwitterButton favoriteButton;
            @Nullable
            protected TwitterButton shareButton;
            protected int fourBtnHeightOffset;
            private final TweetFrame frame;

            protected TweetEntry(TweetSummary tweet) {
                this.frame = new TweetFrame(AbstractTwitterScreen.this, tweet, TweetList.this.getRowWidth());
                boolean flag = tweet.getRetweetedSummary() != null;
                this.quoteSourceSummary = this.frame.getMain().getQuotedTweetSummary();
                this.quotedTweetStrings = this.quoteSourceSummary != null ? AbstractTwitterScreen.this.wrapLines(new TweetText(this.quoteSourceSummary.getText()), AbstractTwitterScreen.TweetList.this.getRowWidth() - 40) : Lists.newArrayList();
                this.photoRenderingWidth = TweetList.this.getRowWidth() - 30;
                this.photoRenderingHeight = (int) (0.5625F * this.photoRenderingWidth);
                this.height = this.frame.getHeight();
                this.fourBtnHeightOffset = this.height - 14;
            }

            @Override
            public void tick() {
                this.updateButtonY(this.fourBtnHeightOffset + this.y);
            }

            /*
            @Override
            public void init() {
                int i = AbstractTwitterScreen.TweetList.this.getRowLeft() + 24;
                int h = AbstractTwitterScreen.TweetList.this.getRowWidth();
                int j = (h - 64) / 3;

                if (this.summary != null) {
                    this.replyButton = this.addButton(new TwitterButton(i, this.fourBtnHeightOffset, 10, 10, 0, 0, 16, REPLY, 16, 32, 16, 16, (p) -> {
                        AbstractTwitterScreen.this.minecraft.setScreen(new TwitterReplyScreen(AbstractTwitterScreen.this, this.summary));
                    }));

                    i += j;

                    boolean bl = this.summary.isRetweeted();
                    this.retweetButton = this.addButton(new TwitterButton(i, this.fourBtnHeightOffset, 10, 10, 0, 0, bl ? 0 : 16, bl ? RETWEETED : RETWEET, 16, bl ? 16 : 32, 16, 16, (p) -> {
                        if (!this.summary.getUser().isProtected()) {
                            this.showRetweetButtons();
                        }
                    }));

                    this.retweetButton$retweet = this.addOverlayButton(new Button(i + 5 - h / 4, this.fourBtnHeightOffset, h / 2, 20, bl ? new TranslationTextComponent("tw.unretweet") : new TranslationTextComponent("tw.retweet"), button -> {
                        this.hideRetweetButtons();
                        try {
                            if (this.summary.isRetweeted()) {
                                TwitterForMC.getInstance().mcTwitter.unRetweetStatus(this.summary.getId());
                                this.summary.retweet(false);
                                this.retweetButton$retweet.setMessage(new TranslationTextComponent("tw.retweet"));
                                this.retweetButton.setImage(RETWEET);
                                this.retweetButton.setWhenHovered(16);
                                this.retweetButton.setSize(16, 32);
                            } else {
                                TwitterForMC.getInstance().mcTwitter.retweetStatus(this.summary.getId());
                                this.summary.retweet(true);
                                this.retweetButton$retweet.setMessage(new TranslationTextComponent("tw.unretweet"));
                                this.retweetButton.setImage(RETWEETED);
                                this.retweetButton.setWhenHovered(0);
                                this.retweetButton.setSize(16, 16);
                            }
                        } catch (TwitterException e) {
                            AbstractTwitterScreen.this.accept(new TranslationTextComponent("tw.failed.retweet", e.getErrorMessage()));
                        }
                    }));

                    this.retweetButton$quoteRetweet = this.addOverlayButton(new FunctionalButtonWidget(i + 5 - h / 4, this.fourBtnHeightOffset + 20, h / 2, 20, new TranslationTextComponent("tw.quote.tweet"), button -> {
                        this.hideRetweetButtons();

                    }, integer -> integer + 20));

                    this.hideRetweetButtons();

                    i += j;

                    this.favoriteButton = this.addButton(new TwitterButton(i, this.fourBtnHeightOffset, 10, 10, 0, 0, this.summary.isFavorited() ? 0 : 16, this.summary.isFavorited() ? FAVORITED : FAVORITE, 16, this.summary.isFavorited() ? 16 : 32, 16, 16, (b) -> {
                        try {
                            if (this.summary.isFavorited()) {
                                TwitterForMC.getInstance().mcTwitter.destroyFavorite(this.summary.getId());
                                this.summary.favorite(false);
                                ((ChangeableImageButton) b).setImage(FAVORITE);
                                ((ChangeableImageButton) b).setWhenHovered(16);
                                ((ChangeableImageButton) b).setSize(16, 32);
                            } else {
                                TwitterForMC.getInstance().mcTwitter.createFavorite(this.summary.getId());
                                this.summary.favorite(true);
                                ((ChangeableImageButton) b).setImage(FAVORITED);
                                ((ChangeableImageButton) b).setWhenHovered(0);
                                ((ChangeableImageButton) b).setSize(16, 16);
                            }
                        } catch (TwitterException e) {
                            AbstractTwitterScreen.this.accept(new TranslationTextComponent("tw.failed.like", e.getErrorMessage()));
                        }
                    }));

                    i += j;

                    this.shareButton = this.addButton(new TwitterButton(i, this.fourBtnHeightOffset, 10, 10, 0, 0, 16, SHARE, 16, 32, 16, 16, (p) -> {
                        AbstractTwitterScreen.this.minecraft.keyboardHandler.setClipboard(this.summary.getTweetURL());
                        AbstractTwitterScreen.this.accept(new TranslationTextComponent("tw.copy.tweeturl.to.clipboard"));
                    }));
                }
            }
            */

            @Override
            public void onRemove() {
                this.frame.removed();
            }

            protected void hideAllOverlayButtons() {
                this.overlayButtons.forEach(Widget -> Widget.active = Widget.visible = false);
            }

            private void hideOrShowRetweetButtons(boolean flag) {
                if (this.retweetButton$retweet != null && this.retweetButton$quoteRetweet != null) {
                    this.retweetButton$retweet.active = this.retweetButton$quoteRetweet.active = this.retweetButton$retweet.visible = this.retweetButton$quoteRetweet.visible = flag;
                }
            }

            protected void hideRetweetButtons() {
                this.hideOrShowRetweetButtons(false);
            }

            protected void showRetweetButtons() {
                this.hideOrShowRetweetButtons(true);
            }

            @Override
            public void render(MatrixStack matrices, int itemIndex, int rowTop, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableBlend();
                this.frame.renderFrame(matrices, itemIndex, rowTop, rowLeft, rowWidth, height2, mouseX, mouseY, isMouseOverAndObjectEquals, delta);
                /*
                if (this.quoteSourceSummary != null) {
                    nowY += 10;
                    ImageDataDeliverer qsIco = this.quoteSourceSummary.getUserIconData();
                    if (qsIco.readyToRender()) {
                        TwitterForMC.getTextureManager().bindTexture(qsIco.deliver());
                        drawTexture(matrices, rowLeft + 24 + 5, nowY, 0.0F, 0.0F, 10, 10, 10, 10);
                    }
                    this.renderUserName(matrices, this.quoteSourceSummary, rowLeft + 24 + 5 + 10 + 4, nowY, AbstractTwitterScreen.TweetList.this.getRowWidth() - 24 - 5 - 10 - 4 - 10);
                    for (int i = 0; i < this.quotedTweetStrings.size(); i++) {
                        AbstractTwitterScreen.this.drawWithShadowAndEmoji(matrices, this.quotedTweetStrings.get(i), rowLeft + 24 + 5, nowY + 10 + i * AbstractTwitterScreen.this.font.fontHeight, 16777215);
                    }
                    nowY += 10 + this.quotedTweetStrings.size() * AbstractTwitterScreen.this.font.fontHeight;
                }

                this.renderButtons(matrices, mouseX, mouseY, delta);

                if (this.summary != null) {
                    if (this.summary.getRetweetCount() != 0 && this.retweetButton != null) {
                        AbstractTwitterScreen.this.drawWithShadowAndEmoji(matrices, Text.of("" + this.summary.getRetweetCountF()), this.retweetButton.x + 16.0F, this.retweetButton.y, 11184810);
                    }
                    if (this.summary.getFavoriteCount() != 0 && this.favoriteButton != null) {
                        AbstractTwitterScreen.this.drawWithShadowAndEmoji(matrices, Text.of("" + this.summary.getFavoriteCountF()), this.favoriteButton.x + 16.0F, this.favoriteButton.y, 11184810);
                    }
                }
                */
            }

            @Override
            public boolean mouseClicked(double x, double y, int button) {
                /*
                    int i = AbstractTwitterScreen.TweetList.this.getRowLeft() + 24;
                    int j = this.y + this.retweetedUserNameHeight + 11 + this.strings.size() * AbstractTwitterScreen.this.font.lineHeight;
                    int k = this.summary.getPhotoMediaLength();
                    int w2 = this.photoRenderingWidth / 2;
                    int h2 = this.photoRenderingHeight / 2;
                    boolean xMore = x >= i;
                    boolean yMore = y >= j;
                    boolean b = xMore && x <= i + this.photoRenderingWidth && yMore && y <= j + this.photoRenderingHeight;
                    boolean b1 = xMore && x <= i + w2 && yMore && y <= j + this.photoRenderingHeight;
                    boolean b2 = x >= i + w2 + 1 && x <= i + this.photoRenderingWidth && yMore && y <= j + h2;

                    if (k == 1) {
                        if (b) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 0);
                        }
                    } else if (k == 2) {
                        if (b1) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 0);
                        } else if (x >= i + w2 + 1 && x <= i + this.photoRenderingWidth && yMore && y <= j + this.photoRenderingHeight) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 1);
                        }
                    } else if (k == 3) {
                        if (b1) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 0);
                        } else if (b2) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 1);
                        } else if (xMore && x <= i + this.photoRenderingWidth && y >= j + h2 + 1 && y <= j + this.photoRenderingHeight) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 2);
                        }
                    } else if (k == 4) {
                        if (xMore && x <= i + w2 && yMore && y <= j + h2) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 0);
                        } else if (b2) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 1);
                        } else if (xMore && x <= i + w2 && y >= j + h2 + 1 && y <= j + this.photoRenderingHeight) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 2);
                        } else if (x >= i + w2 + 1 && x <= i + this.photoRenderingWidth && y >= j + h2 + 1 && y <= j + this.photoRenderingHeight) {
                            return this.displayTwitterPhotoAndShowStatusScreen(button, 3);
                        }
                    }



                    if (this.frame.getMain().isIncludeVideo()) {
                        if (b) {
                            return this.videoClicked(button);
                        }
                    }
*/
                for (Widget w : this.buttons) {
                    if (w.mouseClicked(x, y, button)) {
                        return true;
                    }
                }

                if (button == 0) {
                    if (TweetList.this.getSelected() == this) {
                        AbstractTwitterScreen.this.displayStatus(AbstractTwitterScreen.this, this.frame.getMain());
                    } else {
                        TweetList.this.setSelected(this);
                    }
                    return true;
                } else {
                    return false;
                }
            }

            protected boolean mayClickIcon(double x, double y) {
                int i = AbstractTwitterScreen.TweetList.this.getRowLeft();
                int j = this.y + this.retweetedUserNameHeight;
                return TweetList.this.top < y && TweetList.this.bottom > y && x > i && x < i + 16 && y > j && y < j + 16;
            }

            protected boolean displayTwitterPhotoAndShowStatusScreen(int mouseButton, int index) {
                //AbstractTwitterScreen.this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if (mouseButton == 0) {
                    AbstractTwitterScreen.this.minecraft.setScreen(new TwitterPhotoAndShowStatusScreen(AbstractTwitterScreen.this, this.frame.getMain(), index));
                } else if (mouseButton == 1) {
                    //TODO save picture action;
                }

                return true;
            }

            protected boolean videoClicked(int mouseButton) {
                if (mouseButton == 0) {
                    Util.getPlatform().openUri(this.frame.getMain().getVideoURL());
                } else if (mouseButton == 1) {
                    AbstractTwitterScreen.this.minecraft.setScreen(new DownloadTwitterVideoScreen(AbstractTwitterScreen.this, this.frame.getMain()));
                }

                return false;
            }

            @Override
            public void setHeight(int height) {
                super.setHeight(height);
                this.fourBtnHeightOffset = this.height - 14;
                this.buttons.clear();
                this.overlayButtons.clear();
                this.init();
                TweetList.this.calcAllHeight();
                TweetList.this.calcAverage();
                TweetList.this.setY(-(int) TweetList.this.getScrollAmount());
            }

            @Override
            public void setY(int y) {
                super.setY(y);
                this.updateButtonY(this.fourBtnHeightOffset + this.y);
            }

            protected void updateButtonY(int y) {
                this.buttons.forEach(widget -> {
                    if (widget instanceof FunctionalButtonWidget) {
                        FunctionalButtonWidget functionalButtonWidget = (FunctionalButtonWidget) widget;
                        functionalButtonWidget.y = functionalButtonWidget.yFunction.apply(y);
                    } else {
                        widget.y = y;
                    }
                });

                this.overlayButtons.forEach(widget -> {
                    if (widget instanceof FunctionalButtonWidget) {
                        FunctionalButtonWidget functionalButtonWidget = (FunctionalButtonWidget) widget;
                        functionalButtonWidget.y = functionalButtonWidget.yFunction.apply(y);
                    } else {
                        widget.y = y;
                    }
                });
            }

            @Override
            public boolean equals(Object obj) {
                return this.frame.getMain().equals(obj);
            }

            @Override
            public int hashCode() {
                return this.frame.getMain().hashCode();
            }
        }
    }
}
