package com.hamusuke.tw4mc2.mixin;

import com.hamusuke.tw4mc2.invoker.TextHandlerInvoker;
import com.hamusuke.tw4mc2.text.TweetTextUtil;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.CharacterManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CharacterManager.class)
public class TextHandlerMixin implements TextHandlerInvoker {
    @Shadow
    @Final
    private CharacterManager.ICharWidthProvider widthProvider;

    @Override
    public float getWidthWithEmoji(IReorderingProcessor text) {
        return TweetTextUtil.getWidthWithEmoji(text, this.widthProvider);
    }
}
