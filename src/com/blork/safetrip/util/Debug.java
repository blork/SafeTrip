package com.blork.safetrip.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public final class Debug {

	private static final String TAG = "SafeTrip";
	
	public static void log(Object msg) {
		Log.d(TAG, msg.toString());
	}
	
	public static void log(Object msg, Throwable tr) {
		Log.e(TAG, msg.toString(), tr);
	}
	
	public static void toast(Object msg, Context ctx) {
		Toast.makeText(ctx, msg.toString(), Toast.LENGTH_LONG).show();
	}
	
}
