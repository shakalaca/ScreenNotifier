package com.corner23.android.i9000.notifier;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MissEventNotifierService extends Service {
	private static final String TAG = "MissEventNotifierService";
	
	private static boolean MissEventNotifyIsOn = false;
	private static boolean bEnabled = false;
	
	private static TelephonyManager tm;
	private static PowerManager pm;
	private static WakeLock wl;
	private static ScreenLED mScreenLED = null;

	private final Runnable stopNotificationRunnable = new Runnable() {
		public void run() {
			if (mScreenLED != null) {
				mScreenLED.stopNotification();
			}
		}
	};
	
	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		boolean offhook = false;
		boolean ringing = false;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				if (ringing && !offhook) {
					MissEventNotifyIsOn = true;
					if (!pm.isScreenOn()) {
						startNotification();
					}
				}
				ringing = false;
				offhook = false;
				break;

			case TelephonyManager.CALL_STATE_RINGING:
				ringing = true;
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				offhook = true;
				break;
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {	
		Log.d(TAG, "onCreate");
		super.onCreate();

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		enableListeners();			
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		
		disableListeners();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		SharedPreferences prefs = this.getSharedPreferences(Settings.SHARED_PREFS_NAME, 0);
		if (prefs.getBoolean(Settings.PREF_ENABLE, true)) {
			Log.d(TAG, "Service enabled");
		} else {
			Log.d(TAG, "Service disabled");
			this.stopSelf();
			return;
		}
		
		if (intent != null) {
			if (intent.getBooleanExtra("reset", false)) {
				Log.d(TAG, "reset");
				MissEventNotifyIsOn = false;
				if (mScreenLED != null) {
					mScreenLED.runOnUiThread(stopNotificationRunnable);
				}
			} else if (intent.getBooleanExtra("shownotify", false)) {
				Log.d(TAG, "show notify");
				MissEventNotifyIsOn = true;
				if (!pm.isScreenOn()) {
					startNotification();
				}
			} else if (intent.getBooleanExtra("lock", false)) {
				Log.d(TAG, "lock");
				acquireWakeLock();
			} else if (intent.getBooleanExtra("unlock", false)) {
				Log.d(TAG, "unlock");
				cancelWakeLock();
			}
		}
	}

	private void enableListeners() {		
		if (!bEnabled) {
			tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	
			final IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			registerReceiver(screenStateBR, filter);
			bEnabled = true;
		}
	}
	
	private void disableListeners() {
		if (bEnabled) {
			tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
			unregisterReceiver(screenStateBR);
			bEnabled = false;
		}
	}
	
	public static void registerActivity(ScreenLED activity) {
		mScreenLED = activity;
	}
	
	public static void unregisterActivity() {
		mScreenLED = null;
	}
	
	private void startNotification() {
		final Intent intent = new Intent(MissEventNotifierService.this, ScreenLED.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED |
				Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
				Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		
		startActivity(intent);
	}
	
	private final BroadcastReceiver screenStateBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive");
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (MissEventNotifyIsOn) {
					startNotification();
				}
			}
		}
	};

	private void acquireWakeLock() {
		wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "MissEventNotifier");
		wl.acquire();
	}
	
	private void cancelWakeLock() {
		if (wl != null) {
			wl.release();
			wl = null;
		}
	}
}
