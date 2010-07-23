package com.corner23.android.i9000.notifier;

import java.util.Timer;
import java.util.TimerTask;

import com.corner23.android.i9000.notifier.receivers.PhoneListener;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MissEventNotifierService extends Service {
	private static final String TAG = "MissEventNotifierService";
	
	public static MissEventNotifierService NotifierService;
	public static boolean MissEventNotifyIsOn = false;

	private final Handler handler = new Handler();
	private boolean isDone = false;

	public static PowerManager pm;
	private TelephonyManager tm;
	private WakeLock wl;
	private static ScreenLED mScreenLED = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {				
		startForeground(0, null);
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(new PhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);

		isDone = false;
		NotifierService = this;
		
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(screenStateBR, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(screenStateBR);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		if (intent != null) {
			if (intent.getBooleanExtra("reset", false)) {
				MissEventNotifyIsOn = false;
			}
		}
	}
	
	public static void registerActivity(ScreenLED activity) {
		mScreenLED = activity;
	}
	
	public static void unregisterActivity() {
		mScreenLED = null;
	}
	
	private final Runnable showNotificationRunnable = new Runnable() {
		public void run() {
			if (mScreenLED != null) {
				mScreenLED.displayNotification();
			}
		}
	};
	
	private void startScreenLED() {
		if (mScreenLED == null) {
			final Intent intent = new Intent(MissEventNotifierService.this, ScreenLED.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(
					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
					Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
					Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			
			startActivity(intent);
		} else {
			mScreenLED.runOnUiThread(showNotificationRunnable);
		}
	}
	
	private final BroadcastReceiver screenStateBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (MissEventNotifyIsOn) {
					startScreenLEDWrapper();
				}
			}
		}
	};
	
	private void acquireWakeLock() {
		wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "MissEventNotifier");
		// wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MissEventNotifier");
		wl.acquire();
	}
	
	private void cancelWakeLock() {
		if (wl != null) {
			wl.release();
			wl = null;
		}
	}
	
	private void startScreenLEDWrapper() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (!isDone) {
					acquireWakeLock();
					startScreenLED();
					
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							cancelWakeLock();
							isDone = false;
						}}, 600);
					
					isDone = true;
				}
			}			
		});
	}
	
	public void showNotification() {
		MissEventNotifyIsOn = true;
		
		if (mScreenLED != null) {
			startScreenLEDWrapper();
		}
	}
}
