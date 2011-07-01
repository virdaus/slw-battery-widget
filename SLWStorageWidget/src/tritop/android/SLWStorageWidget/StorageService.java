/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Storage Widget. (SLW Storage Widget)
 * 
 * Simple Lightweight Storage Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Storage Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Battery Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.SLWStorageWidget;



import android.app.PendingIntent;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.widget.RemoteViews;
import 	android.os.StatFs;




public class StorageService extends IntentService {
	public static final String REFRESH_INTENT="tritop.android.storagewidget.action.REFRESH";
	
	public StorageService() {
		super("StorageService");
	}	

	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	
	private void updateWidgets(){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWStorageWidget.class);
		
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		
		String state = Environment.getExternalStorageState();
		StatFs stat=null;
		int blocks;
		int bytesPerBlock;
		long availableBytes;
        String sdCard,internal;
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	    	stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
	    	blocks = stat.getAvailableBlocks();
	    	bytesPerBlock = stat.getBlockSize();
	    	availableBytes=(long) blocks*bytesPerBlock;
	        sdCard=String.valueOf(availableBytes/(1024*1024));
	        stat=null;
	    } else {
	    	blocks=0;
	    	bytesPerBlock=0;
	    	sdCard="not ready";
	    }
  
	    stat= new StatFs(Environment.getDataDirectory().getPath());
	    blocks = stat.getAvailableBlocks();
    	bytesPerBlock = stat.getBlockSize();
    	availableBytes=(long)blocks*bytesPerBlock;
    	internal=String.valueOf(availableBytes/(1024*1024));
    	
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			Intent intent = new Intent(REFRESH_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rView.setOnClickPendingIntent(R.id.relativeLayoutRoot, pendingInt);
			rView.setTextViewText(R.id.tv_left, sdCard);
			rView.setTextViewText(R.id.tv_right, internal);
			appmanager.updateAppWidget(wid, rView);
		}
	}


	@Override
	protected void onHandleIntent(Intent arg0) {
		updateWidgets();
	}



}
