package com.hamusuke.tw4mc2.text;

import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import javax.annotation.Nullable;

public class TweetText extends TextComponent {
    private final String string;
    private IReorderingProcessor orderedText;
    @Nullable
    private LanguageMap previousLanguage;

    public TweetText(String string) {
        this.string = string;
        this.orderedText = IReorderingProcessor.EMPTY;
    }

    @Override
    public String getContents() {
        return this.string;
    }

    @Override
    public TextComponent plainCopy() {
        return new StringTextComponent(this.string);
    }

    @Override
    public IReorderingProcessor getVisualOrderText() {
        LanguageMap language = LanguageMap.getInstance();
        if (this.previousLanguage != language) {
            this.orderedText = TweetTextUtil.reorderIgnoreStyleChar(this, language.isDefaultRightToLeft());
            this.previousLanguage = language;
        }

        return this.orderedText;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof TweetText)) {
            return false;
        } else {
            return this.string.equals(((TweetText) object).getContents()) && super.equals(object);
        }
    }

    @Override
    public String toString() {
        return "TweetTextComponent{text='" + this.string + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
    }
}
