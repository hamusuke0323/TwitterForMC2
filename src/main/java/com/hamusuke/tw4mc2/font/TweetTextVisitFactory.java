package com.hamusuke.tw4mc2.font;

import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.text.CharacterAndEmojiVisitor;
import com.hamusuke.tw4mc2.text.emoji.Emoji;
import com.hamusuke.tw4mc2.text.emoji.Fitzpatrick;
import net.minecraft.util.text.Style;

public class TweetTextVisitFactory {
    public static boolean visitForwardsCharacterOrEmoji(String text, Style style, CharacterAndEmojiVisitor characterAndEmojiVisitor) {
        int i = text.length();

        StringBuilder emoji = new StringBuilder();
        for (int j = 0; j < i; ++j) {
            char c = text.charAt(j);

            if (TwitterForMC2.getInstance().getEmojiManager().isEmoji(Integer.toHexString(c))) {
                characterAndEmojiVisitor.acceptEmoji(getEmoji(Integer.toHexString(c)));
            } else if (Character.isHighSurrogate(c) && j + 1 < text.length() && Character.isLowSurrogate(text.charAt(j + 1))) {
                char low = text.charAt(j + 1);
                emoji.append(Integer.toHexString(Character.toCodePoint(c, low)));
                j++;
                if (j + 1 < text.length() && (text.charAt(j + 1) == 0x200d || (j + 2 < text.length() && Fitzpatrick.isFitzpatrick(Integer.toHexString(Character.toCodePoint(text.charAt(j + 1), text.charAt(j + 2))))))) {
                    emoji.append("-");
                } else {
                    characterAndEmojiVisitor.acceptEmoji(getEmoji(emoji.toString()));
                    emoji = new StringBuilder();
                }
            } else if (c == 0x200d) {
                emoji.append(Integer.toHexString(c)).append("-");
            } else if (!emoji.toString().isEmpty()) {
                characterAndEmojiVisitor.acceptEmoji(getEmoji(emoji.substring(0, emoji.length() - 2)));
                emoji = new StringBuilder();
            } else if (!characterAndEmojiVisitor.accept(j, style, c)) {
                return false;
            }
        }

        return true;
    }

    public static boolean visitBackwardsCharacterOrEmoji(String text, Style style, CharacterAndEmojiVisitor characterAndEmojiVisitor) {
        int i = text.length();

        StringBuilder emoji = new StringBuilder();
        for (int j = i - 1; j >= 0; --j) {
            char c = text.charAt(j);

            if (TwitterForMC2.getInstance().getEmojiManager().isEmoji(Integer.toHexString(c))) {
                characterAndEmojiVisitor.acceptEmoji(getEmoji(Integer.toHexString(c)));
            } else if (Character.isLowSurrogate(c) && j - 1 >= 0 && Character.isHighSurrogate(text.charAt(j - 1))) {
                char high = text.charAt(j - 1);
                emoji.append(Integer.toHexString(Character.toCodePoint(high, c)));
                --j;
                if (j - 1 >= 0 && (text.charAt(j - 1) == 0x200d || (j - 2 >= 0 && Fitzpatrick.isFitzpatrick(Integer.toHexString(Character.toCodePoint(text.charAt(j - 1), text.charAt(j - 2))))))) {
                    emoji.append("-");
                } else {
                    characterAndEmojiVisitor.acceptEmoji(getEmoji(emoji.toString()));
                    emoji = new StringBuilder();
                }
            } else if (c == 0x200d) {
                emoji.append(Integer.toHexString(c)).append("-");
            } else if (!emoji.toString().isEmpty()) {
                characterAndEmojiVisitor.acceptEmoji(getEmoji(emoji.substring(0, emoji.length() - 2)));
                emoji = new StringBuilder();
            } else if (!characterAndEmojiVisitor.accept(j, style, c)) {
                return false;
            }
        }

        return true;
    }

    private static Emoji getEmoji(String hex) {
        return TwitterForMC2.getInstance().getEmojiManager().getEmoji(hex);
    }
}
