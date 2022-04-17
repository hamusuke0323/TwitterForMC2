package com.hamusuke.tw4mc2.gui.widget.list;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.list.ExtendedList;

import java.util.List;

public class WidgetList extends ExtendedList<WidgetList.AbstractButtonEntry> {
    public WidgetList(Minecraft client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.centerListVertically = false;
    }

    @Override
    public int addEntry(AbstractButtonEntry entry) {
        return super.addEntry(entry);
    }

    public void addEntry(Widget widget) {
        AbstractButtonEntry entry = new AbstractButtonEntry();
        entry.addWidget(widget);
        this.addEntry(entry);
    }

    public static class AbstractButtonEntry extends ExtendedList.AbstractListEntry<WidgetList.AbstractButtonEntry> {
        private final List<Widget> buttons = Lists.newArrayList();

        public <T extends Widget> T addWidget(T button) {
            this.buttons.add(button);
            return button;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.buttons.forEach(button -> {
                button.y = y;
                button.render(matrices, mouseX, mouseY, tickDelta);
            });
        }

        @Override
        public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
            for (Widget widget : this.buttons) {
                if (widget.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_)) {
                    return true;
                }
            }

            return false;
        }
    }
}
