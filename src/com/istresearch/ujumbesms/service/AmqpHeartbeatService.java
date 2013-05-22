package com.istresearch.ujumbesms.service;

import android.app.IntentService;
import android.content.Intent;
<<<<<<< HEAD:src/com/istresearch/ujumbesms/service/AmqpHeartbeatService.java
import com.istresearch.ujumbesms.AmqpConsumer;

=======

import com.istresearch.ujumbesms.AmqpConsumer;
>>>>>>> 40c527446f08d9368acbf2b7cfb985fe12567e83:src/com/istresearch/ujumbesms/service/AmqpHeartbeatService.java
import com.istresearch.ujumbesms.App;

public class AmqpHeartbeatService extends IntentService {
    
    private App app;
    
    public AmqpHeartbeatService(String name)
    {
        super(name);        
    }
    
    public AmqpHeartbeatService()
    {
        this("AmqpHeartbeatService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App)this.getApplicationContext();        
    }      
    
    @Override
    protected void onHandleIntent(Intent intent)
    {          
        AmqpConsumer consumer = app.getAmqpConsumer();
        consumer.sendHeartbeatBlocking();
    }
}
