package com.hamusuke.tw4mc2.utils;

public final class TwitterThread extends Thread {
	public TwitterThread(Runnable run) {
		super(run, "Twitter Thread");
	}
}
