package com.istresearch.ujumbesms.receiver;

import java.util.ArrayList;
import com.istresearch.ujumbesms.App;
import com.istresearch.ujumbesms.IncomingSms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import com.istresearch.ujumbesms.ui.Prefs;
import android.app.Activity;
import com.istresearch.ujumbesms.ui.LogView;
import com.istresearch.ujumbesms.ui.Main;

public class UjumbeCacheReceiver extends BroadcastReceiver {
  
	private Bundle extras;
	private String test = "recieved from ujumbe cache";
	private String fail = "nothing in the intent";
	private String TAG = "myApp";
	private App app;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		app = (App)context.getApplicationContext();
		
		extras = intent.getExtras();
		if (extras != null){
			
			SmsManager smgr = SmsManager.getDefault();
			//ArrayList<String> bodyParts = extras.getStringArrayList("body");
			String message = extras.getString("message");
			ArrayList<String> bodyParts = smgr.divideMessage(message);
			String to = extras.getString("to");
			boolean deliveryReport = extras.getBoolean("delivery",false);
			
			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			ArrayList<PendingIntent> deliveryIntents = null;
			
			
			
	//----since data received from broadcast instead of sms we can be positive that we recieved all the parts
	//----since sms can only send a maximum number of 160 characters, as a result boolean deliveryReport will
	//----always be true
			deliveryReport = true;
			if(deliveryReport){
				deliveryIntents = new ArrayList<PendingIntent>();
				Log.i(TAG, "In 1st deliveryReport");
			}
			int numParts = bodyParts.size();
			for(int j = 0; j < numParts; j++)
			{
				Intent statusIntent = new Intent(App.MESSAGE_STATUS_INTENT, intent.getData());
				statusIntent.putExtra(App.STATUS_EXTRA_INDEX, j);
				statusIntent.putExtra(App.STATUS_EXTRA_NUM_PARTS, numParts);
				sentIntents.add(PendingIntent.getBroadcast(context,0,statusIntent,PendingIntent.FLAG_ONE_SHOT));
				
				if(deliveryReport)
				{
					Intent deliveryIntent = new Intent(App.MESSAGE_DELIVERY_INTENT, intent.getData());
					deliveryIntent.putExtra(App.STATUS_EXTRA_INDEX, j);
					deliveryIntent.putExtra(App.STATUS_EXTRA_NUM_PARTS, numParts);
					
					deliveryIntents.add(PendingIntent.getBroadcast(context,0,deliveryIntent,PendingIntent.FLAG_ONE_SHOT));
				}
			}
			
			//Toast.makeText(context, test, Toast.LENGTH_SHORT).show();
			//Log.i(TAG, "received from UjumbeCache" + message +"  "+to);
			
			
			smgr.sendMultipartTextMessage(to, null, bodyParts, sentIntents, deliveryIntents);
			IncomingSms sms = new IncomingSms(app);
			//sms.setMessagingId(id);
			sms.setTo(to);
			sms.setMessageBody(message);
			//sms.setTimestamp(xxxxx);
			sms.setDirection(IncomingSms.Direction.Sent);
			Log.i(TAG, "created SMS object");
			app.inbox.forwardMessage(sms);			
			Log.i(TAG, "send text message to user ADD "+ to + ": " + message);
			
		}
		else{
			Toast.makeText(context, fail, Toast.LENGTH_SHORT).show();
		}	
	}
}







