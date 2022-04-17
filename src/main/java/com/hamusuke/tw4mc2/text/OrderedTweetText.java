package com.hamusuke.tw4mc2.text;

import com.hamusuke.tw4mc2.font.TweetTextVisitFactory;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextProcessing;

public interface OrderedTweetText extends IReorderingProcessor {
    static IReorderingProcessor styledForwardsVisitedString(String string, Style style) {
        return string.isEmpty() ? EMPTY : (visitor) -> {
            if (visitor instanceof CharacterAndEmojiVisitor) {
                return TweetTextVisitFactory.visitForwardsCharacterOrEmoji(string, style, (CharacterAndEmojiVisitor) visitor);
            } else {
                return TextProcessing.iterate(string, style, visitor);
            }
        };
    }

    static IReorderingProcessor styledBackwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
        return string.isEmpty() ? EMPTY : (visitor) -> {
            if (visitor instanceof CharacterAndEmojiVisitor) {
                return TweetTextVisitFactory.visitBackwardsCharacterOrEmoji(string, style, (CharacterAndEmojiVisitor) visitor);
            } else {
                return TextProcessing.iterateBackwards(string, style, IReorderingProcessor.decorateOutput(visitor, codePointMapper));
            }
        };
    }
}
