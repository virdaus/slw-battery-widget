package tritop.android.SLWTrafficMeterWidget;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class TrafficMeterService extends IntentService {

	public static final String SWITCH_INTENT="tritop.android.slwtrafficmeterwidget.SWITCH";
	public static final String RESET_INTENT="tritop.android.slwtrafficmeterwidget.RESET";
	public static final String REFRESH_INTENT="tritop.android.slwtrafficmeterwidget.REFRESH";
	
	public static final String PREFS="TRAFFICMETERSERVICE";
	
	private static long mCell_zero,mWifi_zero,mTotal_zero;
	private static long mCell_now=0,mWifi_now=0,mTotal_now=0;
	private static int mViewmode;
	SharedPreferences mPref;
	
	public TrafficMeterService() {
		super("TrafficMeterService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mPref = getSharedPreferences(PREFS,MODE_PRIVATE);
		
		if(mPref.getBoolean("SHUTDOWN", false)==true && intent.getStringExtra("ERSAVE")==null){
			restoreData();
		}
		
		updateCounters();
		
		mViewmode = mPref.getInt("VIEWMODE", 0);
		
		if(RESET_INTENT.equals(intent.getAction())){
			SharedPreferences.Editor editor = mPref.edit();
		    editor.putLong("CELLZERO", mCell_now);
		    editor.putLong("WIFIZERO", mWifi_now);
		    editor.putLong("TOTALZERO", mTotal_now);
		    editor.putLong("CELLLAST", 0);
		    editor.putLong("WIFILAST", 0);
		    editor.putLong("TOTALLAST", 0);
		    editor.putLong("CELLEXTRA", 0);
		    editor.putLong("WIFIEXTRA", 0);
		    editor.putLong("TOTALEXTRA", 0);
		    editor.commit();
		}
		else if(SWITCH_INTENT.equals(intent.getAction())){
			mViewmode+=1;
			if(mViewmode>2){
				mViewmode=0;
			}
			SharedPreferences.Editor editor = mPref.edit();
		    editor.putInt("VIEWMODE", mViewmode);
		    editor.commit();
		}
		
		mCell_zero = mPref.getLong("CELLZERO", 0);
		mWifi_zero = mPref.getLong("WIFIZERO", 0);
		mTotal_zero = mPref.getLong("TOTALZERO", 0);
		
		updateWidgets();
	}

	private void restoreData() {
		long tmpCell,tmpWifi,tmpTotal;
		tmpTotal=mPref.getLong("TOTALEXTRA", 0)+mPref.getLong("TOTALLAST", 0);
		tmpCell=mPref.getLong("CELLEXTRA", 0)+mPref.getLong("CELLLAST", 0);
		tmpWifi=mPref.getLong("WIFIEXTRA", 0)+mPref.getLong("WIFILAST", 0);
		SharedPreferences.Editor editor = mPref.edit();
		editor.putLong("CELLEXTRA", tmpCell);
		editor.putLong("WIFIEXTRA", tmpWifi);
		editor.putLong("TOTALEXTRA", tmpTotal);
		editor.putLong("CELLZERO", 0);
	    editor.putLong("WIFIZERO", 0);
	    editor.putLong("TOTALZERO", 0);
		editor.putBoolean("SHUTDOWN", false);
		editor.commit();
	}

	private void updateCounters(){
		try {
			String line="N";
			Process process = new ProcessBuilder()
			   .command("/system/bin/dumpsys","netstat")
			   .redirectErrorStream(true)
			   .start();
			InputStream in = process.getInputStream();
			BufferedReader buffRead= new BufferedReader(new InputStreamReader(in));
			while ((line = buffRead.readLine()) != null) {
			    if(line.startsWith("Mobile")){
			    	mCell_now=0;
						for(String substr:line.split(" ")){
							for(String sub_substr:substr.split("/")){
								if(sub_substr.endsWith("B")){
									String traffic = sub_substr.replaceAll( "[^\\d]","");
									mCell_now+=Long.valueOf(traffic);
								}
						    	
							}
					    	
					    }
			    }
			    if(line.startsWith("Total")){
			    	mTotal_now=0;
			    		for(String substr:line.split(" ")){
			    			for(String sub_substr:substr.split("/")){
			    				if(sub_substr.endsWith("B")){
			    					String traffic = sub_substr.replaceAll( "[^\\d]","");
			    					mTotal_now+=Long.valueOf(traffic);
			    				}
			    			}
				    	
			    		}
			    }
			}
			process.destroy();
			mWifi_now=mTotal_now-mCell_now;
			SharedPreferences.Editor editor = mPref.edit();
		    editor.putLong("CELLLAST", mCell_now);
		    editor.putLong("WIFILAST", mWifi_now);
		    editor.putLong("TOTALLAST", mTotal_now);
		    editor.commit();
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateWidgets(){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWTrafficMeterWidget.class);
		
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		
		long display=0;
		String value="?";
		
	
		mWifi_zero = mPref.getLong("WIFIZERO", 0);
		
		
		if(mViewmode==0){
			display=mTotal_now-mTotal_zero;
			display+=mPref.getLong("TOTALEXTRA", 0);
		}
		else if(mViewmode==1){
			display=mCell_now-mCell_zero;
			display+=mPref.getLong("CELLEXTRA", 0);
		}
		else{
			display=mWifi_now-mWifi_zero;
			display+=mPref.getLong("WIFIEXTRA", 0);
		}
		
		int cnt=0;
		while(display>1024){
			display/=1024;
			cnt++;
		}
		
		
		switch(cnt){
			case 0:value=String.valueOf(display)+" B";break;
			case 1:value=String.valueOf(display)+" KB";break;
			case 2:value=String.valueOf(display)+" MB";break;
			case 3:value=String.valueOf(display)+" GB";break;
			case 4:value=String.valueOf(display)+" TB";break;
			case 5:value=String.valueOf(display)+" PB";break;
			default:value="err";
		}
		
		
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			Intent intent = new Intent(RESET_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rView.setOnClickPendingIntent(R.id.imageViewLeft, pendingInt);
			intent = new Intent(SWITCH_INTENT);
			pendingInt = PendingIntent.getBroadcast(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rView.setOnClickPendingIntent(R.id.imageViewRight, pendingInt);
			rView.setTextViewText(R.id.textViewCount, value);
			if(mViewmode==0){
				rView.setImageViewResource(R.id.imageViewSymbol, R.drawable.sum);
			}else if(mViewmode==1){
				rView.setImageViewResource(R.id.imageViewSymbol, R.drawable.cell);
			}else{
				rView.setImageViewResource(R.id.imageViewSymbol, R.drawable.wlan);
			}
			
			appmanager.updateAppWidget(wid, rView);
		}
	}
}
