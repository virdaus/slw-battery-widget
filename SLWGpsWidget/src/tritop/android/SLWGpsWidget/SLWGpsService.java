/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight gps Widget. (SWL gps Widget)
 * 
 * Simple Lightweight gps Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight gps Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Battery Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package tritop.android.SLWGpsWidget;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

public class SLWGpsService extends Service {
	
	public static final String SWITCH_INTENT="tritop.android.slwgpswidget.SWITCH";
	private boolean isRunning,isGPSenabled;
	private LocationManager locMan;
	private LocationListener locListener;
	private GpsStatus.Listener gpslistener;
	private double mLat,mLon,mAlt,mAcc;
	private boolean mFix=false;
	private int mTotalSatCount,mFixSatCount;
	private String mGPSDisabled1,mGPSDisabled2;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
			isRunning=false;
			isGPSenabled=false;
			mAcc=999.0d;
			mGPSDisabled1 = this.getString(R.string.gps_disabled1);
			mGPSDisabled2 = this.getString(R.string.gps_disabled2);
			locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
			locListener = new LocationListener(){

				@Override
				public void onLocationChanged(Location loc) {
					if(loc.getAccuracy()!=0 && loc.getAccuracy()<=mAcc){
						mFix=true;
						mLat=loc.getLatitude();
						mLon=loc.getLongitude();
						mAlt=loc.getAltitude();
						mAcc=loc.getAccuracy();
					}
				}

				@Override
				public void onProviderDisabled(String provider) {
					
					
				}

				@Override
				public void onProviderEnabled(String provider) {
					
					
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}
	        	
	        };
	        
	        gpslistener = new GpsStatus.Listener(){

				@Override
				public void onGpsStatusChanged(int event) {
					switch(event){
						case GpsStatus.GPS_EVENT_STARTED:break;
						case GpsStatus.GPS_EVENT_STOPPED:break;
						case GpsStatus.GPS_EVENT_FIRST_FIX:firstFix();break;
						case GpsStatus.GPS_EVENT_SATELLITE_STATUS:newSatStatus();break;
					}
					
				}
	        	
	        };
	        
	        mTotalSatCount=0;
			mFixSatCount=0;
			
			super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(isRunning){
			stopSelf();
		}
		
		
		
		if( intent != null && !intent.getBooleanExtra("Init", false)){
			setupNotification();
			locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
			locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
			locMan.addGpsStatusListener(gpslistener);
			isRunning=true;
		}
		
		updateWidgets();
		
		if( intent!=null && intent.getBooleanExtra("Init", false)){
			stopSelf();
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		locMan.removeUpdates(locListener);
		locMan.removeGpsStatusListener(gpslistener);
		super.onDestroy();
	}
	
	private void firstFix(){
		mFix=true;
	}
	
	private void newSatStatus(){
		GpsStatus status = locMan.getGpsStatus(null);
		mTotalSatCount=0;
		mFixSatCount=0;
		for(GpsSatellite sat:status.getSatellites()){
    		if(sat.usedInFix()){
    			mFixSatCount++;
    		}
    		mTotalSatCount++;
    	}
		updateWidgets();
	}

	private void setupNotification(){
		Notification nInfo= new Notification(R.drawable.notificationicon,"SLW GPS Widget active",System.currentTimeMillis());
		Intent btn_intent = new Intent(SWITCH_INTENT);
		PendingIntent pendingInt = PendingIntent.getBroadcast(this, 999, btn_intent, PendingIntent.FLAG_CANCEL_CURRENT);
		nInfo.setLatestEventInfo(this, "SLW GPS", "Widget active", pendingInt);
		startForeground(4567, nInfo);
	}
	
	private void updateWidgets(){
		isGPSenabled = locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWGpsWidget.class);
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			if(!isGPSenabled){
				rView.setTextViewText(R.id.textViewTopLeft, mGPSDisabled1 );
				rView.setTextViewText(R.id.textViewTopMiddle, "");
				rView.setTextViewText(R.id.textViewTopRight, mGPSDisabled2);
			}
			else if(isGPSenabled && !mFix){
				rView.setTextViewText(R.id.textViewTopLeft, "");
				rView.setTextViewText(R.id.textViewTopRight, "");
			}
			else if(mFix){
					rView.setTextViewText(R.id.textViewTopLeft, String.format("%3.3f", mLat));
					rView.setTextViewText(R.id.textViewTopMiddle, String.format("%4.0f", mAlt));
					rView.setTextViewText(R.id.textViewTopRight, String.format("%3.3f", mLon));
					rView.setTextViewText(R.id.textViewBottomLeft, String.format("%3.0f", mAcc));
			}
			rView.setTextViewText(R.id.textViewBottomRight, String.format("%1d/%1d", mFixSatCount,mTotalSatCount));
			Intent btn_intent = new Intent(SWITCH_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, btn_intent, PendingIntent.FLAG_CANCEL_CURRENT);
			rView.setOnClickPendingIntent(R.id.relativeLayout_base, pendingInt);
			appmanager.updateAppWidget(wid, rView);
		}
	}
	
}
