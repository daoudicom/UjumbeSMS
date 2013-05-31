package com.istresearch.ujumbesms.receiver;

import java.util.ArrayList;
import com.istresearch.ujumbesms.App;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class UjumbeCacheReceiver extends BroadcastReceiver {
  
	private Bundle extras;
	private String test = "recieved from ujumbe cache";
	private String fail = "nothing in the intent";
	private String TAG = "myApp";
	@Override
	public void onReceive(Context context, Intent intent) {
		extras = intent.getExtras();
		if (extras != null){
			SmsManager smgr = SmsManager.getDefault();
			String message = extras.getString("message");
			String to = extras.getString("number");
			ArrayList<String> bodyParts = smgr.divideMessage(message);
			
			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			ArrayList<PendingIntent> deliveryIntents = null;
			
	//----since data received from broadcast instead of sms we can be positive that we recieved all the parts
	//----since sms can only send a maximum number of 160 characters, as a result boolean deliveryReport will
	//----always be true
			boolean deliveryReport = true;
			if(deliveryReport){
				deliveryIntents = new ArrayList<PendingIntent>();
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
			Log.i(TAG, "send text message to user "+ to + ": " + message);			
		}
		else{
			Toast.makeText(context, fail, Toast.LENGTH_SHORT).show();
		}	
	}
}







