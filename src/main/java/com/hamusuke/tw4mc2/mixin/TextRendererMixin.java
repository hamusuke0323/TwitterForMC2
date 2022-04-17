package com.hamusuke.tw4mc2.mixin;

import com.google.common.collect.ImmutableList;
import com.hamusuke.tw4mc2.font.TweetTextDrawer;
import com.hamusuke.tw4mc2.invoker.TextHandlerInvoker;
import com.hamusuke.tw4mc2.invoker.TextRendererInvoker;
import com.hamusuke.tw4mc2.text.TweetTextUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.CharacterManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Function;

@Mixin(FontRenderer.class)
public abstract class TextRendererMixin implements TextRendererInvoker {
    @Shadow
    @Final
    private CharacterManager splitter;

    @Shadow
    public abstract boolean isBidirectional();

    @Shadow
    private static int adjustColor(int p_238403_0_) {
        return 0;
    }

    @Shadow
    @Final
    private static Vector3f SHADOW_OFFSET;

    @Shadow
    @Final
    private Function<ResourceLocation, Font> fonts;

    @Override
    public int drawWithShadowAndEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color) {
        return this.drawWithEmoji(text.getVisualOrderText(), x, y, color, matrices.last().pose(), true);
    }

    @Override
    public int drawWithEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color) {
        return this.drawWithEmoji(text.getVisualOrderText(), x, y, color, matrices.last().pose(), false);
    }

    @Override
    public int drawWithShadowAndEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color) {
        return this.drawWithEmoji(text, x, y, color, matrices.last().pose(), true);
    }

    @Override
    public int drawWithEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color) {
        return this.drawWithEmoji(text, x, y, color, matrices.last().pose(), false);
    }

    private int drawWithEmoji(IReorderingProcessor text, float x, float y, int color, Matrix4f matrix, boolean shadow) {
        IRenderTypeBuffer.Impl immediate = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        int i = this.drawInternalWithEmoji(text, x, y, color, shadow, matrix, immediate, false, 0, 15728880);
        immediate.endBatch();
        return i;
    }

    @Override
    public int drawWithEmoji(ITextComponent text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        return this.drawInternalWithEmoji(text.getVisualOrderText(), x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
    }

    @Override
    public int drawWithEmoji(IReorderingProcessor text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        return this.drawInternalWithEmoji(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
    }

    @Override
    public int getWidthWithEmoji(IReorderingProcessor text) {
        return MathHelper.ceil(((TextHandlerInvoker) this.splitter).getWidthWithEmoji(text));
    }

    @Override
    public List<IReorderingProcessor> wrapLinesWithEmoji(ITextProperties text, int width) {
        return this.splitter.splitLines(text, width, Style.EMPTY).stream().map(stringVisitable -> TweetTextUtil.reorderIgnoreStyleChar(stringVisitable, this.isBidirectional())).collect(ImmutableList.toImmutableList());
    }

    private int drawInternalWithEmoji(IReorderingProcessor text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumers, boolean seeThrough, int backgroundColor, int light) {
        color = adjustColor(color);
        Matrix4f matrix4f = matrix.copy();
        if (shadow) {
            this.drawLayerWithEmoji(text, x, y, color, true, matrix, vertexConsumers, seeThrough, backgroundColor, light);
            matrix4f.translate(SHADOW_OFFSET);
        }

        x = this.drawLayerWithEmoji(text, x, y, color, false, matrix4f, vertexConsumers, seeThrough, backgroundColor, light);
        return (int) x + (shadow ? 1 : 0);
    }

    private float drawLayerWithEmoji(IReorderingProcessor text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumerProvider, boolean seeThrough, int underlineColor, int light) {
        TweetTextDrawer drawer = new TweetTextDrawer(this.fonts, vertexConsumerProvider, x, y, color, shadow, matrix, seeThrough, light);
        text.accept(drawer);
        return drawer.drawLayer(underlineColor, x);
    }
}
