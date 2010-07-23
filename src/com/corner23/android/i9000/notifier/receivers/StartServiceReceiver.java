package com.corner23.android.i9000.notifier.receivers;

import com.corner23.android.i9000.notifier.MissEventNotifierService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction() != null) {
			Intent serviceIntent = new Intent(context, MissEventNotifierService.class);
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				context.startService(serviceIntent);
			} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				serviceIntent.putExtra("reset", true);
				context.startService(serviceIntent);
			}
		}
	}
}
