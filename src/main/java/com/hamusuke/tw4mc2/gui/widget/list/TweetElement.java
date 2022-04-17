package com.hamusuke.tw4mc2.gui.widget.list;

import net.minecraft.client.gui.IGuiEventListener;

public interface TweetElement extends IGuiEventListener {
	void setHeight(int height);

	int getHeight();

	int getY();

	void setY(int y);
}
