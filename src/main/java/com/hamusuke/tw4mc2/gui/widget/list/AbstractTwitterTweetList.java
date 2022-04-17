package com.hamusuke.tw4mc2.gui.widget.list;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class AbstractTwitterTweetList<E extends AbstractTwitterTweetList.AbstractTwitterListEntry<E>> extends FocusableGui implements IRenderable {
	protected final Minecraft minecraft;
	private final SimpleArrayList children = new SimpleArrayList();
	protected int width;
	protected int height;
	protected int top;
	protected int bottom;
	protected int right;
	protected int left;
	protected int yDrag = -2;
	protected boolean renderSelection = true;
	protected boolean renderHeader;
	protected int headerHeight;
	private int averageHeight;
	private double scrollAmount;
	private boolean scrolling;
	private E selected;
	private int allHeight;
	private boolean renderHorizontalShadows = true;

	public AbstractTwitterTweetList(Minecraft mcIn, int width, int height, int top, int bottom) {
		this.minecraft = mcIn;
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setRenderSelection(boolean p_setRenderSelection_1_) {
		this.renderSelection = p_setRenderSelection_1_;
	}

	protected void setRenderHeader(boolean p_setRenderHeader_1_, int p_setRenderHeader_2_) {
		this.renderHeader = p_setRenderHeader_1_;
		this.headerHeight = p_setRenderHeader_2_;

		if (!p_setRenderHeader_1_) {
			this.headerHeight = 0;
		}
	}

	public void tick() {
	}

	public int getRowWidth() {
		return 220;
	}

	@Nullable
	public E getSelected() {
		return this.selected;
	}

	public void setSelected(@Nullable E entry) {
		this.selected = entry;
	}

	@Override
	@Nullable
	public E getFocused() {
		return (E) (super.getFocused());
	}

	@Override
	public List<E> children() {
		return this.children;
	}

	public final void clearEntries() {
		this.children.forEach(E::onRemove);
		this.children.clear();
		this.allHeight = 0;
	}

	protected void replaceEntries(Collection<E> p_replaceEntries_1_) {
		this.children.forEach(E::onRemove);
		this.children.clear();
		p_replaceEntries_1_.forEach(E::init);
		this.children.addAll(p_replaceEntries_1_);
		this.calcAllHeight();
		this.calcAverage();
	}

	public void setRenderHorizontalShadows(boolean renderHorizontalShadows) {
		this.renderHorizontalShadows = renderHorizontalShadows;
	}

	protected E getEntry(int p_getEntry_1_) {
		return this.children().get(p_getEntry_1_);
	}

	public int addEntry(E p_addEntry_1_) {
		p_addEntry_1_.init();
		this.children.add(p_addEntry_1_);
		this.allHeight += p_addEntry_1_.getHeight();
		this.calcAverage();
		this.setY(0);
		return this.children.size() - 1;
	}

	protected int getItemCount() {
		return this.children().size();
	}

	protected boolean isSelectedItem(int p_isSelectedItem_1_) {
		return Objects.equals(this.getSelected(), this.children().get(p_isSelectedItem_1_));
	}

	@Nullable
	protected final E getEntryAtPosition(double x, double y) {
		int i = this.getRowWidth() / 2;
		int j = this.left + this.width / 2;
		int k = j - i;
		int l = j + i;
		boolean flag = x < (double) this.getScrollbarPositionX() && x >= (double) k && x <= (double) l;
		if (flag) {
			for (E e : this.children()) {
				int ey = e.getY();
				int eyheight = ey + e.getHeight();
				if (ey <= (int) y && eyheight >= (int) y) {
					return e;
				}
			}
		}

		return null;
	}

	public void updateSize(int width, int height, int top, int bottom) {
		this.width = width;
		this.height = height;
		this.top = top;
		this.bottom = bottom;
		this.left = 0;
		this.right = width;
	}

	public void setLeftPos(int left) {
		this.left = left;
		this.right = left + this.width;
	}

	public void setLeftRightPos(int left, int right) {
		this.left = left;
		this.right = right;
	}

	protected int getMaxPosition() {
		return this.allHeight + this.headerHeight;
	}

	protected void clickedHeader(int p_clickedHeader_1_, int p_clickedHeader_2_) {
	}

	protected void renderHeader(MatrixStack matrices, int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
	}

	protected void renderBackground(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		this.minecraft.textureManager.bind(BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.vertex(this.left, this.bottom, 0.0D).uv((float) this.left / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(this.right, this.bottom, 0.0D).uv((float) this.right / 32.0F, (float) (this.bottom + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(this.right, this.top, 0.0D).uv((float) this.right / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
		bufferbuilder.vertex(this.left, this.top, 0.0D).uv((float) this.left / 32.0F, (float) (this.top + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
		tessellator.end();
	}

	protected void renderDecorations(MatrixStack matrices, int p_renderDecorations_1_, int p_renderDecorations_2_) {
	}

	@Override
	public void render(MatrixStack matrices, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(matrices);
		int i = this.getScrollbarPositionX();
		int j = i + 6;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		int k = this.getRowLeft();
		int l = this.top + 4 - (int) this.getScrollAmount();

		if (this.renderHeader) {
			this.renderHeader(matrices, k, l, tessellator);
		}

		this.renderList(matrices, k, l, p_render_1_, p_render_2_, p_render_3_);
		if (this.renderHorizontalShadows) {
			this.minecraft.textureManager.bind(BACKGROUND_LOCATION);
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(519);
			float g = 32.0F;
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferBuilder.vertex(this.left, this.top, -100.0D).uv(0.0F, (float) this.top / 32.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left + this.width, this.top, -100.0D).uv((float) this.width / 32.0F, (float) this.top / 32.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left + this.width, 0.0D, -100.0D).uv((float) this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left, this.height, -100.0D).uv(0.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left + this.width, this.height, -100.0D).uv((float) this.width / 32.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left + this.width, this.bottom, -100.0D).uv((float) this.width / 32.0F, (float) this.bottom / 32.0F).color(64, 64, 64, 255).endVertex();
			bufferBuilder.vertex(this.left, this.bottom, -100.0D).uv(0.0F, (float) this.bottom / 32.0F).color(64, 64, 64, 255).endVertex();
			tessellator.end();
			RenderSystem.depthFunc(515);
			RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
			RenderSystem.disableTexture();
			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(this.left, this.top + 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferBuilder.vertex(this.right, this.top + 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferBuilder.vertex(this.right, this.top, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(this.left, this.top, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(this.left, this.bottom, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(this.right, this.bottom, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(this.right, this.bottom - 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferBuilder.vertex(this.left, this.bottom - 4, 0.0D).color(0, 0, 0, 0).endVertex();
			tessellator.end();
		}

		int j1 = this.getMaxScroll();
		if (j1 > 0) {
			RenderSystem.disableTexture();
			int k1 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getMaxPosition());
			k1 = MathHelper.clamp(k1, 32, this.bottom - this.top - 8);
			int l1 = (int) this.getScrollAmount() * (this.bottom - this.top - k1) / j1 + this.top;

			if (l1 < this.top) {
				l1 = this.top;
			}

			bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(i, this.bottom, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(j, this.bottom, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(j, this.top, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(i, this.top, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferBuilder.vertex(i, l1 + k1, 0.0D).color(128, 128, 128, 255).endVertex();
			bufferBuilder.vertex(j, l1 + k1, 0.0D).color(128, 128, 128, 255).endVertex();
			bufferBuilder.vertex(j, l1, 0.0D).color(128, 128, 128, 255).endVertex();
			bufferBuilder.vertex(i, l1, 0.0D).color(128, 128, 128, 255).endVertex();
			bufferBuilder.vertex(i, l1 + k1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
			bufferBuilder.vertex(j - 1, l1 + k1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
			bufferBuilder.vertex(j - 1, l1, 0.0D).color(192, 192, 192, 255).endVertex();
			bufferBuilder.vertex(i, l1, 0.0D).color(192, 192, 192, 255).endVertex();
			tessellator.end();
		}

		this.renderDecorations(matrices, p_render_1_, p_render_2_);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	protected void centerScrollOn(E p_centerScrollOn_1_) {
		this.setScrollAmount(this.countBefore(this.children().indexOf(p_centerScrollOn_1_)) + (float) (p_centerScrollOn_1_.getHeight() / 2 - (this.bottom - this.top) / 2));
	}

	protected void ensureVisible(E entry) {
		int i = this.getRowTop(this.children().indexOf(entry));
		int j = i - this.top - 4 - entry.getHeight();

		if (j < 0) {
			this.scroll(j);
		}

		int k = this.bottom - i - (entry.getHeight() * 2);

		if (k < 0) {
			this.scroll(-k);
		}
	}

	public boolean isEntryVisible(E entry) {
		int index = this.children.indexOf(entry);
		return index != -1 && this.getRowBottom(index) >= this.top && this.getRowTop(index) <= this.bottom;
	}

	private void scroll(int p_scroll_1_) {
		this.setScrollAmount(this.getScrollAmount() + (double) p_scroll_1_);
		this.yDrag = -2;
	}

	public double getScrollAmount() {
		return this.scrollAmount;
	}

	public void setScrollAmount(double p_setScrollAmount_1_) {
		this.scrollAmount = MathHelper.clamp(p_setScrollAmount_1_, 0.0D, this.getMaxScroll());
		this.setY(-(int) this.scrollAmount);
	}

	protected int getMaxScroll() {
		return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
	}

	public int getScrollBottom() {
		return (int) this.getScrollAmount() - this.height - this.headerHeight;
	}

	protected void updateScrollingState(double mouseX, double mouseY, int mouseButton) {
		this.scrolling = mouseButton == 0 && mouseX >= (double) this.getScrollbarPositionX() && mouseX < (double) (this.getScrollbarPositionX() + 6);
	}

	protected int getScrollbarPositionX() {
		return this.width / 2 + 124;
	}

	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		this.updateScrollingState(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);

		if (!this.isMouseOver(p_mouseClicked_1_, p_mouseClicked_3_)) {
			return false;
		} else {
			E e = this.getEntryAtPosition(p_mouseClicked_1_, p_mouseClicked_3_);

			if (e != null) {
				if (e.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
					this.setFocused(e);
					this.setDragging(true);
					return true;
				}
			} else if (p_mouseClicked_5_ == 0) {
				this.clickedHeader((int) (p_mouseClicked_1_ - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)), (int) (p_mouseClicked_3_ - (double) this.top) + (int) this.getScrollAmount() - 4);
				return true;
			}

			return this.scrolling;
		}
	}

	@Override
	public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
		if (this.getFocused() != null) {
			this.getFocused().mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
		}

		return false;
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		if (super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_)) {
			return true;
		} else if (p_mouseDragged_5_ == 0 && this.scrolling) {
			if (p_mouseDragged_3_ < (double) this.top) {
				this.setScrollAmount(0.0D);
			} else if (p_mouseDragged_3_ > (double) this.bottom) {
				this.setScrollAmount(this.getMaxScroll());
			} else {
				double d0 = Math.max(1, this.getMaxScroll());
				int i = this.bottom - this.top;
				int j = MathHelper.clamp((int) ((float) (i * i) / (float) this.getMaxPosition()), 32, i - 8);
				double d1 = Math.max(1.0D, d0 / (double) (i - j));
				this.setScrollAmount(this.getScrollAmount() + p_mouseDragged_8_ * d1);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
		this.setScrollAmount(this.getScrollAmount() - p_mouseScrolled_5_ * (double) this.averageHeight / 2.0D);
		return true;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		} else if (p_keyPressed_1_ == 264) {
			this.moveSelection(1);
			return true;
		} else if (p_keyPressed_1_ == 265) {
			this.moveSelection(-1);
			return true;
		} else {
			return false;
		}
	}

	protected void moveSelection(int inc) {
		if (!this.children().isEmpty()) {
			int i = this.children().indexOf(this.getSelected());
			int j = MathHelper.clamp(i + inc, 0, this.getItemCount() - 1);
			E e = this.children().get(j);
			this.setSelected(e);
		}
	}

	@Override
	public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
		return p_isMouseOver_3_ >= (double) this.top && p_isMouseOver_3_ <= (double) this.bottom && p_isMouseOver_1_ >= (double) this.left && p_isMouseOver_1_ <= (double) this.right;
	}

	protected void renderList(MatrixStack matrices, int p_renderList_1_, int p_renderList_2_, int p_renderList_3_, int p_renderList_4_, float p_renderList_5_) {
		int i = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();

		for (int j = 0; j < i; ++j) {
			int k = this.getRowTop(j);
			int l = this.getRowBottom(j);

			if (l >= this.top && k <= this.bottom) {
				E e = this.getEntry(j);
				int i1 = p_renderList_2_ + this.countBefore(j) + this.headerHeight;
				int j1 = e.getHeight() - 4;
				int k1 = this.getRowWidth();

				if (this.renderSelection && this.isSelectedItem(j)) {
					int l1 = this.left + this.width / 2 - k1 / 2;
					int i2 = this.left + this.width / 2 + k1 / 2;
					RenderSystem.disableTexture();
					float f = this.isFocused() ? 1.0F : 0.5F;
					RenderSystem.color4f(f, f, f, 1.0F);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
					bufferbuilder.vertex(l1, i1 + j1 + 2, 0.0D).endVertex();
					bufferbuilder.vertex(i2, i1 + j1 + 2, 0.0D).endVertex();
					bufferbuilder.vertex(i2, i1 - 2, 0.0D).endVertex();
					bufferbuilder.vertex(l1, i1 - 2, 0.0D).endVertex();
					tessellator.end();
					RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
					bufferbuilder.vertex(l1 + 1, i1 + j1 + 1, 0.0D).endVertex();
					bufferbuilder.vertex(i2 - 1, i1 + j1 + 1, 0.0D).endVertex();
					bufferbuilder.vertex(i2 - 1, i1 - 1, 0.0D).endVertex();
					bufferbuilder.vertex(l1 + 1, i1 - 1, 0.0D).endVertex();
					tessellator.end();
					RenderSystem.enableTexture();
				}

				int j2 = this.getRowLeft();
				e.render(matrices, j, k, j2, k1, j1, p_renderList_3_, p_renderList_4_, this.isMouseOver(p_renderList_3_, p_renderList_4_) && Objects.equals(this.getEntryAtPosition(p_renderList_3_, p_renderList_4_), e), p_renderList_5_);
			}
		}
	}

	protected int getRowLeft() {
		return this.left + this.width / 2 - this.getRowWidth() / 2 + 2;
	}

	protected int getRowTop(int index) {
		return this.top + 4 - (int) this.getScrollAmount() + this.countBefore(index) + this.headerHeight;
	}

	protected int getRowBottom(int index) {
		return this.getRowTop(index) + this.children().get(index).getHeight();
	}

	protected boolean isFocused() {
		return false;
	}

	protected void renderHoleBackground(MatrixStack matrices, int top, int bottom, int alphaTop, int alphaBottom) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		this.minecraft.textureManager.bind(BACKGROUND_LOCATION);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.vertex(this.left, bottom, 0.0D).uv(0.0F, (float) bottom / 32.0F).color(64, 64, 64, alphaBottom).endVertex();
		bufferbuilder.vertex(this.left + this.width, bottom, 0.0D).uv((float) this.width / 32.0F, (float) bottom / 32.0F).color(64, 64, 64, alphaBottom).endVertex();
		bufferbuilder.vertex(this.left + this.width, top, 0.0D).uv((float) this.width / 32.0F, (float) top / 32.0F).color(64, 64, 64, alphaTop).endVertex();
		bufferbuilder.vertex(this.left, top, 0.0D).uv(0.0F, (float) top / 32.0F).color(64, 64, 64, alphaTop).endVertex();
		tessellator.end();
	}

	protected E remove(int p_remove_1_) {
		E e = this.children.get(p_remove_1_);
		return this.removeEntry(this.children.get(p_remove_1_)) ? e : null;
	}

	protected boolean removeEntry(E p_removeEntry_1_) {
		boolean flag = this.children.remove(p_removeEntry_1_);

		if (flag) {
			this.allHeight -= p_removeEntry_1_.getHeight();
			if (p_removeEntry_1_ == this.getSelected()) {
				this.setSelected(null);
			}
			p_removeEntry_1_.onRemove();
		}

		return flag;
	}

	protected int countBefore(int index) {
		int j = 0;
		for (int i = 0; i < index; i++) {
			j += this.children().get(i).getHeight();
		}
		return j;
	}

	protected void calcAllHeight() {
		this.allHeight = 0;
		for (E e : this.children()) {
			this.allHeight += e.getHeight();
		}
	}

	protected void calcAverage() {
		this.averageHeight = this.allHeight / this.children().size();
	}

	protected void setY(int topY) {
		for (int i = 0; i < this.children().size(); i++) {
			this.children().get(i).setY(this.top + topY + this.countBefore(i));
		}
	}

	public abstract static class AbstractTwitterListEntry<E extends AbstractTwitterTweetList.AbstractTwitterListEntry<E>> implements TweetElement {
		protected final List<Widget> buttons = Lists.newArrayList();
		protected final List<Widget> overlayButtons = Lists.newArrayList();
		@Deprecated
		AbstractTwitterTweetList<E> list;

		public void init() {
		}

		public void tick() {
		}

		public void render(MatrixStack matrices, int itemIndex, int rowTop, int rowLeft, int rowWidth, int height2, int mouseX, int mouseY, boolean isMouseOverAndObjectEquals, float delta) {
		}

		public void renderButtons(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			for (Widget button : this.buttons) {
				button.render(matrices, mouseX, mouseY, delta);
			}
		}

		public void renderOverlayButtons(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			for (Widget button : this.overlayButtons) {
				button.render(matrices, mouseX, mouseY, delta);
			}
		}

		public void onRemove() {
		}

		@Override
		public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
			return Objects.equals(this.list.getEntryAtPosition(p_isMouseOver_1_, p_isMouseOver_3_), this);
		}

		protected <T extends Widget> T addButton(T widget) {
			this.buttons.add(widget);
			return widget;
		}

		protected <T extends Widget> T addOverlayButton(T widget) {
			this.overlayButtons.add(widget);
			return widget;
		}

		public ImmutableList<Widget> getOverlayButtons() {
			return ImmutableList.copyOf(this.overlayButtons);
		}
	}

	class SimpleArrayList extends AbstractList<E> {
		private final List<E> field_216871_b = Lists.newArrayList();

		private SimpleArrayList() {
		}

		@Override
		public E get(int p_get_1_) {
			return this.field_216871_b.get(p_get_1_);
		}

		@Override
		public int size() {
			return this.field_216871_b.size();
		}

		@Override
		public E set(int p_set_1_, E p_set_2_) {
			E e = this.field_216871_b.set(p_set_1_, p_set_2_);
			p_set_2_.list = AbstractTwitterTweetList.this;
			return e;
		}

		@Override
		public void add(int p_add_1_, E p_add_2_) {
			this.field_216871_b.add(p_add_1_, p_add_2_);
			p_add_2_.list = AbstractTwitterTweetList.this;
		}

		@Override
		public E remove(int p_remove_1_) {
			return this.field_216871_b.remove(p_remove_1_);
		}
	}
}
