package com.hamusuke.tw4mc2.text.emoji;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.Objects;

public final class Emoji {
    private final String hex;
    private final ResourceLocation id;

    public Emoji(String hex, ResourceLocation location) {
        this.hex = Objects.requireNonNull(hex, "hex cannot be null.");
        this.id = Objects.requireNonNull(location, "location cannot be null.");
    }

    public String getHex() {
        return this.hex;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public int getEmojiWidth() {
        return 9;
    }

    public void renderEmoji(Matrix4f matrix, IRenderTypeBuffer vertexConsumerProvider, float x, float y, float alpha, int light) {
        IVertexBuilder vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.text(this.id));
        vertexConsumer.vertex(matrix, x, y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix, x, y + this.getEmojiWidth(), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix, x + this.getEmojiWidth(), y + this.getEmojiWidth(), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix, x + this.getEmojiWidth(), y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(1.0F, 0.0F).uv2(light).endVertex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Emoji emoji = (Emoji) o;
        return this.hex.equals(emoji.hex) && this.id.equals(emoji.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.hex, this.id);
    }
}
