package com.hamusuke.tw4mc2.utils;

import com.hamusuke.tw4mc2.tweet.TweetSummary;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import twitter4j.*;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class TwitterUtil {
	private static final Logger LOGGER = LogManager.getLogger();

	public static StatusUpdate createReplyTweet(String tweet, Status replyTo) {
		return new StatusUpdate("@" + replyTo.getUser().getScreenName() + " " + tweet).inReplyToStatusId(replyTo.getId());
	}

	public static StatusUpdate createQuoteTweet(String comment, TweetSummary target) {
		return new StatusUpdate(comment).attachmentUrl(target.getTweetURL());
	}

	public static void saveToken(NewToken newToken, File tokenFile) throws Exception {
		TokenUtil.func_a_(newToken, tokenFile);
	}

	public static NewToken readToken(File tokenFile) throws Exception {
		return TokenUtil.func_b_(tokenFile);
	}

	public static JSONObject getReplyCount(Twitter twitter, long tweetId) throws TwitterException {
		HttpResponse httpResponse = HttpClientFactory.getInstance().get("https://api.twitter.com/2/tweets", new HttpParameter[]{new HttpParameter("ids", tweetId), new HttpParameter("tweet.fields", "public_metrics,author_id,conversation_id,created_at,in_reply_to_user_id,referenced_tweets"), new HttpParameter("expansions", "author_id,in_reply_to_user_id,referenced_tweets.id"), new HttpParameter("user.fields", "name,username")}, twitter.getAuthorization(), null);

		if (httpResponse != null) {
			return httpResponse.asJSONObject();
		}

		return new JSONObject();
	}

	@Nullable
	public static ReplyObject getReplies(Twitter twitter, long tweetId) throws TwitterException {
		return getReplies(twitter, tweetId, 10);
	}

	@Nullable
	public static ReplyObject getReplies(Twitter twitter, long tweetId, int maxResult) throws TwitterException {
		HttpResponse httpResponse = HttpClientFactory.getInstance().get("https://api.twitter.com/2/tweets/search/recent", new HttpParameter[]{new HttpParameter("expansions", "attachments.poll_ids,attachments.media_keys,author_id,entities.mentions.username,geo.place_id,in_reply_to_user_id,referenced_tweets.id,referenced_tweets.id.author_id"), new HttpParameter("query", "conversation_id:" + tweetId), new HttpParameter("tweet.fields", "author_id,conversation_id,created_at,entities,id,in_reply_to_user_id,public_metrics,referenced_tweets,reply_settings,text"), new HttpParameter("user.fields", "id,name,pinned_tweet_id,profile_image_url,protected,username,verified"), new HttpParameter("max_results", maxResult)}, twitter.getAuthorization(), null);
		return httpResponse != null ? new ReplyObject(httpResponse.asJSONObject()) : null;
	}

	public static String getChunkedNumber(int number) {
		return getChunkedNumber("" + number);
	}

	//123456 -> 123.4K
	public static String getChunkedNumber(String number) {
		try {
			Integer.parseInt(number);
			StringBuilder builder = new StringBuilder();
			int index = 0;
			int counter = 0;
			for (int i = number.length() - 1; i >= 0; i--) {
				builder.append(number.charAt(i));
				index++;
				if (index % 3 == 0 && index != number.length()) {
					builder.append('.');
					counter++;
					builder.delete(0, counter == 1 ? 2 : 4);
				}
			}
			builder.reverse();
			if (builder.length() >= 2 && builder.charAt(builder.length() - 2) == '.' && builder.charAt(builder.length() - 1) == '0') {
				builder.delete(builder.length() - 2, builder.length());
			}

			if (counter != 0) {
				builder.append(TwitterUtil.Unit.get(counter));
			}
			return builder.toString();
		} catch (RuntimeException e) {
			LOGGER.warn("Input: {} is not a number", number);
			return number;
		}
	}

	public static String getDifferenceTime(Calendar created, Calendar now) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(now.getTimeInMillis() - created.getTimeInMillis()));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY) - 9;
		int minute = calendar.get(Calendar.MINUTE);
		if (year == 1970 && month == 1 && day == 1 && hour == 0 && minute == 0) {
			return I18n.get("tw.seconds", calendar.get(Calendar.SECOND));
		} else if (year == 1970 && month == 1 && day == 1 && hour == 0) {
			return I18n.get("tw.minutes", minute);
		} else if (year == 1970 && month == 1 && day == 1) {
			return I18n.get("tw.hours", hour);
		} else if (year == 1970) {
			return I18n.get("tw.month.day." + (created.get(Calendar.MONTH) + 1), created.get(Calendar.DAY_OF_MONTH));
		} else {
			return I18n.get("tw.year.month.day." + (created.get(Calendar.MONTH) + 1), created.get(Calendar.YEAR), created.get(Calendar.DAY_OF_MONTH));
		}
	}

	public static String getTime(Calendar c) {
		int y = c.get(Calendar.YEAR);
		int mon = c.get(Calendar.MONTH);
		int d = c.get(Calendar.DAY_OF_MONTH);
		int h = c.get(Calendar.HOUR_OF_DAY);
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);
		return y + "/" + (mon < 10 ? "0" : "") + mon + "/" + (d < 10 ? "0" : "") + d + " " + (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
	}

	@Nullable
	public static String getHiBitrateVideoURL(MediaEntity media) {
		int i = 0, b = 0;
		if (media.getType().equals("video")) {
			MediaEntity.Variant[] variants = media.getVideoVariants();
			for (int j = 0; j < variants.length; j++) {
				MediaEntity.Variant v = variants[j];
				String url = v.getUrl();
				if (!url.contains("m3u8")) {
					int k = v.getBitrate();
					if (b < k) {
						b = k;
						i = j;
					}
				}
			}
			return variants[i].getUrl();
		}
		return null;
	}

	public static Dimension wrapImageSizeToMax(Dimension imageSize, Dimension boundary) {
		double ratio = Math.max(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
		return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
	}

	public static Dimension wrapImageSizeToMin(Dimension imageSize, Dimension boundary) {
		double ratio = Math.min(boundary.getWidth() / imageSize.getWidth(), boundary.getHeight() / imageSize.getHeight());
		return new Dimension((int) (imageSize.width * ratio), (int) (imageSize.height * ratio));
	}

	enum Unit {
		KILO('K', 1),
		MEGA('M', 2),
		GIGA('G', 3);

		private final char name;
		private final int split;

		Unit(char name, int split) {
			this.name = name;
			this.split = split;
		}

		static char get(int split) {
			for (TwitterUtil.Unit u : TwitterUtil.Unit.values()) {
				if (u.split == split) {
					return u.name;
				}
			}

			return Character.MIN_VALUE;
		}
	}

	private static final class TokenUtil {
		private static final transient String field_a_ = "aGFtdXN1a2UwMzIzKEdpdEh1YjpoYW11c3VrZTIzMDMpIGlzIGEgSmF2YSBQcm9ncmFtbWVyLg==";
		private static final transient String field_b_ = "bG92ZXMgSmF2YQ==";

		private static void func_a_(NewToken p_func_a__0_, File p_func_a__1_) throws Exception {
			FileOutputStream field_a_ = new FileOutputStream(p_func_a__1_);
			final Cipher field_d_ = Cipher.getInstance("AES/CBC/PKCS5Padding");
			field_d_.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(field_b_.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(Arrays.copyOfRange(TokenUtil.field_a_.getBytes(StandardCharsets.UTF_8), 0, 16)));
			try (CipherOutputStream field_c_ = new CipherOutputStream(field_a_, field_d_)) {
				field_c_.write(SerializationUtils.serialize(p_func_a__0_));
				field_c_.flush();
			}
		}

		private static NewToken func_b_(File p_func_b__0_) throws Exception {
			FileInputStream field_a_ = new FileInputStream(p_func_b__0_);
			final Cipher field_b_ = Cipher.getInstance("AES/CBC/PKCS5Padding");
			field_b_.init(Cipher.DECRYPT_MODE, new SecretKeySpec(TokenUtil.field_b_.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(Arrays.copyOfRange(TokenUtil.field_a_.getBytes(StandardCharsets.UTF_8), 0, 16)));
			CipherInputStream field_c_ = new CipherInputStream(field_a_, field_b_);
			try (ObjectInputStream field_d_ = new ObjectInputStream(field_c_)) {
				return (NewToken) field_d_.readObject();
			}
		}
	}
}
