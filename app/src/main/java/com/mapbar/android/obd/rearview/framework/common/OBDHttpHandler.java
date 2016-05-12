package com.mapbar.android.obd.rearview.framework.common;

import android.content.Context;

import com.mapbar.android.net.HttpHandler;


public class OBDHttpHandler extends HttpHandler {

    private final static String TASK_TAG = "OBDTask";
    private Context context;

    public OBDHttpHandler(Context context) {
        super(TASK_TAG, context);
        this.context = context;
    }

//	protected String getUserAgentString() {
//		String ua = super.getUserAgentString();
//		String channel = UmengUpdateAgentEx.getChannel(context);
//		if (channel != null && channel.length() > 0)
//			return ua + ";obdua;" + channel;
//		else
//			return ua;
//	}
}
