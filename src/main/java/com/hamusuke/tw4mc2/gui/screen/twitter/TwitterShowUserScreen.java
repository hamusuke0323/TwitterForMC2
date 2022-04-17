package com.hamusuke.tw4mc2.gui.screen.twitter;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.text.TweetText;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.tweet.UserSummary;
import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import twitter4j.User;

import javax.annotation.Nullable;
import java.util.List;

public class TwitterShowUserScreen extends AbstractTwitterScreen {
	private final UserSummary user;
	private List<IReorderingProcessor> name = Lists.newArrayList();

	public TwitterShowUserScreen(@Nullable Screen parent, User user) {
		super(new StringTextComponent(user.getName()).withStyle(TextFormatting.BOLD), parent);
		this.user = new UserSummary(user);
	}

	@Override
	protected void init() {
		List<IReorderingProcessor> wrapped = this.wrapLines(this.title, this.width / 2 - 20);
		this.name = wrapped;
		int fontHeight = this.font.lineHeight + 1;
		int top = fontHeight * wrapped.size() + fontHeight;

		this.addButton(new Button(this.width / 2 - this.width / 4, 0, 20, 20, new StringTextComponent("←"), button -> this.onClose(), (button, matrices, mouseX, mouseY) -> {
			this.renderTooltip(matrices, DialogTexts.GUI_BACK, mouseX, mouseY);
		}));

		if (!this.user.isGettingUserTimeline()) {
			if (!this.user.isAlreadyGotUserTimeline()) {
				this.user.startGettingUserTimeline(() -> {
					double scroll = 0.0D;
					if (this.list != null) {
						scroll = this.list.getScrollAmount();
						this.children.remove(this.list);
					}

					this.list = new TweetList(this.minecraft, top, this.user);
					this.list.setScrollAmount(scroll);
					this.addWidget(this.list);
				});
			} else {
				double scroll = this.list != null ? this.list.getScrollAmount() : 0.0D;
				this.list = new TweetList(this.minecraft, top, this.user);
				this.list.setScrollAmount(scroll);
				this.addWidget(this.list);
			}
		}

		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.parent != null) {
			this.parent.render(matrices, -1, -1, delta);
		}
		if (this.minecraft.screen == this) {
			this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		}

		if (this.list != null) {
			this.list.render(matrices, mouseX, mouseY, delta);
		}
		super.render(matrices, mouseX, mouseY, delta);
		float left = (float) (this.width / 2 - this.width / 4 + 2);
		float y = 0.0F;
		for (IReorderingProcessor text : this.name) {
			this.drawWithShadowAndEmoji(matrices, text, left + 20, y, 16777215);
			y += this.font.lineHeight;
		}
		this.font.drawShadow(matrices, new TranslationTextComponent("tw.statuses.count", this.user.getStatusesCount()).withStyle(TextFormatting.GRAY), left + 20, y, 16777215);

		renderMessage(matrices, mouseX, mouseY, delta);
	}

	private class TweetList extends AbstractTwitterScreen.TweetList {
		private TweetList(Minecraft mcIn, int top, UserSummary userSummary) {
			super(mcIn, TwitterShowUserScreen.this.width, TwitterShowUserScreen.this.height, top, TwitterShowUserScreen.this.height - 20);
			this.addEntry(new TwitterShowUserScreen.TweetList.UserProfile(userSummary));
			for (TweetSummary summary : userSummary.getUserTimeline()) {
				this.addEntry(new TwitterShowUserScreen.TweetList.TweetEntry(summary));
			}

			if (this.getSelected() != null) {
				this.centerScrollOn(this.getSelected());
			}
		}

		@Override
		public void setSelected(@Nullable ParentEntry entry) {
			if (!(entry instanceof UserProfile)) {
				super.setSelected(entry);
			}
		}

		private class UserProfile extends ParentEntry {
			private final UserSummary summary;
			private final List<IReorderingProcessor> name;
			private final List<IReorderingProcessor> desc;

			private UserProfile(UserSummary summary) {
				this.summary = summary;
				boolean p = this.summary.isProtected();
				boolean v = this.summary.isVerified();
				int protectedVerifiedWidth = (p ? 10 : 0) + (v ? 10 : 0);
				this.name = TwitterShowUserScreen.this.wrapLines(new TweetText(this.summary.getName()).withStyle(TextFormatting.BOLD), TweetList.this.getRowWidth() - 10 - protectedVerifiedWidth);
				this.desc = TwitterShowUserScreen.this.wrapLines(new TweetText(this.summary.getDescription()), TweetList.this.getRowWidth() - 20);
				this.height = TwitterShowUserScreen.TweetList.this.getRowWidth() / 3 + 60 + this.desc.size() * TwitterShowUserScreen.this.font.lineHeight;
			}

			@Override
			public void init() {
			}

			@Override
			public void render(MatrixStack matrices, int itemIndex, int rowTop, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.enableBlend();

				int i = rowWidth / 3;
				int j = rowWidth / 5;

				ImageDataDeliverer header = this.summary.getHeader();
				ImageDataDeliverer icon = this.summary.getIcon();

				if (header.readyToRender()) {
					TwitterForMC2.getInstance().getTextureManager().bindTexture(header.deliver());
				} else {
					TwitterShowUserScreen.this.minecraft.textureManager.bind(MissingTextureSprite.getLocation());
				}
				blit(matrices, rowLeft, rowTop, 0.0F, 0.0F, rowWidth, i, rowWidth, i);

				if (icon.readyToRender()) {
					TwitterForMC2.getInstance().getTextureManager().bindTexture(icon.deliver());
				} else {
					TwitterShowUserScreen.this.minecraft.textureManager.bind(MissingTextureSprite.getLocation());
				}
				blit(matrices, rowLeft + 10, rowTop + (i - i / 3), 0.0F, 0.0F, j, j, j, j);

				int k = rowTop + (i - i / 3) + j;
				int x = 0;
				for (IReorderingProcessor text : this.name) {
					x = TwitterShowUserScreen.this.drawWithShadowAndEmoji(matrices, text, rowLeft + 10, k, 16777215);
				}

				k += (this.name.size() - 1) * TwitterShowUserScreen.this.font.lineHeight;

				if (this.summary.isProtected()) {
					x += TwitterShowUserScreen.this.renderProtected(matrices, x, k);
				}
				if (this.summary.isVerified()) {
					TwitterShowUserScreen.this.renderVerified(matrices, x, k);
				}

				k += TwitterShowUserScreen.this.font.lineHeight;

				TwitterShowUserScreen.this.drawWithShadowAndEmoji(matrices, new TweetText(this.summary.getScreenName()).withStyle(TextFormatting.GRAY), rowLeft + 10, k, 0);

				for (int index = 0; index < this.desc.size(); index++) {
					TwitterShowUserScreen.this.drawWithShadowAndEmoji(matrices, this.desc.get(index), rowLeft + 10, k + TwitterShowUserScreen.this.font.lineHeight * 2 + index * TwitterShowUserScreen.this.font.lineHeight, 16777215);
				}
			}
		}
	}
}
