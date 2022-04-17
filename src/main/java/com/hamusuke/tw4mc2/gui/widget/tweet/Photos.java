package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.hamusuke.tw4mc2.tweet.TwitterPhotoMedia;
import com.hamusuke.tw4mc2.utils.TwitterUtil;
import com.mojang.blaze3d.matrix.MatrixStack;

import java.awt.*;
import java.util.List;

public class Photos extends TweetFramePiece {
    Photos(TweetFrame parent, TweetSummary tweetSummary, int rowWidth) {
        super(parent, tweetSummary);

        if (tweetSummary.getPhotoMedias().size() > 0) {
            this.width = rowWidth - 30;
            this.height = (int) (this.width * 0.5625F);
        } else {
            this.width = 0;
            this.height = 0;
        }
    }

    @Override
    public int render(MatrixStack matrices, int itemIndex, int y, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
        this.x = rowLeft + 24;
        this.y = y;
        int w2 = this.width / 2;
        int h2 = this.height / 2;
        List<TwitterPhotoMedia> p = this.summary.getPhotoMedias();
        if (p.size() == 1) {
            TwitterPhotoMedia media = p.get(0);
            if (media.readyToRender()) {
                Dimension d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(this.width, this.height));
                TwitterForMC2.getInstance().getTextureManager().bindTexture(media.getData());
                blit(matrices, this.x, y, 0.0F, (float) (d.height - this.height) / 2, this.width, this.height, d.width, d.height);
            }
        } else if (p.size() == 2) {
            for (int i = 0; i < 2; i++) {
                TwitterPhotoMedia media = p.get(i);
                if (media.readyToRender()) {
                    Dimension d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(w2, this.height));
                    TwitterForMC2.getInstance().getTextureManager().bindTexture(media.getData());
                    blit(matrices, this.x + i * w2 + 1, y, 0.0F, (float) (d.height - this.height) / 2, w2, this.height, d.width, d.height);
                }
            }
        } else if (p.size() == 3) {
            for (int i = 0; i < 3; i++) {
                TwitterPhotoMedia media = p.get(i);
                if (media.readyToRender()) {
                    Dimension d;
                    TwitterForMC2.getInstance().getTextureManager().bindTexture(media.getData());
                    if (i == 0) {
                        d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(w2, this.height));
                        blit(matrices, this.x, y, 0.0F, (float) (d.height - this.height) / 2, w2, this.height, d.width, d.height);
                    } else if (i == 1) {
                        d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(w2, h2 - 1));
                        blit(matrices, this.x + w2 + 1, y, 0.0F, (float) (d.height - h2 - 1) / 2, w2, h2 - 1, d.width, d.height - 1);
                    } else {
                        d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(w2, h2 - 1));
                        blit(matrices, this.x + w2 + 1, y + h2 + 1, 0.0F, (float) (d.height - h2 - 1) / 2, w2, h2 - 1, d.width, d.height - 1);
                    }
                }
            }
        } else if (p.size() == 4) {
            for (int i = 0; i < 4; i++) {
                TwitterPhotoMedia media = p.get(i);
                if (media.readyToRender()) {
                    Dimension d = TwitterUtil.wrapImageSizeToMax(new Dimension(media.getWidth(), media.getHeight()), new Dimension(w2, h2));
                    TwitterForMC2.getInstance().getTextureManager().bindTexture(media.getData());
                    if (i % 2 == 0) {
                        blit(matrices, this.x, y + ((i / 2) * (h2 + 1)), 0.0F, (float) (d.height - h2) / 2, w2, h2, d.width, d.height);
                    } else {
                        blit(matrices, this.x + w2 + 1, y + ((i / 3) * (h2 + 1)), 0.0F, (float) (d.height - h2) / 2, w2, h2, d.width, d.height);
                    }
                }
            }
        }

        return p.size() == 0 ? 0 : this.height;
    }
}
