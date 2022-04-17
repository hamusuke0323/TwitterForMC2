package com.hamusuke.tw4mc2.gui.screen.login;

import com.hamusuke.tw4mc2.TwitterAuth;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.gui.filechooser.FileChooserOpen;
import com.hamusuke.tw4mc2.gui.screen.ErrorScreen;
import com.hamusuke.tw4mc2.gui.screen.ParentalScreen;
import com.hamusuke.tw4mc2.gui.toasts.TwitterNotificationToast;
import com.hamusuke.tw4mc2.gui.widget.MaskableTextFieldWidget;
import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URI;

public class TwitterLoginScreen extends ParentalScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private MaskableTextFieldWidget consumer;
    private MaskableTextFieldWidget consumerS;
    private MaskableTextFieldWidget access;
    private MaskableTextFieldWidget accessS;
    private CheckboxButton save;
    private CheckboxButton autoLogin;
    private Button login;
    private final FileChooserOpen tokenFileChooser = new FileChooserOpen(file -> {
        if (file != null) {
            TwitterAuth auth = TwitterForMC2.getInstance().getAuth();
            auth.setTokenFile(file);
            auth.read();
            auth.getToken().ifPresent(newToken -> {
                this.consumer.setText(newToken.getConsumer());
                this.consumerS.setText(newToken.getConsumerSecret());
                this.access.setText(newToken.getAccessToken());
                this.accessS.setText(newToken.getAccessTokenSecret());

                if (newToken.autoLogin()) {
                    if (!this.save.selected()) {
                        this.save.onPress();
                    }
                    if (!this.autoLogin.selected()) {
                        this.autoLogin.onPress();
                    }
                } else {
                    if (this.autoLogin.selected()) {
                        this.autoLogin.onPress();
                    }
                }
            });
        }
    }, FMLPaths.GAMEDIR.get().toFile());

    public TwitterLoginScreen(Screen parent) {
        super(new TranslationTextComponent("twitter.login"), parent);
    }

    @Override
    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.width / 4;
        int k = this.width / 3;

        this.consumer = new MaskableTextFieldWidget(this.font, j, 20, i, 20, this.consumer, new TranslationTextComponent("tw.consumer.key"), '●', 1000);
        this.addButton(this.consumer);

        this.consumerS = new MaskableTextFieldWidget(this.font, j, 60, i, 20, this.consumerS, new TranslationTextComponent("tw.consumer.secret"), '●', 1000);
        this.addButton(this.consumerS);

        this.access = new MaskableTextFieldWidget(this.font, j, 100, i, 20, this.access, new TranslationTextComponent("tw.access.token"), '●', 1000);
        this.addButton(this.access);

        this.accessS = new MaskableTextFieldWidget(this.font, j, 140, i, 20, this.accessS, new TranslationTextComponent("tw.access.token.secret"), '●', 1000);
        this.addButton(this.accessS);

        this.save = this.addButton(new CheckboxButton(j, 170, 20, 20, new TranslationTextComponent("tw.save.keys"), this.save != null ? this.save.selected() : TwitterForMC2.getInstance().getAuth().readToken()));

        this.autoLogin = this.addButton(new CheckboxButton(j, 200, 20, 20, new TranslationTextComponent("tw.auto.login"), this.autoLogin != null ? this.autoLogin.selected() : TwitterForMC2.getInstance().getAuth().readToken() && TwitterForMC2.getInstance().getAuth().isAutoLogin()));

        this.login = this.login != null ? this.login : new Button(0, this.height - 20, this.width / 2, 20, this.title, b -> {
            this.active(false);
            if (this.access.getText().isEmpty() || this.accessS.getText().isEmpty()) {
                this.pinLogin(() -> {
                    this.minecraft.setScreen(TwitterForMC2.getInstance().twitterScreen);
                    this.addToast();
                });
            } else {
                this.simpleLogin(() -> {
                    this.minecraft.setScreen(TwitterForMC2.getInstance().twitterScreen);
                    this.addToast();
                });
            }
            this.active(true);
        });
        this.login.y = this.height - 20;
        this.login.setWidth(k);
        this.login.setMessage(this.title);
        this.addButton(this.login);

        this.addButton(new Button(k, this.height - 20, k, 20, new TranslationTextComponent("tw.token.file.choose"), b -> this.tokenFileChooser.choose()));

        this.addButton(new Button(k * 2, this.height - 20, k, 20, DialogTexts.GUI_BACK, b -> this.onClose()));
    }

    private void addToast() {
        try {
            Twitter twitter = TwitterForMC2.getInstance().mcTwitter;
            new ImageDataDeliverer(twitter.showUser(twitter.getId()).get400x400ProfileImageURLHttps()).prepareAsync(e -> {
                this.minecraft.getToasts().addToast(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslationTextComponent("tw.login.successful"), null));
            }, imageDataDeliverer -> this.minecraft.getToasts().addToast(new TwitterNotificationToast(imageDataDeliverer.deliver(), new TranslationTextComponent("tw.login.successful"))));
        } catch (Exception e) {
            this.minecraft.getToasts().addToast(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslationTextComponent("tw.login.successful"), null));
        }
    }

    private void active(boolean flag) {
        this.consumer.setEditable(flag);
        this.consumerS.setEditable(flag);
        this.access.setEditable(flag);
        this.accessS.setEditable(flag);
        this.save.active = flag;
        this.autoLogin.active = flag;
        this.login.active = flag;
    }

    @Override
    public void tick() {
        this.login.active = !(this.consumer.active && this.consumer.getText().isEmpty()) && !(this.consumerS.active && this.consumerS.getText().isEmpty());
        this.autoLogin.active = this.save.active && this.save.selected();
        this.autoLogin.visible = this.save.selected();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.parent.render(matrices, -1, -1, delta);
        this.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
        super.render(matrices, mouseX, mouseY, delta);
        if (this.save.isMouseOver(mouseX, mouseY)) {
            this.renderTooltip(matrices, this.font.split(new TranslationTextComponent("tw.save.keys.desc"), this.width / 2), mouseX, mouseY);
        }
    }

    private synchronized void simpleLogin(Runnable callback) {
        try {
            Twitter twitter = TwitterForMC2.getInstance().mcTwitter;
            twitter.setOAuthConsumer(this.consumer.getText(), this.consumerS.getText());
            AccessToken token = new AccessToken(this.access.getText(), this.accessS.getText());
            twitter.setOAuthAccessToken(token);
            twitter.getId();
            TwitterForMC2.getInstance().getAuth().store(this.consumer.getText(), this.consumerS.getText(), token, this.autoLogin.selected());
            TwitterForMC2.getInstance().setLoggedInTwitter(true);
            callback.run();
        } catch (Throwable e) {
            TwitterForMC2.getInstance().setLoggedInTwitter(false);
            LOGGER.error("Error occurred while logging in twitter", e);
            this.minecraft.setScreen(new ErrorScreen(new TranslationTextComponent("tw.login.failed"), this, e.getLocalizedMessage()));
        }
    }

    private synchronized void pinLogin(Runnable callback) {
        try {
            Twitter twitter = TwitterForMC2.getInstance().mcTwitter;
            twitter.setOAuthConsumer(this.consumer.getText(), this.consumerS.getText());
            RequestToken requestToken = twitter.getOAuthRequestToken();
            Util.getPlatform().openUri(new URI(requestToken.getAuthorizationURL()));
            this.minecraft.setScreen(new EnterPinScreen(pin -> {
                try {
                    AccessToken token = twitter.getOAuthAccessToken(requestToken, pin);
                    twitter.setOAuthAccessToken(token);
                    twitter.getId();
                    this.access.setText(token.getToken());
                    this.accessS.setText(token.getTokenSecret());
                    TwitterForMC2.getInstance().getAuth().store(this.consumer.getText(), this.consumerS.getText(), token, this.autoLogin.selected());
                    TwitterForMC2.getInstance().setLoggedInTwitter(true);
                    callback.run();
                } catch (Throwable e) {
                    TwitterForMC2.getInstance().setLoggedInTwitter(false);
                    LOGGER.error("Error occurred while logging in twitter", e);
                    this.minecraft.setScreen(new ErrorScreen(new TranslationTextComponent("tw.login.failed"), this, e.getLocalizedMessage()));
                }
            }));
        } catch (Throwable e) {
            LOGGER.error("Error occurred while logging in twitter", e);
            this.minecraft.setScreen(new ErrorScreen(new TranslationTextComponent("tw.login.failed"), this, e.getLocalizedMessage()));
        }
    }
}
