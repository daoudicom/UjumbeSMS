package com.istresearch.ujumbesms.receiver;

import com.istresearch.ujumbesms.OutgoingMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class UjumbeCacheReceiver extends BroadcastReceiver {
  
	private Bundle extras;
	private String test = "recieved from ujumbe cache";
	@Override
	public void onReceive(Context context, Intent intent) {
		extras = intent.getExtras();
		if (extras != null){
			String message = extras.getString("message");
			String phoneNumber = extras.getString("number");
			//OutgoingMessage text = new OutgoingMessage(context.getApplicationContext());
		}
		Toast.makeText(context, test, Toast.LENGTH_SHORT).show();
		
	}

}
