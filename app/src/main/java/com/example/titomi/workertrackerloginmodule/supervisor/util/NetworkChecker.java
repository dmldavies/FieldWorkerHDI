package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.titomi.workertrackerloginmodule.supervisor.activities.NoNetworkActivity;

public class NetworkChecker {
	static Context cxt;
	
	public NetworkChecker(Context cxt){
		NetworkChecker.cxt = cxt;
	}
	public static boolean haveNetworkConnection(Context cxt)
	{
	boolean haveConnectedWifi =false;
	boolean haveConnectedMobile =false;
	ConnectivityManager cm =(ConnectivityManager)
	cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo[] netInfo = cm.getAllNetworkInfo();

	for(NetworkInfo ni : netInfo){
	if(ni.getTypeName().equalsIgnoreCase("WIFI"))
	if(ni.isConnected())   haveConnectedWifi =true;

	if(ni.getTypeName().equalsIgnoreCase("MOBILE"))
	if(ni.isConnected()) haveConnectedMobile =true;
	}
		boolean hasNetwork = haveConnectedWifi || haveConnectedMobile;
		if(!hasNetwork) {
			cxt.startActivity(new Intent(cxt,NoNetworkActivity.class));
		}
	return hasNetwork ;
	}
}
