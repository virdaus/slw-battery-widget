/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Cache Cleaner Widget. (SLW Cache Cleaner Widget)
 * 
 * Simple Lightweight Cache Cleaner Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Cache Cleaner Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Cache Cleaner Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.SLWCacheCleanerWidget;


import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

public class CacheCleanerService extends Service {
	
	public static final String REFRESH_INTENT="tritop.android.slwcachecleanerwidget.REFRESH";
	public static final String CLEAR_INTENT="tritop.android.slwcachecleanerwidget.CLEAR";
	public static final long RECOUNTNDELAY=1500;
	private boolean mDND=false;
	private Handler mHandler;
	private int statsCounter;
	private long mCacheSum;
	private StatsObserver mStatsObs;
	private ClearCacheObserver mClearObs;
	private PackageManager mPM;
	private List<PackageInfo> mInstPkg;
	
	
	private Runnable mTriggerCount = new Runnable()
	{
	    @Override
	    public void run()
	    {
	     countCache();
	    }
	 };
	 
	 private Runnable mAutoKill = new Runnable()
		{
		    @Override
		    public void run()
		    {
		     stopSelf();
		    }
		 };
	
	
    //More info in ApplicationState.java @ android.git.kernel.org
	class StatsObserver extends IPackageStatsObserver.Stub{
		public void onGetStatsCompleted(PackageStats stats,boolean bl){
			mCacheSum+=stats.cacheSize;
			statsCounter++;
			if(statsCounter>=mInstPkg.size()){
				updateWidgets();
			}
		}
	}
	
	class ClearCacheObserver extends IPackageDataObserver.Stub {
	    public void onRemoveCompleted(final String packageName, final boolean succeeded) {
	     }
	 }
	
	private void countCache() {
		statsCounter = 0;
		mCacheSum = 0;
		mInstPkg= mPM.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES |
	               PackageManager.GET_DISABLED_COMPONENTS);
		for(PackageInfo pInfo: mInstPkg){
			   mPM.getPackageSizeInfo(pInfo.packageName, mStatsObs);
		}
	}
	
	private void clearCache(){
		mInstPkg= mPM.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES |
	               PackageManager.GET_DISABLED_COMPONENTS);
		mPM.freeStorageAndNotify(Integer.MAX_VALUE, mClearObs);
		mHandler.postDelayed(mTriggerCount, RECOUNTNDELAY);
	}
	
	private void updateWidgets(){
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWCacheCleanerWidget.class);
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		mCacheSum/=1024;
		String level=mCacheSum+" KB";
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);
			Intent intent = new Intent(CLEAR_INTENT);
			PendingIntent pendingInt = PendingIntent.getBroadcast(this, 99, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			rView.setOnClickPendingIntent(R.id.relativeLayoutBase, pendingInt);
			rView.setTextViewText(R.id.textViewCenter, level);
			appmanager.updateAppWidget(wid, rView);
		}
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		mStatsObs = new StatsObserver();
		mClearObs = new ClearCacheObserver();
		mPM = getPackageManager();
		mHandler = new Handler();
	}

	@Override
	public void onDestroy() {
		mHandler.removeCallbacks(mAutoKill);
		mHandler.removeCallbacks(mTriggerCount);
		mDND=false;
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(!mDND){
			mHandler.postDelayed(mAutoKill, 20000);
			mDND=true;
			mCacheSum=0;
			statsCounter=0;
			if(CLEAR_INTENT.equals(intent.getAction())){
				clearCache();
			}
			else{
				countCache();
			}
		}
	}

	

	
}
