package com.hamusuke.tw4mc2.gui.widget;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public class FunctionalButtonWidget extends Button {
    public final Function<Integer, Integer> yFunction;

    public FunctionalButtonWidget(int x, int y, int width, int height, ITextComponent message, IPressable onPress, Function<Integer, Integer> yFunction) {
        super(x, y, width, height, message, onPress);
        this.yFunction = yFunction;
    }

    public FunctionalButtonWidget(int x, int y, int width, int height, ITextComponent message, IPressable onPress, ITooltip tooltipSupplier, Function<Integer, Integer> yFunction) {
        super(x, y, width, height, message, onPress, tooltipSupplier);
        this.yFunction = yFunction;
    }


}
