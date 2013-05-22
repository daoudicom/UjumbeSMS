package com.istresearch.ujumbesms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
<<<<<<< HEAD:src/com/istresearch/ujumbesms/ui/Help.java
import com.istresearch.ujumbesms.R;

import com.istresearch.ujumbesms.App;
=======

import com.istresearch.ujumbesms.App;
import com.istresearch.ujumbesms.R;
>>>>>>> 40c527446f08d9368acbf2b7cfb985fe12567e83:src/com/istresearch/ujumbesms/ui/Help.java

public class Help extends Activity {

    private App app;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.help);
        
        TextView help = (TextView) this.findViewById(R.id.help);
                
        app = (App)getApplication();

        String html = "<b>"+getText(R.string.app_name)+" " + app.getPackageInfo().versionName + "</b><br /><br />"                
            + "Menu icons cc/by www.androidicons.com<br /><br />";
        
        help.setText(Html.fromHtml(html));                        
        
    }
    
    public void resetClicked(View v)
    {        
        new AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setPositiveButton("Yes", 
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        PreferenceManager.getDefaultSharedPreferences(app)                            
                            .edit()
                            .clear()
                            .commit();
                        
                        app.enabledChanged();
                        
                        dialog.dismiss();
                        
                        finish();
                    }
                }
            )
            .setNegativeButton("Cancel", 
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }
            )
            .show();
    }    
}
