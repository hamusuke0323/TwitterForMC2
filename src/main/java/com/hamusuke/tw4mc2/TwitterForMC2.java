package com.hamusuke.tw4mc2;

import com.google.common.collect.Sets;
import com.hamusuke.tw4mc2.gui.screen.twitter.TwitterScreen;
import com.hamusuke.tw4mc2.license.LicenseManager;
import com.hamusuke.tw4mc2.text.emoji.EmojiManager;
import com.hamusuke.tw4mc2.texture.TextureManager;
import com.hamusuke.tw4mc2.tweet.TweetSummary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.progress.StartupMessageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

@Mod(TwitterForMC2.MOD_ID)
public final class TwitterForMC2 {
    public static final String MOD_ID = "tw4mc2";
    private static TwitterForMC2 INSTANCE;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Minecraft mc = Minecraft.getInstance();
    private final TextureManager textureManager = new TextureManager();
    private final EmojiManager emojiManager = new EmojiManager();
    private final Path configFile;
    private final TwitterAuth auth;
    public final Twitter mcTwitter = new TwitterFactory().getInstance();
    private boolean loggedInTwitter;
    public final TwitterScreen twitterScreen;
    public final TreeSet<Status> tweets = Sets.newTreeSet(Collections.reverseOrder());
    public final TreeSet<TweetSummary> tweetSummaries = Sets.newTreeSet(Collections.reverseOrder());
    public final KeyBinding openTwitter = new KeyBinding("key.twitter4mc.opentw", GLFW.GLFW_KEY_B, "key.categories.gameplay");

    public synchronized void exportTimeline() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.configFile.resolve("timeline").toFile()))) {
            oos.writeObject(this.tweets);
            oos.flush();
        } catch (IOException e) {
            LOGGER.error("Error occurred while exporting timeline", e);
            throw e;
        }
    }

    private synchronized void importTimeline() {
        File file = this.configFile.resolve("timeline").toFile();
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object object = ois.readObject();
            if (object instanceof List) {
                List<Status> statuses = (List<Status>) object;
                this.tweets.clear();
                this.tweets.addAll(statuses);
            } else if (object instanceof TreeSet) {
                TreeSet<Status> statuses = (TreeSet<Status>) object;
                this.tweets.clear();
                this.tweets.addAll(statuses);
            }
        } catch (Throwable e) {
            LOGGER.error("Error occurred while importing timeline", e);
        }
    }

    public TwitterForMC2() {
        INSTANCE = this;
        this.twitterScreen = new TwitterScreen();
        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(this.emojiManager);
        this.configFile = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);
        if (!this.configFile.toFile().exists()) {
            if (this.configFile.toFile().mkdir()) {
                LOGGER.info("made config directory: {}", MOD_ID);
            }
        }
        this.auth = new TwitterAuth(this.configFile.resolve("token").toFile());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void addMessage(String msg) {
        StartupMessageManager.addModMessage("Twitter for MC 2: " + msg);
    }

    @SubscribeEvent
    public void onSetup(final FMLClientSetupEvent event) {
        addMessage("Hello from TwitterForMC2#onSetup!");

        addMessage("Registering keybinding");
        ClientRegistry.registerKeyBinding(this.openTwitter);
        LicenseManager.registerLicense(new ResourceLocation(TwitterForMC2.MOD_ID, "license/gplv3.txt"), 400, "tw.license.thismod");
        LicenseManager.registerLicense(new ResourceLocation(TwitterForMC2.MOD_ID, "license/twitter4j_license.txt"), 270, "tw.license.twitter4j");
        LicenseManager.registerLicense(new ResourceLocation(TwitterForMC2.MOD_ID, "license/twemoji_graphics_license.txt"), 320, "tw.license.twemoji.graphics");

        this.auth.read();
        this.auth.getToken().ifPresent(token -> {
            addMessage("Trying to log in twitter");
            AccessToken var1 = new AccessToken(token.getAccessToken(), token.getAccessTokenSecret());
            if (token.autoLogin()) {
                try {
                    this.mcTwitter.setOAuthConsumer(token.getConsumer(), token.getConsumerSecret());
                    this.mcTwitter.setOAuthAccessToken(var1);
                    this.mcTwitter.getId();
                    this.loggedInTwitter = true;
                    LOGGER.info("Successfully logged in twitter");
                    addMessage("Successfully logged in twitter");
                } catch (Throwable e) {
                    this.loggedInTwitter = false;
                    LOGGER.error("Error occurred while logging in twitter", e);
                }
            }
        });

        if (this.loggedInTwitter) {
            this.importTimeline();
            if (this.tweets.size() > 0) {
                addMessage("Trying to import timeline");
                for (Status s : this.tweets) {
                    TweetSummary tweetSummary = new TweetSummary(s);
                    this.tweetSummaries.add(tweetSummary);
                    addMessage(String.format("Added an tweet(id: %s)", tweetSummary.getId()));
                }
                addMessage("Finished importing timeline");
            }
        }
        System.setProperty("java.awt.headless", "false");

        addMessage("Completed loading the mod correctly");
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (this.openTwitter.consumeClick()) {
                this.twitterScreen.setParentScreen(null);
                mc.setScreen(this.twitterScreen);
            }
        }
    }

    public boolean isLoggedInTwitter() {
        return this.loggedInTwitter;
    }

    public void setLoggedInTwitter(boolean bl) {
        this.loggedInTwitter = bl;
    }

    public TwitterAuth getAuth() {
        return this.auth;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public EmojiManager getEmojiManager() {
        return this.emojiManager;
    }

    public static TwitterForMC2 getInstance() {
        return INSTANCE;
    }
}
