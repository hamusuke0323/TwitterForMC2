package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.screen.twitter.TwitterPhotoAndShowStatusScreen;
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

    @Override
    protected void onClick(double x, double y, int button) {
        int i = this.x;
        int j = this.y;
        int k = this.summary.getPhotoMediaLength();
        int w2 = this.width / 2;
        int h2 = this.height / 2;
        boolean xMore = x >= i;
        boolean yMore = y >= j;
        boolean b = xMore && x <= i + this.width && yMore && y <= j + this.height;
        boolean b1 = xMore && x <= i + w2 && yMore && y <= j + this.height;
        boolean b2 = x >= i + w2 + 1 && x <= i + this.width && yMore && y <= j + h2;

        if (k == 1) {
            if (b) {
                this.displayScreen(button, 0);
            }
        } else if (k == 2) {
            if (b1) {
                this.displayScreen(button, 0);
            } else if (x >= i + w2 + 1 && x <= i + this.width && yMore && y <= j + this.height) {
                this.displayScreen(button, 1);
            }
        } else if (k == 3) {
            if (b1) {
                this.displayScreen(button, 0);
            } else if (b2) {
                this.displayScreen(button, 1);
            } else if (xMore && x <= i + this.width && y >= j + h2 + 1 && y <= j + this.height) {
                this.displayScreen(button, 2);
            }
        } else if (k == 4) {
            if (xMore && x <= i + w2 && yMore && y <= j + h2) {
                this.displayScreen(button, 0);
            } else if (b2) {
                this.displayScreen(button, 1);
            } else if (xMore && x <= i + w2 && y >= j + h2 + 1 && y <= j + this.height) {
                this.displayScreen(button, 2);
            } else if (x >= i + w2 + 1 && x <= i + this.width && y >= j + h2 + 1 && y <= j + this.height) {
                this.displayScreen(button, 3);
            }
        }
    }

    protected boolean displayScreen(int mouseButton, int index) {
        if (mouseButton == 0) {
            this.minecraft.setScreen(new TwitterPhotoAndShowStatusScreen(this.parent.twitterScreen, this.parent.getMain(), index));
        } else if (mouseButton == 1) {
            //TODO save picture action;
        }

        return true;
    }
}
