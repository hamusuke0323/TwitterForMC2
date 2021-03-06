package com.hamusuke.tw4mc2.tweet;

import com.google.common.collect.Sets;
import com.hamusuke.tw4mc2.TwitterForMC2;
import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import com.hamusuke.tw4mc2.utils.TweetSummaryProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Status;
import twitter4j.User;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserSummary {
    private static final Logger LOGGER = LogManager.getLogger();
    private final User user;
    private final long id;
    private final String name;
    private final String screenName;
    private final String description;
    private final int statusesCount;
    private final ImageDataDeliverer icon;
    private final ImageDataDeliverer header;
    private final TreeSet<TweetSummary> userTimeline = Sets.newTreeSet(Collections.reverseOrder());
    private final AtomicBoolean isGettingUserTimeline = new AtomicBoolean();
    private final AtomicBoolean isAlreadyGotUserTimeline = new AtomicBoolean();
    private final boolean isProtected;
    private final boolean isVerified;

    public UserSummary(User user) {
        this.user = user;
        this.id = this.user.getId();
        this.name = this.user.getName();
        this.screenName = this.user.getScreenName();
        this.description = this.user.getDescription();
        this.statusesCount = this.user.getStatusesCount();
        this.icon = new ImageDataDeliverer(this.user.get400x400ProfileImageURLHttps()).prepareAsync();
        this.header = new ImageDataDeliverer(this.user.getProfileBanner1500x500URL()).prepareAsync();
        this.isProtected = this.user.isProtected();
        this.isVerified = this.user.isVerified();
    }

    public void startGettingUserTimeline(Runnable onAdded) {
        if (TwitterForMC2.getInstance().isLoggedInTwitter() && !this.isGettingUserTimeline()) {
            this.isGettingUserTimeline.set(true);
            try {
                List<Status> statuses = TwitterForMC2.getInstance().mcTwitter.getUserTimeline(this.user.getId());
                Collections.reverse(statuses);

                new TweetSummaryProcessor(statuses, tweetSummary -> {
                    if (this.userTimeline.add(tweetSummary)) {
                        onAdded.run();
                    }
                }, () -> {
                    this.isGettingUserTimeline.set(false);
                    this.isAlreadyGotUserTimeline.set(true);
                }).process();
            } catch (Throwable e) {
                LOGGER.error("Error occurred while getting user timeline", e);
                this.isGettingUserTimeline.set(false);
                this.isAlreadyGotUserTimeline.set(false);
                this.userTimeline.clear();
            }
        }
    }

    public boolean isGettingUserTimeline() {
        return this.isGettingUserTimeline.get();
    }

    public boolean isAlreadyGotUserTimeline() {
        return this.isAlreadyGotUserTimeline.get();
    }

    public User getUser() {
        return this.user;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getScreenName() {
        return "@" + this.screenName;
    }

    public String getDescription() {
        return this.description;
    }

    public int getStatusesCount() {
        return this.statusesCount;
    }

    public ImageDataDeliverer getIcon() {
        return this.icon;
    }

    public ImageDataDeliverer getHeader() {
        return this.header;
    }

    public TreeSet<TweetSummary> getUserTimeline() {
        return this.userTimeline;
    }

    public boolean isProtected() {
        return this.isProtected;
    }

    public boolean isVerified() {
        return this.isVerified;
    }
}
