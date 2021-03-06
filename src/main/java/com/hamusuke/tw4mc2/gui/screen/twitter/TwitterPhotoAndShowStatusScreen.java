package com.hamusuke.tw4mc2.gui.screen.twitter;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.tweet.TwitterPhotoMedia;
import com.hamusuke.tw4mc2.utils.TwitterUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.List;

public class TwitterPhotoAndShowStatusScreen extends ParentalScreen {
	private final TweetSummary summary;
	private final int index;

	public TwitterPhotoAndShowStatusScreen(Screen parent, TweetSummary summary, int index) {
		super(NarratorChatListener.NO_TITLE, parent);
		this.summary = summary;
		this.index = index;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.parent.render(matrices, -1, -1, p_render_3_);
		this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();

		List<TwitterPhotoMedia> p = this.summary.getPhotoMedias();
		TwitterPhotoMedia media = p.get(this.index);
		if (media.readyToRender()) {
			Dimension d = TwitterUtil.wrapImageSizeToMin(new Dimension(media.getWidth(), media.getHeight()), new Dimension(this.width, this.height));
			TwitterForMC2.getInstance().getTextureManager().bindTexture(media.getData());
			blit(matrices, 0, 0, 0.0F, 0.0F, d.width, d.height, d.width, d.height);
		}

		RenderSystem.disableBlend();

		super.render(matrices, p_render_1_, p_render_2_, p_render_3_);
	}
}
