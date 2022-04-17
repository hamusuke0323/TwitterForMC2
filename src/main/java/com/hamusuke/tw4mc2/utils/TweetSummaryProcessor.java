package com.hamusuke.tw4mc2.utils;

import com.hamusuke.tw4mc2.tweet.TweetSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.Status;

import java.util.List;
import java.util.function.Consumer;

public class TweetSummaryProcessor {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TwitterThread twitterThread;

    public TweetSummaryProcessor(List<Status> statuses, Consumer<TweetSummary> callback, Runnable onProcessFinished) {
        this.twitterThread = new TwitterThread(() -> {
            statuses.forEach(status -> callback.accept(new TweetSummary(status)));
            onProcessFinished.run();
        });
    }

    public void process() {
        if (this.twitterThread.getState() == Thread.State.NEW) {
            this.twitterThread.start();
            return;
        }

        LOGGER.warn("the process is already started");
    }
}
