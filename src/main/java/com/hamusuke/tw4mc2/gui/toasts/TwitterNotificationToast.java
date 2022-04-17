package com.hamusuke.tw4mc2.gui.toasts;

import com.hamusuke.tw4mc2.invoker.TextRendererInvoker;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.List;

public class TwitterNotificationToast extends InputStreamToast implements ClickableToast {
	private final ITextComponent title;
	@Nullable
	private final ITextComponent subtitle;

	public TwitterNotificationToast(@Nullable InputStream image, ITextComponent title, @Nullable ITextComponent subtitle) {
		super(image);
		this.title = title;
		this.subtitle = subtitle;
	}

	public TwitterNotificationToast(@Nullable InputStream image, ITextComponent title) {
		this(image, title, null);
	}

	static TextRendererInvoker getInvoker(FontRenderer textRenderer) {
		return (TextRendererInvoker) textRenderer;
	}

	@Override
	public void mouseClicked(int toastX, int toastY, double x, double y, int button) {
		LogManager.getLogger().info("x: {}, y: {}", toastX, toastY);
	}

	@Override
	public Visibility render(MatrixStack matrices, ToastGui toastGui, long delta) {
		toastGui.getMinecraft().textureManager.bind(TEXTURE);
		RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
		toastGui.blit(matrices, 0, 0, 0, 0, 160, 32);

		if (this.subtitle == null) {
			getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, this.title, 30.0F, 12.0F, -1);
		} else {
			List<IReorderingProcessor> list = getInvoker(toastGui.getMinecraft().font).wrapLinesWithEmoji(this.subtitle, 125);
			if (list.size() == 1) {
				getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, this.title, 30.0F, 7.0F, 16777215);
				getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, this.subtitle, 30.0F, 18.0F, 16777215);
			} else {
				if (delta < 1500L) {
					int k = MathHelper.floor(MathHelper.clamp((float) (1500L - delta) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
					getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, this.title, 30.0F, 7.0F, 16777215 | k);
					getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, this.subtitle, 30.0F, 18.0F, 16777215 | k);
				} else {
					int i1 = MathHelper.floor(MathHelper.clamp((float) (delta - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
					int l = 16 - list.size() * 9 / 2;

					for (IReorderingProcessor text : list) {
						getInvoker(toastGui.getMinecraft().font).drawWithEmoji(matrices, text, 30.0F, (float) l, 16777215 | i1);
						l += 9;
					}
				}
			}
		}

		if (this.image != null) {
			matrices.pushPose();
			RenderSystem.enableBlend();
			RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
			matrices.translate(8.0D, 8.0D, 0.0D);
			TwitterForMC2.getInstance().getTextureManager().bindTexture(this.image);
			AbstractGui.blit(matrices, 0, 0, 0.0F, 0.0F, 16, 16, 16, 16);
			matrices.popPose();
		}

		return delta < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
	}
}
