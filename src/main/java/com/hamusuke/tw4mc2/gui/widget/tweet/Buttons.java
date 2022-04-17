package com.hamusuke.tw4mc2.gui.widget.tweet;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.widget.TwitterButton;
import net.minecraft.util.ResourceLocation;

public class Buttons extends TweetFramePiece {
    protected static final ResourceLocation REPLY = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/reply.png");
    protected static final ResourceLocation RETWEET = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweet.png");
    protected static final ResourceLocation RETWEETED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/retweeted.png");
    protected static final ResourceLocation FAVORITE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorite.png");
    protected static final ResourceLocation FAVORITED = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/favorited.png");
    protected static final ResourceLocation SHARE = new ResourceLocation(TwitterForMC2.MOD_ID, "textures/twitter/icon/share.png");

    private final TwitterButton reply;

    public Buttons(TweetFrame parent) {
        super(parent);
    }


}
