package com.hamusuke.tw4mc2.texture;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TextureManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<InputStream, Texture> textureMap = Maps.newHashMap();

    public void bindTexture(InputStream is) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.bindTextureRaw(is));
        } else {
            this.bindTextureRaw(is);
        }
    }

    private void bindTextureRaw(InputStream inputStream) {
        Texture texture = this.textureMap.get(inputStream);
        if (texture == null) {
            texture = new InputStreamTexture();
            this.registerTexture(inputStream, texture);
        }

        texture.bind();
    }

    public void registerTexture(InputStream inputStream, Texture texture) {
        texture = this.loadTexture(inputStream, texture);
        Texture texture1 = this.textureMap.put(inputStream, texture);
        if (texture1 != texture) {
            if (texture1 != null && texture1 != MissingTextureSprite.getTexture()) {
                texture1.releaseId();
            }
        }
    }

    private Texture loadTexture(InputStream inputStream, Texture texture) {
        try {
            ((InputStreamTexture) texture).load(inputStream);
            return texture;
        } catch (IOException var7) {
            LOGGER.warn("Failed to load InputStream texture", var7);
            return MissingTextureSprite.getTexture();
        } catch (Throwable var8) {
            CrashReport crashReport = CrashReport.forThrowable(var8, "Registering texture");
            CrashReportCategory crashReportSection = crashReport.addCategory("Resource location being registered");
            crashReportSection.setDetail("Texture object class", () -> texture.getClass().getName());
            throw new ReportedException(crashReport);
        }
    }
}
