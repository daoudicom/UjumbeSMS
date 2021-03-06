package com.istresearch.ujumbesms.service;

import android.app.IntentService;
import android.content.Intent;
import com.istresearch.ujumbesms.AmqpConsumer;
import com.istresearch.ujumbesms.App;

public class AmqpConsumerService extends IntentService {
    
    private App app;
    
    public AmqpConsumerService(String name)
    {
        super(name);        
    }
    
    public AmqpConsumerService()
    {
        this("AmqpConsumerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App)this.getApplicationContext();        
    }      
    
    @Override
    protected void onHandleIntent(Intent intent)
    {  
        boolean start = intent.getBooleanExtra("start", false);
        
        AmqpConsumer consumer = app.getAmqpConsumer();
        if (start)
        {                       
            consumer.startBlocking();
        }
        else
        {
            consumer.stopBlocking();            
        }
        
    }
}
