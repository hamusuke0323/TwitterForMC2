package com.hamusuke.tw4mc2.invoker;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

import java.util.List;

public interface TextRendererInvoker {
    int drawWithShadowAndEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color);

    int drawWithEmoji(MatrixStack matrices, ITextComponent text, float x, float y, int color);

    int drawWithEmoji(ITextComponent text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumers, boolean seeThrough, int backgroundColor, int light);

    int drawWithShadowAndEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color);

    int drawWithEmoji(MatrixStack matrices, IReorderingProcessor text, float x, float y, int color);

    int drawWithEmoji(IReorderingProcessor text, float x, float y, int color, boolean shadow, Matrix4f matrix, IRenderTypeBuffer vertexConsumers, boolean seeThrough, int backgroundColor, int light);

    int getWidthWithEmoji(IReorderingProcessor text);

    List<IReorderingProcessor> wrapLinesWithEmoji(ITextProperties text, int width);
}
