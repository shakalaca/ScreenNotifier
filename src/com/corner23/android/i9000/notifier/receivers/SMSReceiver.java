package com.corner23.android.i9000.notifier.receivers;

import com.corner23.android.i9000.notifier.MissEventNotifierService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, MissEventNotifierService.class);
		serviceIntent.putExtra("shownotify", true);
		context.startService(serviceIntent);
	}
}
