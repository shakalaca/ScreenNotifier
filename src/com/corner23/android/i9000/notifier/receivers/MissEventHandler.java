package com.corner23.android.i9000.notifier.receivers;

import com.corner23.android.i9000.notifier.MissEventNotifierService;

public class MissEventHandler implements Runnable {

	public MissEventHandler() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while (true) {
//			if ((MissEventType == MissEventNotifierService.MISS_CALL_NOTIFY && MissEventNotifierService.MissCallNotifyIsOn) ||
//					(MissEventType == MissEventNotifierService.MISS_SMS_NOTIFY && MissEventNotifierService.MissSMSNotifyIsOn)) {
//				return;
//			}
			
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
			MissEventNotifierService.NotifierService.showNotification();
		}
	}
}
