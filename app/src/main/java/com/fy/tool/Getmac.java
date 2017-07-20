package com.fy.tool;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Getmac {
	
	public Activity activity;
	
	
   
	public Getmac(Activity activity) {
		super();
		this.activity = activity;
	}



	public String getLocalMacAddress() {  
	    WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);  
	    WifiInfo info = wifi.getConnectionInfo();  
	    String mac = info.getMacAddress();
	    mac= mac.replaceAll(":", "");
	    String mac2 = "aca213c81600";
	    return mac;  
	}

  


}


