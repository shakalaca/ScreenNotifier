package com.corner23.android.i9000.notifier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends PreferenceActivity implements ColorPickerDialog.OnColorChangedListener  {

	public static final String SHARED_PREFS_NAME = "men_settings";
	public static final String PREF_APPEARANCE = "appearance";
	public static final String PREF_DISPLAY_INTERVAL = "display_interval";
	public static final String PREF_DOT_COLOR = "dot_color";
	
	public static final int PREF_APPEARANCE_DOT = 0;
	public static final int PREF_APPEARANCE_LED = 1;
	
	TextView mTextView = null;
	
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        getPreferenceManager().setSharedPreferencesName(SHARED_PREFS_NAME);
	    addPreferencesFromResource(R.xml.preferences);
	    setContentView(R.layout.settings);

	    Button colorPicker = (Button) findViewById(R.id.ColorPicker);
	    colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = 
                	Settings.this.getSharedPreferences(SHARED_PREFS_NAME, 0).getInt(PREF_DOT_COLOR, Color.RED);
                new ColorPickerDialog(Settings.this, Settings.this, color).show();
            }
        });
	    
	    mTextView = (TextView) findViewById(R.id.Preview);
	    mTextView.setBackgroundColor(getSharedPreferences(SHARED_PREFS_NAME, 0).getInt(PREF_DOT_COLOR, Color.RED));
	    
		Intent serviceIntent = new Intent(this, MissEventNotifierService.class);
		startService(serviceIntent);
    }

	@Override
	public void colorChanged(int color) {
		Settings.this.getSharedPreferences(SHARED_PREFS_NAME, 0).edit().putInt(PREF_DOT_COLOR, color).commit();
	    mTextView.setBackgroundColor(color);
	}
}
