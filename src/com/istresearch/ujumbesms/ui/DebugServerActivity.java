package com.istresearch.ujumbesms.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.istresearch.ujumbesms.App;
import com.istresearch.ujumbesms.R;
import com.istresearch.ujumbesms.task.HttpTask;
import com.istresearch.ujumbesms.task.PollerTask;

public class DebugServerActivity extends Activity {
	private class TestTask extends HttpTask {
		public TestTask(App aApp) {
			super(aApp, new BasicNameValuePair("action", App.ACTION_TEST));   
		}
		
		@Override
		protected void handleResponse(HttpResponse aResponse) throws Exception {
			mDebugView.loadData(convertToString(aResponse.getEntity().getContent()),"text/html", "utf-8");
		}
	}

	private class DebugPollerTask extends PollerTask {
		public DebugPollerTask(App aApp) {
			super(aApp);   
		}
		
		@Override
		protected void handleResponse(HttpResponse aResponse) throws Exception {
			mDebugView.loadData(convertToString(aResponse.getEntity().getContent()),"text/html", "utf-8");
		}
	}

	private TextView mInfoView;
	private TextView mHeaderView;
	private WebView mDebugView;
	
	private BroadcastReceiver settingsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context aContext, Intent aIntent) {  
			updateInfo();
		}
	};
	
	private App getApp() {
		return (App)getApplication();
	}
	
	public String convertToString(InputStream aInputStream) {
		StringBuffer theStringBuffer = new StringBuffer();
		BufferedReader theReader = new BufferedReader(new InputStreamReader(aInputStream));
		String theLine;
		try {
			while ((theLine = theReader.readLine()) != null) {
				theStringBuffer.append(theLine + "\n");
			}
		} catch (IOException e) {}
		return theStringBuffer.toString();
	}
	
	private synchronized void checkOutgoingMessages() {
		App anApp = getApp();
		String theServerUrl = anApp.getServerUrl();
		if (theServerUrl.length()>0) {
			anApp.log("Checking for messages");
			new DebugPollerTask(anApp).execute();
		} else {
			anApp.log("Can't check messages; server URL not set");
		}
    }

	public String getSettingsSummary() {
		App anApp = getApp();
		StringBuilder builder = new StringBuilder();
		
		if (anApp.getKeepInInbox()) {
			builder.append("- New messages kept in Messaging inbox\n");
		} else {
			builder.append("- New messages not kept in Messaging inbox\n");
		}
		
		if (anApp.callNotificationsEnabled()) {
			builder.append("- Call notifications enabled\n");
		} else {
			builder.append("- Call notifications disabled\n");
		}
		
		List<String> ignoredNumbers = anApp.getIgnoredPhoneNumbers();
		boolean ignoreShortcodes = anApp.ignoreShortcodes();
		boolean ignoreNonNumeric = anApp.ignoreNonNumeric();
		boolean testMode = anApp.isTestMode();
		
		builder.append("- Send up to " + anApp.getOutgoingMessageLimit()+ " SMS/hour\n");		
		
		if (ignoredNumbers.isEmpty() && !ignoreShortcodes && !ignoreNonNumeric && !testMode) {
			builder.append("- Forward messages from all phone numbers");
		} else if (testMode) {
			builder.append("- Forward messages only from certain phone numbers");
		} else {
			builder.append("- Ignore messages from some phone numbers");
		}			
		
		return builder.toString();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppPrefs.setDefaultPrefs(this);

		setContentView(R.layout.debug_http_response);
		
		mInfoView = (TextView)findViewById(R.id.TextView_Info);
		mHeaderView = (TextView)findViewById(R.id.TextView_Heading);
		mDebugView = (WebView)findViewById(R.id.WebView_Debug);
		
		updateInfo();
		registerReceiver(settingsReceiver, new IntentFilter(App.SETTINGS_CHANGED_INTENT));		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu aMenu) {
		getMenuInflater().inflate(R.menu.options_debug_view, aMenu);
		
		Intent prefsActivity = new Intent(this,AppPrefs.class);
		prefsActivity.addCategory(Intent.CATEGORY_PREFERENCE);
		prefsActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// Get the settings activity and use most of it's definitions to auto-handle menu item
		MenuItem theSettingsTemplate = aMenu.findItem(R.id.menu_item_settings);
		aMenu.removeItem(R.id.menu_item_settings);
		aMenu.addIntentOptions(
			 theSettingsTemplate.getGroupId(),	// Menu group to which new items will be added
			 theSettingsTemplate.getItemId(),	// Unique item ID
			 theSettingsTemplate.getOrder(),		// Order for the item
			 this.getComponentName(),	// The current Activity name
			 null,						// Specific items to place first (none)
			 prefsActivity,				// Intent created above that describes our requirements
			 0,							// Additional flags to control items (none)
			 null);	// Array of MenuItems that correlate to specific items (none)
		//Android Market will not allow android drawables as part of the Manifest xml
		MenuItem theSettingsItem = aMenu.findItem(R.id.menu_item_settings);
		theSettingsItem.setIcon(android.R.drawable.ic_menu_preferences);
		
		return true;
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(settingsReceiver);
		super.onDestroy();
	}
	
	public void onInfoClicked(View v) {
		checkOutgoingMessages();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem aMenuItem) {
		switch (aMenuItem.getItemId()) {
			case R.id.menu_check_now: 
				checkOutgoingMessages();
				return true;
			case R.id.menu_test:
				new TestTask(getApp()).execute();
				return true;
	        case R.id.retry_now:
	            getApp().retryStuckMessages();
	            return true;
			default:
				return super.onOptionsItemSelected(aMenuItem);
		}
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem retryItem = menu.findItem(R.id.retry_now);
        int pendingTasks = getApp().getPendingTaskCount();
        retryItem.setEnabled(pendingTasks > 0);
        retryItem.setTitle("Retry All (" + pendingTasks + ")");
        return true;
    }
    
	@Override
	protected void onPause() {
		getApp().mDebugView = null;
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getApp().mDebugView = new WeakReference<WebView>(mDebugView);
	}
	
	public void updateInfo() {
		boolean bEnabled = getApp().isEnabled();
		String s = bEnabled ? "<b>" + getText(R.string.running) + " ("+getApp().getPhoneNumber()+")</b>" 
				: "<b>" +getText(R.string.disabled) + "</b>";
		mHeaderView.setText(Html.fromHtml(s));
		
		if (bEnabled) {
			mInfoView.setText("New messages will be forwarded to server");
			if (getApp().isTestMode()) {
				mInfoView.append("\n(Test mode enabled)");
			}			
		} else {
			mInfoView.setText("New messages will not be forwarded to server");
		}
	}

}