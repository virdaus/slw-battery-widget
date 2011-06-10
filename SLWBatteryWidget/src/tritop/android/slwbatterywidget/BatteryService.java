/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Battery Widget. (SWLBattery Widget)
 * 
 * Simple Lightweight Battery Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Battery Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Battery Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */


package tritop.android.slwbatterywidget;

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
import android.util.Log;
import android.widget.RemoteViews;

public class BatteryService extends Service {
	BroadcastReceiver batteryReceiver=null;
	private final static int MAXHEIGHT=80;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	
	@Override
	public void onCreate() {
		batteryReceiver = new BroadcastReceiver() {
	        int level = 0;
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            level = intent.getIntExtra("level", 0);
	            updateWidgets(level);
	        }
	    };
	    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    registerReceiver(batteryReceiver, filter);
	}


	@Override
	public void onDestroy() {
		unregisterReceiver(batteryReceiver);
		super.onDestroy();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	
	private void updateWidgets(int level){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWBatteryWidget.class);
        int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
        int[] green={Color.rgb(0, 200, 0)};
        int[] red={Color.rgb(200, 0, 0)};
        int[] yellow={Color.rgb(230, 230, 50)};
        int[] color;
        if(level<90){
        	color=red;
        } else if(level<95) {
        	color=yellow;
        }else{
        	color=green;
        }
        Bitmap onePixel= Bitmap.createBitmap(color, 1, 1, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        float currentlevel=(float)level/100*MAXHEIGHT;
        matrix.postScale(15, currentlevel);
        Bitmap manyPixels = Bitmap.createBitmap(onePixel, 0, 0,1, 1, matrix, true); 
        manyPixels.setDensity(DisplayMetrics.DENSITY_HIGH);
        for(int wid:widgetIds){
        	RemoteViews rView = new RemoteViews(getPackageName(), R.layout.main);
        	rView.setTextViewText(R.id.tv, level+"%");
        	rView.setImageViewBitmap(R.id.imageViewBack, manyPixels);
        	appmanager.updateAppWidget(wid, rView);
        }
	}
	

}
