package com.istresearch.ujumbesms.task;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;

import com.istresearch.ujumbesms.IncomingMessage;

public class DebugForwarderTask extends ForwarderTask {

    public DebugForwarderTask(IncomingMessage message, BasicNameValuePair... paramsArr) {
        super(message, paramsArr);
    }
    
    @Override
    protected void handleResponse(HttpResponse aResponse) throws Exception {
    	if (app.mDebugView!=null) {
    		app.mDebugView.get().loadData(IOUtils.toString(aResponse.getEntity().getContent(),"UTF-8"),"text/html", "utf-8");
    	} else {
    		app.log(IOUtils.toString(aResponse.getEntity().getContent(),"UTF-8"));
    	}
    }

    @Override
    protected void handleFailure() {        
    } 
}
