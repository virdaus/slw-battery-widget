/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Wlan scan Widget. (SLW Wlan Scan Widget)
 * 
 * Simple Lightweight Wlan scan Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Wlan scan Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Wlan scan Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.SLWWlanWidget;


import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.RemoteViews;

public class SLWWlanService extends IntentService {
    

	public SLWWlanService() {
		super("SLWWlanService");
	}

    private static final String PREFS="WLANSERVICE";
    public static final String INTENT_FLIP_SWITCH="tritop.android.slwwlanwidget.FLIP_SWITCH";
    private WifiManager wifiMgr;
	private boolean scan;

	@Override
	public void onCreate() {
		wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent.hasExtra("FLIP_SWITCH")){
			SharedPreferences mPref = getSharedPreferences(PREFS,MODE_PRIVATE);
			scan = mPref.getBoolean("scan", false);
			scan = !scan;
			SharedPreferences.Editor editor = mPref.edit();
		    editor.putBoolean("scan", scan);
		    editor.commit();
		}
		else{
			SharedPreferences mPref = getSharedPreferences(PREFS,MODE_PRIVATE);
			scan = mPref.getBoolean("scan", false);
		}
		updateWidgets(intent);
	}

	private void updateWidgets(Intent intent){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWWlanWidget.class);
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		List<ScanResult> results = wifiMgr.getScanResults();
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			rView.removeAllViews(R.id.linearLayout1);
			int cnt=1;
			if(results!=null){
				for(ScanResult result: results){
					RemoteViews rViewChannel;
					if(cnt%2==0){
						rViewChannel = new RemoteViews(getPackageName(),R.layout.channelline);
					}
					else{
						rViewChannel = new RemoteViews(getPackageName(),R.layout.channellinediff);
					}
					rViewChannel.setTextViewText(R.id.channelLine,String.format("Ch: %2d  %2ddb  %s",((result.frequency-2407)/5),result.level,result.SSID));
					rView.addView(R.id.linearLayout1, rViewChannel);
					cnt++;
				}
			}
			Intent btn_intent = new Intent(INTENT_FLIP_SWITCH);
			btn_intent.putExtra("FLIP_SWITCH", 1);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, btn_intent, PendingIntent.FLAG_CANCEL_CURRENT);
			rView.setOnClickPendingIntent(R.id.imageViewBtn, pendingInt);
			if(scan){
				rView.setImageViewResource(R.id.imageViewBtn, R.drawable.btnstop45);
			}
			else{
				rView.setImageViewResource(R.id.imageViewBtn, R.drawable.btnscan45);
			}
			appmanager.updateAppWidget(wid, rView);
		}
		if(scan){
			startScan();
		}
	}
	
	private void startScan(){
        if(!wifiMgr.isWifiEnabled()){
        	wifiMgr.setWifiEnabled(true);
        }
        wifiMgr.startScan();
	}
	
	
}
