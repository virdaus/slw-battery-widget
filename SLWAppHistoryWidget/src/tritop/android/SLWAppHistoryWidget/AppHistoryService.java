/* 
 * Copyright (C) 2012 Christian Schneider
 * 
 * This file is part of Simple Lightweight App History Widget. (SWL App History Widget)
 * 
 * Simple Lightweight App History Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight App History Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight App History Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.SLWAppHistoryWidget;

import java.util.ArrayList;
import java.util.List;





import android.app.ActivityManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

public class AppHistoryService extends IntentService {
	public static final String REFRESH_INTENT="tritop.android.apphistoryservice.action.REFRESH";
	
	private PackageManager mPackageManager;
	private ActivityManager mActivityManager;
	private List<ActivityManager.RecentTaskInfo> recentTasks;
	private List<Intent> recentIntents;
	
	public AppHistoryService() {
		super("AppHistoryService");
	}

	@Override
	public void onCreate() {
		this.mPackageManager = this.getPackageManager();
		this.mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		recentIntents = new ArrayList<Intent>();
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(REFRESH_INTENT.equals(intent.getAction())){
		   recentTasks = mActivityManager.getRecentTasks(10,0);
		   recentIntents.clear();
		   parseRecentTaskInfo(recentTasks);
		}
		try {
			updateWidgets();
		} catch (NameNotFoundException e) {
			Log.v("AppHistory","Name not found");
		}
	}

	private void parseRecentTaskInfo(List<ActivityManager.RecentTaskInfo> tasks){
		for(ActivityManager.RecentTaskInfo task : tasks){
			if(task.baseIntent.getAction()!=null && 
			   task.baseIntent.getAction().equals(Intent.ACTION_MAIN) && 
			   (!task.baseIntent.hasCategory(Intent.CATEGORY_HOME) )){
				recentIntents.add(task.baseIntent);
			}
		}
	}
	
	
	private void updateWidgets() throws NameNotFoundException{
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWAppHistoryWidget.class);
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			Intent intent = new Intent(REFRESH_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rView.setOnClickPendingIntent(R.id.imageViewTask1, pendingInt);
			rView.setOnClickPendingIntent(R.id.imageViewTask2, pendingInt);
			rView.setOnClickPendingIntent(R.id.imageViewTask3, pendingInt);
			if(recentIntents.size()>=1){
				Drawable appIcon = mPackageManager.getActivityIcon(recentIntents.get(0));
				pendingInt = PendingIntent.getActivity(this, 1001, recentIntents.get(0), PendingIntent.FLAG_UPDATE_CURRENT);
				rView.setOnClickPendingIntent(R.id.imageViewTask1, pendingInt);
				rView.setImageViewBitmap(R.id.imageViewTask1, ((BitmapDrawable)appIcon).getBitmap());
			}
			if(recentIntents.size()>=2){
				Drawable appIcon = mPackageManager.getActivityIcon(recentIntents.get(1));
				pendingInt = PendingIntent.getActivity(this, 1002, recentIntents.get(1), PendingIntent.FLAG_UPDATE_CURRENT);
				rView.setOnClickPendingIntent(R.id.imageViewTask2, pendingInt);
				rView.setImageViewBitmap(R.id.imageViewTask2, ((BitmapDrawable)appIcon).getBitmap());
			}
			if(recentIntents.size()>=3){
				Drawable appIcon = mPackageManager.getActivityIcon(recentIntents.get(2));
				pendingInt = PendingIntent.getActivity(this, 1003, recentIntents.get(2), PendingIntent.FLAG_UPDATE_CURRENT);
				rView.setOnClickPendingIntent(R.id.imageViewTask3, pendingInt);
				rView.setImageViewBitmap(R.id.imageViewTask3, ((BitmapDrawable)appIcon).getBitmap());
			}
			
			appmanager.updateAppWidget(wid, rView);
		}
		
	}
	
	
}
