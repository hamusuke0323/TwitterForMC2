package com.hamusuke.tw4mc2.tweet;

import com.hamusuke.tw4mc2.utils.ImageDataDeliverer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.MediaEntity;

import java.io.InputStream;

public class TwitterPhotoMedia {
	private final MediaEntity entity;
	private final String url;
	private final ImageDataDeliverer data;
	private static final Logger LOGGER = LogManager.getLogger();

	public TwitterPhotoMedia(MediaEntity entity) {
		this.entity = entity;
		this.url = this.entity.getMediaURLHttps();
		this.data = new ImageDataDeliverer(this.url).prepareAsync(e -> LOGGER.warn("Failed to load photo data, return null.", e), ignored -> {
		});
	}

	public MediaEntity getMediaEntity() {
		return this.entity;
	}

	public String getMediaURL() {
		return this.url;
	}

	public ImageDataDeliverer getImageDataDeliverer() {
		return this.data;
	}

	public InputStream getData() {
		return this.data.deliver();
	}

	public int getWidth() {
		return this.data.getWidth();
	}

	public int getHeight() {
		return this.data.getHeight();
	}

	public boolean readyToRender() {
		return this.data.readyToRender();
	}
}
