package com.hamusuke.tw4mc2.gui.toasts;

import net.minecraft.client.gui.toasts.IToast;

import javax.annotation.Nullable;
import java.io.InputStream;

public abstract class InputStreamToast implements IToast {
	@Nullable
	protected final InputStream image;

	public InputStreamToast(@Nullable InputStream image) {
		this.image = image;
	}
}
