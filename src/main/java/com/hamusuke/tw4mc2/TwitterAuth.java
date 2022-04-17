package com.hamusuke.tw4mc2;

import com.hamusuke.tw4mc2.utils.NewToken;
import com.hamusuke.tw4mc2.utils.TwitterUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.auth.AccessToken;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

public final class TwitterAuth {
    private static final Logger LOGGER = LogManager.getLogger();
    private File tokenFile;
    @Nullable
    private NewToken token;

    TwitterAuth(File token) {
        this.tokenFile = token;
    }

    public synchronized void read() {
        if (this.tokenFile.exists()) {
            try {
                this.token = TwitterUtil.readToken(this.tokenFile);
            } catch (Exception e) {
                LOGGER.warn("Error occurred while reading tokens", e);
            }
        }
    }

    public synchronized void store(String consumer, String consumerS, AccessToken token, boolean autoLogin) throws Throwable {
        Objects.requireNonNull(consumer, "consumer cannot be null");
        Objects.requireNonNull(consumerS, "consumer secret cannot be null");
        Objects.requireNonNull(token, "access token cannot be null");
        TwitterUtil.saveToken(new NewToken(consumer, consumerS, token, autoLogin), tokenFile);
    }

    public Optional<NewToken> getToken() {
        return Optional.ofNullable(this.token);
    }

    public boolean readToken() {
        return this.token != null;
    }

    public boolean isAutoLogin() {
        return this.readToken() && this.token.autoLogin();
    }

    public void setTokenFile(File tokenFile) {
        this.tokenFile = tokenFile;
    }
}
