package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.gui.screen.DownloadTwitterVideoScreen;
import com.hamusuke.tw4mc2.utils.TwitterUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Util;
import nick1st.fancyvideo.api.MediaPlayer;
import nick1st.fancyvideo.api.MediaPlayers;

import java.awt.*;

public class Video extends TweetFramePiece {
    private int id = -1;
    private MediaPlayer player;
    private final String url;

    Video(TweetFrame parent, String url, int rowWidth) {
        super(parent);
        this.url = url;
        this.width = rowWidth - 30;
        this.height = (int) (this.width * 0.5625F);
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        if (this.id < 0) {
            this.id = MediaPlayer.getNew();
            this.player = MediaPlayers.getPlayer(this.id);
            this.player.prepare(this.url);
            this.player.volume(100);
            this.player.getTrueMediaPlayer().mediaPlayer().controls().setRepeat(true);
            this.player.playPrepared();
        }

        this.y = y;
        matrices.pushPose();
        this.player.bindFrame();

        Dimension dimension = this.player.getTrueMediaPlayer().mediaPlayer().video().videoDimension();
        if (dimension != null) {
            int w = rowWidth - 30;
            Dimension r = TwitterUtil.wrapImageSizeToMin(dimension, new Dimension(w, (int) (w * 0.5625F)));
            this.width = r.width;
            this.height = r.height;
            this.x = rowLeft + 24 + (w - this.width) / 2;
        }

        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrices, this.x, this.y, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        matrices.popPose();

        return this.height;
    }

    @Override
    protected void onClick(double x, double y, int button) {
        if (button == 0) {
            Util.getPlatform().openUri(this.url);
        } else if (button == 1) {
            this.minecraft.setScreen(new DownloadTwitterVideoScreen(this.parent.twitterScreen, this.parent.getMain()));
        }
    }

    @Override
    public void removed() {
        if (this.id >= 0) {
            MediaPlayers.removePlayer(this.id);
        }
    }
}
