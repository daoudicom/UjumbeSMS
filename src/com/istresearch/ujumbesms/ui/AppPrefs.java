package com.istresearch.ujumbesms.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.istresearch.app.AppPreferenceBase;
import com.istresearch.ujumbesms.App;
import com.istresearch.ujumbesms.R;

public class AppPrefs extends AppPreferenceBase implements OnSharedPreferenceChangeListener {
	static public final int[] PREFERENCE_RESOURCE_IDS = {
		R.layout.app_prefs, 
		R.layout.app_prefs_server,
		R.layout.app_prefs_sms,
		R.layout.app_prefs_networking,
		R.layout.app_prefs_ampq };

    //prefs menu
    private static final int MENU_ITEM_RESET_PREFS = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu aMenu) {
    	aMenu.add(Menu.NONE,MENU_ITEM_RESET_PREFS,0,R.string.menu_item_reset_prefs)
    		.setIcon(android.R.drawable.ic_menu_preferences);
		return super.onCreateOptionsMenu(aMenu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_ITEM_RESET_PREFS: {
				resetPrefs();
				return true;
        	}
		}
		return false;
	}
	
	@Override
	protected void setup() {
		//nothing to do here
	}
	
    @Override 
    protected void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes
		SharedPreferences theSettings = getPrefs(this);
		if (theSettings!=null) {
			theSettings.registerOnSharedPreferenceChangeListener(this);
		}
    }

    @Override 
    protected void onPause() { 
        // Unregister the listener
		SharedPreferences theSettings = getPrefs(this);
		if (theSettings!=null) {
			theSettings.unregisterOnSharedPreferenceChangeListener(this);
		}
        super.onPause();
    } 

	@Override
	protected int[] getPrefResources() {
		return PREFERENCE_RESOURCE_IDS;
	}
	
	/**
	 * Call this method in main activity's onCreate
	 * @param aContext - main activity
	 */
	static public void setDefaultPrefs(Context aContext) {
		setDefaultPrefs(aContext,PREFERENCE_RESOURCE_IDS,false);
	}
	
    public void onSharedPreferenceChanged(SharedPreferences aSettings, String aPrefKey) { 
    	App anAct = (App)getApplication();    	
		String thePrefKey;
		
		thePrefKey = anAct.getString(R.string.pref_key_poll_interval);
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
			anAct.setOutgoingMessageAlarm();
		}
		
		thePrefKey = "amqp_";
		if (aPrefKey==null || aPrefKey.startsWith(thePrefKey)) {
            if (anAct.isAmqpEnabled()) {
                anAct.getAmqpConsumer().startAsync();
            } else {
                anAct.getAmqpConsumer().stopAsync();
            }
		}

		//as of API-17, WIFI_SLEEP_POLICY is a secure setting, regular apps cannot modify it.
        if (Build.VERSION.SDK_INT<17) {
    		thePrefKey = anAct.getString(R.string.pref_key_wifi_sleep_policy);
    		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
                int value = Settings.System.WIFI_SLEEP_POLICY_DEFAULT;
                String valueStr = aSettings.getString(thePrefKey,"screen");
                if ("never".equals(valueStr)) {
                    value = Settings.System.WIFI_SLEEP_POLICY_NEVER;
                } else if ("plugged".equals(valueStr)) {
                    value = Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED;
                }
               	Settings.System.putInt(getContentResolver(),Settings.System.WIFI_SLEEP_POLICY,value);
            }
        }

        thePrefKey = anAct.getString(R.string.pref_key_server_url);
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
			String serverUrl = aSettings.getString(thePrefKey,"");
			// assume http:// scheme if none entered
			if (serverUrl.length() > 0 && !serverUrl.contains("://")) {
                aSettings.edit().putString(thePrefKey, "http://"+serverUrl).commit();
            }
            anAct.log("Server URL changed to: " + anAct.getDisplayString(anAct.getServerUrl()));
        }
		
        thePrefKey = anAct.getString(R.string.pref_key_phone_number);
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
			anAct.log("Phone number changed to: " + anAct.getDisplayString(anAct.getPhoneNumber()));
        }
		
		thePrefKey = "call_notifications";
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
			anAct.log("Call notifications changed to: " + (anAct.callNotificationsEnabled() ? "ON": "OFF"));
        }

		thePrefKey = "test_mode";
   		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
        	anAct.log("Test mode changed to: " + (anAct.isTestMode() ? "ON": "OFF"));
        }        
   		
        thePrefKey = anAct.getString(R.string.pref_key_server_pw);
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
        	anAct.log("Password changed");
        }

        thePrefKey = anAct.getString(R.string.pref_key_enabled);
		if (aPrefKey==null || aPrefKey.equals(thePrefKey)) {
        	anAct.log(anAct.isEnabled() ? getText(R.string.started) : getText(R.string.stopped));
        	anAct.enabledChanged();
        }
        
        sendBroadcast(new Intent(App.SETTINGS_CHANGED_INTENT));
    }    

    /*
    private void updatePrefSummary(Preference p)
    {
        String key = p.getKey();
        
        else if ("send_limit".equals(key))
        {
            int limit = mApp.getOutgoingMessageLimit();
            String limitStr = "Send up to " + limit + " SMS per hour.";
            
            if (limit < 300)
            {
                limitStr += "\nClick to increase limit...";
            }
            
            p.setSummary(limitStr);
        }        
    }    
	*/
}
