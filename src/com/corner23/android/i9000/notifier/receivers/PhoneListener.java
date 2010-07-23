package com.corner23.android.i9000.notifier.receivers;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneListener extends PhoneStateListener {
	boolean offhook = false;
	boolean ringing = false;

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			if (ringing && !offhook) {
				new MissEventHandler();
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
}
