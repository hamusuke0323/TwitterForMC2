package com.hamusuke.tw4mc2;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import nick1st.fancyvideo.api.MediaPlayer;
import nick1st.fancyvideo.api.MediaPlayers;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class TestVideoScreen extends Screen {
    @Nullable
    private final Screen parent;
    private final int id;
    private final MediaPlayer player;
    private boolean pausing;

    public TestVideoScreen(@Nullable Screen parent) {
        super(NarratorChatListener.NO_TITLE);
        this.parent = parent;
        this.id = MediaPlayer.getNew();
        this.player = MediaPlayers.getPlayer(id);
        this.player.prepare("file:///I:/Videos/4Kぞい！.mp4");
        this.player.volume(100);
        this.player.getTrueMediaPlayer().mediaPlayer().controls().setRepeat(true);
        this.player.playPrepared();
    }

    @Override
    protected void init() {

    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (p_231046_1_ == GLFW.GLFW_KEY_LEFT) {
            this.player.getTrueMediaPlayer().mediaPlayer().controls().skipTime(-5000L);
        } else if (p_231046_1_ == GLFW.GLFW_KEY_RIGHT) {
            this.player.getTrueMediaPlayer().mediaPlayer().controls().skipTime(5000L);
        }

        if (p_231046_1_ == GLFW.GLFW_KEY_SPACE) {
            this.player.getTrueMediaPlayer().mediaPlayer().controls().setPause(this.pausing = !this.pausing);
        }

        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        p_230430_1_.pushPose();
        this.player.bindFrame();
        RenderSystem.enableBlend();
        RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(p_230430_1_, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
        RenderSystem.disableBlend();
        p_230430_1_.popPose();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
        MediaPlayers.removePlayer(this.id);
    }
}
