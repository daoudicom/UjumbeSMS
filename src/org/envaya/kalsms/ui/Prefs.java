package org.envaya.kalsms.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import org.envaya.kalsms.App;
import org.envaya.kalsms.R;

public class Prefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);
        
        PreferenceScreen screen = this.getPreferenceScreen();
        int numPrefs = screen.getPreferenceCount();
        
        for(int i=0; i < numPrefs;i++)
        {
            updatePrefSummary(screen.getPreference(i));
        }
    }    

    @Override 
    protected void onResume(){
        super.onResume();
        // Set up a listener whenever a key changes             
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override 
    protected void onPause() { 
        super.onPause();
        // Unregister the listener whenever a key changes             
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);     
    } 

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { 
        
        App app = (App) getApplication();
        
        if (key.equals("outgoing_interval"))
        {            
            app.setOutgoingMessageAlarm();
        }
        else if (key.equals("server_url"))
        {
            app.log("Server URL changed to: " + app.getDisplayString(app.getServerUrl()));
        }
        else if (key.equals("phone_number"))
        {
            app.log("Phone number changed to: " + app.getDisplayString(app.getPhoneNumber()));
        }
        else if (key.equals("password"))
        {
            app.log("Password changed");
        }
        else if (key.equals("enabled"))
        {
            app.log(app.isEnabled() ? "SMS Gateway started." : "SMS Gateway stopped.");
            app.setOutgoingMessageAlarm();
        }
        
        updatePrefSummary(findPreference(key));
    }    

    private void updatePrefSummary(Preference p)
    {
        if (p instanceof ListPreference) {
            p.setSummary(((ListPreference)p).getEntry()); 
        }
        else if (p instanceof EditTextPreference) {
            
            EditTextPreference textPref = (EditTextPreference)p;
            String text = textPref.getText();
            if (text == null || text.equals(""))
            {            
                p.setSummary("(not set)"); 
            }            
            else if (p.getKey().equals("password"))
            {
                p.setSummary("********");
            }
            else
            {
                p.setSummary(text);
            }
        }
    }    

    // any other time the Menu key is pressed
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.finish();
        return (true);
    }
}