package com.istresearch.ujumbesms;

import android.app.Activity;
import android.content.Intent;

public class UjumbeCache extends Activity{
	public void smsReceived(String number, String message){
		Intent i = new Intent("SplashActivity.intent.action.Launch");
		i.putExtra("number", number);
		i.putExtra("message", message);
		
		startActivityForResult(i, 1);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			String number = data.getStringExtra("number");
			String responses = data.getStringExtra("response");
			
		}
	}

}
