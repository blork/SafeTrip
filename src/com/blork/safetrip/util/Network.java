package com.blork.safetrip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blork.safetrip.R;

public class Network {
	public static String streamToString(InputStream is) throws IOException {
		try {
			BufferedReader buf = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			StringBuilder sb = new StringBuilder();
			String s;
			while(true) {
				s = buf.readLine();
				if(s==null || s.length()==0)
					break;
				sb.append(s);
			}
			buf.close();
			is.close();
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isNetworkConnected(Context context) {
		boolean result = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			result = false;
		} else {
			if (!info.isAvailable()) {
				result = false;
			} else {
				result = true;
			}
		}
		return result;
	}

}
