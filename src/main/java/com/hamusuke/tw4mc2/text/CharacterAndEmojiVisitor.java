package com.hamusuke.tw4mc2.text;

import com.hamusuke.tw4mc2.text.emoji.Emoji;
import net.minecraft.util.ICharacterConsumer;

public interface CharacterAndEmojiVisitor extends ICharacterConsumer {
    default boolean acceptEmoji(Emoji emoji) {
        return true;
    }
}
