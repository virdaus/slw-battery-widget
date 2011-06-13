/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Cpu Widget. (SWL Cpu Widget)
 * 
 * Simple Lightweight Cpu Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Cpu Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Battery Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.androidSLWCpuWidget;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public class CpuService extends Service {
	BroadcastReceiver bReceiver=null;
	private final static int MAXHEIGHT=65;
	public static final String REFRESH_INTENT="tritop.android.cpuwidget.action.refresh";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public void onCreate() {
		bReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            updateWidgets();
	        }
	    };
	    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    registerReceiver(bReceiver, filter);
	    filter = new IntentFilter(REFRESH_INTENT);
	    registerReceiver(bReceiver, filter);
	}


	@Override
	public void onDestroy() {
		unregisterReceiver(bReceiver);
		super.onDestroy();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	
	private void updateWidgets(){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWCpuWidget.class);
		
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		int[] green={Color.rgb(0, 200, 0)};
		int[] red={Color.rgb(200, 0, 0)};
		int[] yellow={Color.rgb(230, 230, 50)};
		int[] color;
		int level = 0;
		
		String line="NIX";
		try {
			Process process = new ProcessBuilder()
			   .command("/system/bin/top")
			   .redirectErrorStream(true)
			   .start();
			InputStream in = process.getInputStream();
			BufferedReader buffRead= new BufferedReader(new InputStreamReader(in));
			while ((line = buffRead.readLine()) != null) {
			    if(line.startsWith("User")){
			    	break;
			    }
			}
			process.destroy();
		} catch ( Exception e) {
			e.printStackTrace();
		}
	    for(String substr:line.split(",")){
	    	String result = substr.replaceAll( "[^\\d]","");
	    	level += Integer.valueOf(result);
	    }
		
		if(level>90){
			color=red;
		} else if(level>70) {
			color=yellow;
		} else{
			color=green;
		}
		
		Bitmap onePixel= Bitmap.createBitmap(color, 1, 1, Bitmap.Config.ARGB_8888);
		Matrix matrix = new Matrix();
		float currentlevel=(float)level/100*MAXHEIGHT;
		if(currentlevel<1){currentlevel=1;}
		matrix.postScale(20, currentlevel);
		Bitmap manyPixels = Bitmap.createBitmap(onePixel, 0, 0,1, 1, matrix, true); 
		manyPixels.setDensity(DisplayMetrics.DENSITY_HIGH);
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			Intent intent = new Intent(REFRESH_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			rView.setOnClickPendingIntent(R.id.relativeLayoutRoot, pendingInt);
			rView.setTextViewText(R.id.tv, level+"%");
			rView.setImageViewBitmap(R.id.imageViewBack, manyPixels);
			appmanager.updateAppWidget(wid, rView);
		}
	}

}
