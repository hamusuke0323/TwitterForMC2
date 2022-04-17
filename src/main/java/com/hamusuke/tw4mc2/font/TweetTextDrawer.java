package com.hamusuke.tw4mc2.font;

import com.google.common.collect.Lists;
import com.hamusuke.tw4mc2.text.CharacterAndEmojiVisitor;
import com.hamusuke.tw4mc2.text.emoji.Emoji;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class TweetTextDrawer implements CharacterAndEmojiVisitor {
    private final Function<ResourceLocation, Font> fonts;
    final IRenderTypeBuffer bufferSource;
    private final boolean dropShadow;
    private final float dimFactor;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final Matrix4f matrix;
    private final boolean seeThrough;
    private final int light;
    private float x;
    private float y;
    @Nullable
    private List<TexturedGlyph.Effect> effects;

    private void addEffect(TexturedGlyph.Effect p_238442_1_) {
        if (this.effects == null) {
            this.effects = Lists.newArrayList();
        }

        this.effects.add(p_238442_1_);
    }

    public TweetTextDrawer(Function<ResourceLocation, Font> fonts, IRenderTypeBuffer buffer, float x, float y, int color, boolean dropShadow, Matrix4f matrix4f, boolean seeThrough, int light) {
        this.fonts = fonts;
        this.bufferSource = buffer;
        this.x = x;
        this.y = y;
        this.dropShadow = dropShadow;
        this.dimFactor = dropShadow ? 0.25F : 1.0F;
        this.red = (float) (color >> 16 & 255) / 255.0F * this.dimFactor;
        this.green = (float) (color >> 8 & 255) / 255.0F * this.dimFactor;
        this.blue = (float) (color & 255) / 255.0F * this.dimFactor;
        this.alpha = (float) (color >> 24 & 255) / 255.0F;
        this.matrix = matrix4f;
        this.seeThrough = seeThrough;
        this.light = light;
    }

    public float drawLayer(int p_238441_1_, float p_238441_2_) {
        if (p_238441_1_ != 0) {
            float f = (float) (p_238441_1_ >> 24 & 255) / 255.0F;
            float f1 = (float) (p_238441_1_ >> 16 & 255) / 255.0F;
            float f2 = (float) (p_238441_1_ >> 8 & 255) / 255.0F;
            float f3 = (float) (p_238441_1_ & 255) / 255.0F;
            this.addEffect(new TexturedGlyph.Effect(p_238441_2_ - 1.0F, this.y + 9.0F, this.x + 1.0F, this.y - 1.0F, 0.01F, f1, f2, f3, f));
        }

        if (this.effects != null) {
            TexturedGlyph texturedglyph = this.fonts.apply(Style.DEFAULT_FONT).whiteGlyph();
            IVertexBuilder ivertexbuilder = this.bufferSource.getBuffer(texturedglyph.renderType(this.seeThrough));

            for (TexturedGlyph.Effect texturedglyph$effect : this.effects) {
                texturedglyph.renderEffect(texturedglyph$effect, this.matrix, ivertexbuilder, this.light);
            }
        }

        return this.x;
    }

    @Override
    public boolean accept(int index, Style style, int codePoint) {
        Font fontStorage = this.fonts.apply(style.getFont());
        IGlyph glyph = codePoint == 12288 ? () -> 6.0F : fontStorage.getGlyphInfo(codePoint);
        TexturedGlyph glyphRenderer = style.isObfuscated() && codePoint != 32 && codePoint != 12288 ? fontStorage.getRandomGlyph(glyph) : codePoint == 12288 ? new EmptyGlyph() : fontStorage.getGlyph(codePoint);
        boolean bl = style.isBold();
        float f = this.alpha;
        Color textColor = style.getColor();
        float m;
        float n;
        float o;
        if (textColor != null) {
            int k = textColor.getValue();
            m = (float) (k >> 16 & 255) / 255.0F * this.dimFactor;
            n = (float) (k >> 8 & 255) / 255.0F * this.dimFactor;
            o = (float) (k & 255) / 255.0F * this.dimFactor;
        } else {
            m = this.red;
            n = this.green;
            o = this.blue;
        }

        float s;
        float r;
        if (!(glyphRenderer instanceof EmptyGlyph)) {
            r = bl ? glyph.getBoldOffset() : 0.0F;
            s = this.dropShadow ? glyph.getShadowOffset() : 0.0F;
            IVertexBuilder vertexConsumer = this.bufferSource.getBuffer(glyphRenderer.renderType(this.seeThrough));
            glyphRenderer.render(style.isItalic(), this.x + s, this.y + s, this.matrix, vertexConsumer, m, n, o, f, this.light);
            if (bl) {
                glyphRenderer.render(style.isItalic(), this.x + s + r, this.y + s, this.matrix, vertexConsumer, m, n, o, f, this.light);
            }
        }

        r = glyph.getAdvance(bl);
        s = this.dropShadow ? 1.0F : 0.0F;
        if (style.isStrikethrough()) {
            this.addEffect(new TexturedGlyph.Effect(this.x + s - 1.0F, this.y + s + 4.5F, this.x + s + r, this.y + s + 4.5F - 1.0F, 0.01F, m, n, o, f));
        }

        if (style.isUnderlined()) {
            this.addEffect(new TexturedGlyph.Effect(this.x + s - 1.0F, this.y + s + 9.0F, this.x + s + r, this.y + s + 9.0F - 1.0F, 0.01F, m, n, o, f));
        }

        this.x += r;
        return true;
    }

    @Override
    public boolean acceptEmoji(Emoji emoji) {
        emoji.renderEmoji(this.matrix, this.bufferSource, this.x, this.y, this.alpha, this.light);
        this.x += emoji.getEmojiWidth();
        return true;
    }
}
