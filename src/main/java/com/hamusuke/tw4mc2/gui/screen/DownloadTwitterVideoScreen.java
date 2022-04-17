package com.hamusuke.tw4mc2.gui.screen;

import com.hamusuke.tw4mc2.download.FileDownload;
import com.hamusuke.tw4mc2.gui.filechooser.FileChooserSave;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.UploadSpeed;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Locale;

public class DownloadTwitterVideoScreen extends ParentalScreen {
    private final String videoUrl;
    private final MutableObject<File> saveTo = new MutableObject<>();
    private final FileDownload fileDownload;
    private final MutableBoolean started = new MutableBoolean();
    private final FileChooserSave fileChooserSave;
    private int tickCount;
    private long previousWrittenBytes;
    private long previousTime;
    private long bytesPerSecond;
    private Button selectFile;
    private Button cancel;

    public DownloadTwitterVideoScreen(@Nullable Screen parent, TweetSummary tweetSummary) {
        super(new TranslationTextComponent("tw.video.download.title"), parent);
        this.saveTo.setValue(SystemUtils.getUserHome().toPath().resolve("twitter_status_id_" + tweetSummary.getId() + "_video.mp4").toFile());
        this.fileChooserSave = new FileChooserSave(file -> {
            if (file == null) {
                return;
            }

            this.saveTo.setValue(file);
        }, this.saveTo.getValue());
        this.videoUrl = tweetSummary.getVideoURL();
        this.fileDownload = new FileDownload(this.videoUrl);
    }

    @Override
    protected void init() {
        this.selectFile = this.addButton(new Button(0, this.height - 40, this.width / 2, 20, new TranslationTextComponent("tw.select.file"), button -> this.fileChooserSave.choose()));
        this.selectFile.active = !this.started.booleanValue();

        this.addButton(new Button(0, this.height - 20, this.width / 2, 20, DialogTexts.GUI_BACK, button -> this.onClose()));

        this.addButton(new Button(this.width / 2, this.height - 40, this.width / 2, 20, new TranslationTextComponent("tw.video.download"), button -> {
            button.active = false;
            this.download();
            this.cancel.active = true;
            this.selectFile.active = false;
        })).active = !this.started.booleanValue();

        this.cancel = this.addButton(new Button(this.width / 2, this.height - 20, this.width / 2, 20, DialogTexts.GUI_CANCEL, button -> {
            button.active = false;
            this.fileDownload.cancel();
        }));
        this.cancel.active = this.started.booleanValue() && !this.fileDownload.finished() && !this.fileDownload.cancelled();

        super.init();
    }

    @Override
    public void tick() {
        this.tickCount++;
        this.cancel.active = this.started.booleanValue() && !this.fileDownload.finished() && !this.fileDownload.cancelled();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.parent != null) {
            matrices.pushPose();
            matrices.translate(0.0D, 0.0D, -1.0D);
            this.parent.render(matrices, -1, -1, delta);
            matrices.popPose();
        }

        if (this.minecraft.screen == this) {
            this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        }

        drawCenteredString(matrices, this.font, this.title, this.width / 2, 20, 16777215);
        drawCenteredString(matrices, this.font, "URL:" + this.videoUrl, this.width / 2, 40, 16777215);
        drawCenteredString(matrices, this.font, new TranslationTextComponent("selectWorld.resultFolder").append(" ").append(this.saveTo.getValue().getAbsolutePath()), this.width / 2, 50, 16777215);
        if (this.fileDownload.bytesWritten() != 0L && !this.fileDownload.cancelled()) {
            this.renderProgressBar(matrices);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    private void renderProgressBar(MatrixStack matrices) {
        double d = Math.min((double) this.fileDownload.bytesWritten() / (double) this.fileDownload.totalBytes(), 1.0D);
        String progress = String.format(Locale.ROOT, "%.1f", d * 100.0D);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        double e = (double) this.width / 4;
        double y = (double) this.height / 2;
        bufferBuilder.vertex(e - 0.5D, y + 10.5D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(e + (double) this.width / 2 + 0.5D, y + 10.5D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(e + (double) this.width / 2 + 0.5D, y - 0.5D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(e - 0.5D, y - 0.5D, 0.0D).color(255, 255, 255, 255).endVertex();
        bufferBuilder.vertex(e, y + 10.0D, 0.0D).color(0, 255, 0, 255).endVertex();
        bufferBuilder.vertex(e + (double) this.width / 2 * d, y + 10.0D, 0.0D).color(0, 255, 0, 255).endVertex();
        bufferBuilder.vertex(e + (double) this.width / 2 * d, y, 0.0D).color(0, 255, 0, 255).endVertex();
        bufferBuilder.vertex(e, y, 0.0D).color(0, 255, 0, 255).endVertex();
        tessellator.end();
        RenderSystem.enableTexture();
        drawCenteredString(matrices, this.font, progress + "%" + this.updateAndReturnDownloadSpeedString(), this.width / 2, (int) (y - 10.0D), 16777215);
    }

    private String updateAndReturnDownloadSpeedString() {
        if (!this.fileDownload.finished() && this.tickCount % 20 == 0) {
            long l = MathHelper.clamp(Util.getMillis() - this.previousTime, 1L, Long.MAX_VALUE);
            this.bytesPerSecond = 1000L * (this.fileDownload.bytesWritten() - this.previousWrittenBytes) / l;
            this.previousWrittenBytes = this.fileDownload.bytesWritten();
            this.previousTime = Util.getMillis();
        }

        if (this.bytesPerSecond > 0L) {
            return "(" + UploadSpeed.humanReadable(this.bytesPerSecond) + "/s)";
        }

        return "";
    }

    private synchronized void download() {
        if (this.saveTo.getValue() != null && this.started.isFalse()) {
            this.started.setTrue();
            this.fileDownload.download(this.saveTo.getValue());
        }
    }

    public FileDownload getFileDownload() {
        return this.fileDownload;
    }
}
