package com.hamusuke.tw4mc2.text.emoji;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public final class EmojiManager extends JsonReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private final Map<String, Emoji> emojiMap = Maps.newHashMap();

    public EmojiManager() {
        super(GSON, "textures/twitter");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
        this.emojiMap.clear();
        long time = System.currentTimeMillis();
        p_212853_1_.values().forEach(jsonElement -> this.load(jsonElement.getAsJsonArray()));
        LOGGER.info("Total loaded emoji(s): {}, Total load time: {}ms", this.emojiMap.size(), System.currentTimeMillis() - time);
    }

    private void load(JsonArray jsonArray) {
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String hex = jsonObject.get("hex").getAsString();
            Emoji emoji = new Emoji(hex, new ResourceLocation(jsonObject.get("image").getAsString()));
            if (this.emojiMap.put(hex, emoji) == null) {
                LOGGER.debug("Registered emoji: {}:{}", emoji.getId().getNamespace(), emoji.getHex());
            }
        }
    }

    public boolean isEmoji(String hex) {
        return this.emojiMap.containsKey(hex);
    }

    public Emoji getEmoji(String hex) {
        Emoji e = this.emojiMap.get(hex);
        return e == null ? new Emoji(hex, MissingTextureSprite.getLocation()) : e;
    }

    public ImmutableMap<String, Emoji> getAllEmojis() {
        return ImmutableMap.copyOf(this.emojiMap);
    }
}
