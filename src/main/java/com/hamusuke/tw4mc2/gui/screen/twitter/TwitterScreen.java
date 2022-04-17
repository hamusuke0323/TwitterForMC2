package com.hamusuke.tw4mc2.gui.screen.twitter;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.login.TwitterLoginScreen;
import com.hamusuke.tw4mc2.gui.screen.settings.TwitterSettingsScreen;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.utils.TweetSummaryProcessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableInt;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitterScreen extends AbstractTwitterScreen {
	@Nullable
	private Screen parent;
	private final AtomicBoolean refreshingTL = new AtomicBoolean();
	@Nullable
	private IntegerFieldWidget count;
	private final TwitterLoginScreen loginScreen;

	public TwitterScreen() {
		super(NarratorChatListener.NO_TITLE, null);
		this.loginScreen = new TwitterLoginScreen(this);

		if (TwitterForMC2.getInstance().twitterScreen != null) {
			throw new IllegalStateException("TwitterScreen object can be created only one");
		}
	}

	public void setParentScreen(@Nullable Screen parent) {
		this.parent = parent;
	}

	@Override
	public void init() {
		this.getPreviousScreen().ifPresent(screen -> this.minecraft.setScreen(screen));

		int i = this.width / 2;
		MutableInt j = new MutableInt();
		int k = this.width / 4;

		if (!TwitterForMC2.getInstance().isLoggedInTwitter()) {
			this.list = new TwitterScreen.TweetList(this.minecraft);

			this.addRenderLaterButton(new Button(j.getValue(), this.height - 20, i, 20, new TranslationTextComponent("twitter.login"), (l) -> {
				this.minecraft.setScreen(this.loginScreen);
			}));

			j.add(i);
		} else {
			this.addButton(new Button(0, this.height - 110, k - 10, 20, new TranslationTextComponent("tweet"), (press) -> {
				this.minecraft.setScreen(new TwitterTweetScreen(this));
			}));

			this.addButton(new Button(0, this.height - 50, k - 10, 20, new TranslationTextComponent("tw.view.profile"), (press) -> {
				press.active = false;
				try {
					this.displayTwitterUser(this, TwitterForMC2.getInstance().mcTwitter.showUser(TwitterForMC2.getInstance().mcTwitter.getId()));
				} catch (TwitterException e) {
					this.accept(ITextComponent.nullToEmpty(e.getErrorMessage()));
					press.active = true;
				}
			}));

			int l = i / 4;
			boolean bl = this.count == null;
			this.count = this.addWidget(new IntegerFieldWidget(j.getValue() + i - l, this.height - 19, l, 18, this.count, new TranslationTextComponent("tw.refresh.desc"), 1, 200));
			if (bl || this.count.getValue().isEmpty()) {
				this.count.setValue("20");
			}

			this.addRenderLaterButton(new Button(j.getValue(), this.height - 20, i - l, 20, new TranslationTextComponent("twitter.refresh"), (p) -> {
				p.active = false;
				this.refreshingTL.set(true);
				List<Status> t = Lists.newArrayList();
				try {
					t.addAll(TwitterForMC2.getInstance().mcTwitter.getHomeTimeline(new Paging().count(this.count == null ? 20 : this.count.getIntValue())));
				} catch (TwitterException e) {
					this.accept(ITextComponent.nullToEmpty(e.getErrorMessage()));
				} catch (CommandSyntaxException e) {
					this.accept(ITextComponent.nullToEmpty(e.getMessage()));
				}

				Collections.reverse(t);

				new TweetSummaryProcessor(t, tweetSummary -> {
					TwitterForMC2.getInstance().tweets.add(tweetSummary.getStatus());
					TwitterForMC2.getInstance().tweetSummaries.add(tweetSummary);
					this.children.remove(this.list);
					this.list = new TweetList(this.minecraft);
					this.addWidget(this.list);
				}, () -> {
					p.active = true;
					this.refreshingTL.set(false);
					this.init(this.minecraft, this.width, this.height);
				}).process();
			})).active = !this.refreshingTL.get();

			j.add(i);

			this.addButton(new Button(0, this.height - 80, k - 10, 20, new TranslationTextComponent("tw.export.timeline"), (b) -> {
				b.active = false;
				try {
					TwitterForMC2.getInstance().exportTimeline();
				} catch (IOException e) {
					this.accept(ITextComponent.nullToEmpty(e.getLocalizedMessage()));
				}
				b.active = true;
			}));
		}

		this.addRenderLaterButton(new Button(j.getValue(), this.height - 20, i, 20, DialogTexts.GUI_BACK, (p_213034_1_) -> this.onClose()));

		this.addButton(new Button(0, this.height - 140, k - 10, 20, new TranslationTextComponent("tw.settings"), (b) -> {
			this.minecraft.setScreen(new TwitterSettingsScreen(this));
		}));

		if (!this.refreshingTL.get()) {
			double scroll = this.list != null ? this.list.getScrollAmount() : 0.0D;
			this.list = new TwitterScreen.TweetList(this.minecraft);
			this.list.setScrollAmount(scroll);
			this.addWidget(this.list);
		}

		if (this.parent != null) {
			this.parent.resize(this.minecraft, this.width, this.height);
		}

		getMessageWidget().ifPresent(messageWidget -> messageWidget.init(this.width, this.height));
	}

	public boolean isInitialized() {
		return this.minecraft != null;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.parent != null) {
			this.parent.render(matrices, -1, -1, delta);
		}

		if (this.minecraft.screen == this) {
			this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		}

		super.render(matrices, mouseX, mouseY, delta);

		if (this.list != null) {
			this.list.render(matrices, mouseX, mouseY, delta);
		}

		this.renderButtonLater(matrices, mouseX, mouseY, delta);

		if (this.count != null) {
			this.count.render(matrices, mouseX, mouseY, delta);
		}

		renderMessage(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}

	private class IntegerFieldWidget extends TextFieldWidget {
		private final IntegerArgumentType integerArgumentType;
		@Nullable
		private ITextComponent message;

		public IntegerFieldWidget(int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, ITextComponent text, int min, int max) {
			super(TwitterScreen.this.font, x, y, width, height, copyFrom, text);
			this.integerArgumentType = IntegerArgumentType.integer(min, max);
			this.setFilter(s -> {
				try {
					this.integerArgumentType.parse(new StringReader(s));
					this.message = null;
				} catch (CommandSyntaxException e) {
					this.message = ITextComponent.nullToEmpty(e.getMessage());
				}

				return true;
			});
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.renderButton(matrices, mouseX, mouseY, delta);
			Optional.ofNullable(this.message).ifPresent(s -> {
				List<IReorderingProcessor> orderedTexts = TwitterScreen.this.font.split(this.message, TwitterScreen.this.width);
				int max = AbstractTwitterScreen.getMaxWidth(TwitterScreen.this.font, orderedTexts);
				TwitterScreen.this.renderTooltip(matrices, orderedTexts, MathHelper.clamp(this.x + this.width / 2 - max / 2, 0, TwitterScreen.this.width - max), this.y - orderedTexts.size() * 9);
			});

			if (this.isHovered) {
				this.renderToolTip(matrices, mouseX, mouseY);
			}
		}

		@Override
		public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
			List<IReorderingProcessor> orderedTexts = TwitterScreen.this.font.split(this.getMessage(), TwitterScreen.this.width / 2);
			TwitterScreen.this.renderTooltip(matrices, orderedTexts, mouseX, mouseY);
		}

		public int getIntValue() throws CommandSyntaxException {
			return this.integerArgumentType.parse(new StringReader(this.getValue()));
		}
	}

	private class TweetList extends AbstractTwitterScreen.TweetList {
		private TweetList(Minecraft mcIn) {
			super(mcIn, TwitterScreen.this.width, TwitterScreen.this.height, 0, TwitterScreen.this.height - 20);
			for (TweetSummary tweetSummary : TwitterForMC2.getInstance().tweetSummaries) {
				this.addEntry(new TweetEntry(tweetSummary));
			}

			if (this.getSelected() != null) {
				this.centerScrollOn(this.getSelected());
			}
		}
	}
}
