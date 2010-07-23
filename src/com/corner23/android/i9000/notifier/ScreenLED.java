package com.corner23.android.i9000.notifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class ScreenLED extends Activity {
	private final static String TAG = "ScreenLED";
 
	DotView mDotView;

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
        	WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | 
        	WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | 
        	WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
		
		MissEventNotifierService.unregisterActivity();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
        
		displayNotification();
    }
	
	public void displayNotification() {
		mDotView = new DotView(this);		
        this.setContentView(mDotView);
	}
}
