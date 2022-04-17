package com.hamusuke.tw4mc2.gui.toasts;

import net.minecraft.client.gui.toasts.IToast;

public interface ClickableToast extends IToast {
    default void mouseClicked(int toastX, int toastY, double x, double y, int button) {
    }

    default void mouseReleased(int toastX, int toastY, double x, double y, int button) {
    }
}
