package com.corner23.android.i9000.notifier;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class ScreenLED extends Activity {
	private final static String TAG = "ScreenLED";

	private int mColor;
	private int mAppearance = Settings.PREF_APPEARANCE_DOT;
	private int mInterval = 7;
	private DotView mDotView;
	private final Handler mHandler = new Handler();

	private final Runnable showDot = new Runnable() {
		public void run() {
			mDotView.post(new Runnable() {
				public void run() {
					mDotView.showDot(mColor);
					mDotView.invalidate();
				}
			});
			mHandler.removeCallbacks(showDot);
			mHandler.postDelayed(showDot, mInterval * 1000);
		}
	};
	
	private boolean bIsLEDOn = false;
	private final Runnable blinkLED = new Runnable() {
		public void run() {
			displayLed(!bIsLEDOn);
			bIsLEDOn = !bIsLEDOn;
			
			mHandler.removeCallbacks(blinkLED);
			mHandler.postDelayed(blinkLED, bIsLEDOn ? 200 : mInterval * 1000);
		}
	};
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
	
	@Override
    protected void onCreate(Bundle icicle) {
		Log.d(TAG, "onCreate");
        super.onCreate(icicle);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(
        	WindowManager.LayoutParams.FLAG_FULLSCREEN | 
        	WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | 
        	WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE /*| 
        	WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | 
        	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON */);

        mDotView = new DotView(this);		
        this.setContentView(mDotView);        
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
		
        MissEventNotifierService.registerActivity(this);

		Intent serviceIntent = new Intent(this, MissEventNotifierService.class);
		serviceIntent.putExtra("lock", true);
    	startService(serviceIntent);
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
		
		MissEventNotifierService.unregisterActivity();

		Intent serviceIntent = new Intent(this, MissEventNotifierService.class);
		serviceIntent.putExtra("unlock", true);
    	startService(serviceIntent);
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		
		stopNotification();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
        
		SharedPreferences prefs = this.getSharedPreferences(Settings.SHARED_PREFS_NAME, 0);
		mAppearance = Integer.parseInt(prefs.getString(Settings.PREF_APPEARANCE, "0"));
		mInterval = Integer.parseInt(prefs.getString(Settings.PREF_DISPLAY_INTERVAL, "7"));
		mColor = prefs.getInt(Settings.PREF_DOT_COLOR, Color.RED);
		
		displayNotification();
    }
	
	private void setBacklight(boolean minOn) {
		try {
			File _file = new File("/sys/class/backlight/s5p_bl/brightness");
			if (_file != null) {
				FileWriter _fw = new FileWriter(_file);
				_fw.write(minOn ? "20" : "0");
				_fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}
	
	private void enableTouchScreen(boolean enable) {
		try {
			File _file = new File("/sys/devices/virtual/touch/switch/set_touchscreen");
			if (_file != null) {
				FileWriter _fw = new FileWriter(_file);
				_fw.write(enable ? "143" : "0");
				_fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}

	private void displayLed(boolean enable) {
		try {
			File _file = new File("/sys/devices/virtual/misc/melfas_touchkey/brightness");
			if (_file != null) {
				FileWriter _fw = new FileWriter(_file);
				_fw.write(enable ? "1" : "2");
				_fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void displayNotification() {
		if (mAppearance == Settings.PREF_APPEARANCE_DOT) {
			mHandler.post(showDot);
			setBacklight(true);
		} else {
			mDotView.hideDot();
			mDotView.invalidate();
			mHandler.post(blinkLED);
			setBacklight(false);
		}
        enableTouchScreen(false);
	}
	
	public void stopNotification() {
		if (mAppearance == Settings.PREF_APPEARANCE_DOT) {
			mHandler.removeCallbacks(showDot);
		} else {
			mHandler.removeCallbacks(blinkLED);
		}		
		setBacklight(true);
        enableTouchScreen(true);
	}	
}
