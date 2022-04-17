package com.hamusuke.tw4mc2.license;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public final class License {
    private final ResourceLocation textLocation;
    private final List<String> text;
    private final int width;
    private final TranslationTextComponent translatableText;
    private final String translationKey;

    public License(ResourceLocation textLocation, List<String> text, int width, String translationKey) {
        this.textLocation = textLocation;
        this.text = text;
        this.width = width;
        this.translationKey = translationKey;
        this.translatableText = new TranslationTextComponent(this.translationKey);
    }

    public ResourceLocation getTextLocation() {
        return this.textLocation;
    }

    public List<String> getLicenseTextList() {
        return this.text;
    }

    public int getWidth() {
        return this.width;
    }

    public TranslationTextComponent getTranslationText() {
        return this.translatableText;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }
}
