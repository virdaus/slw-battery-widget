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


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class SLWAppHistoryWidget extends AppWidgetProvider {
	final static int UPDATETIME=1;
	
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(AppHistoryService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 55543277, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(AppHistoryService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 55543277, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000*60*UPDATETIME, pendingIntent);
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent intent = new Intent(AppHistoryService.REFRESH_INTENT);
		context.sendBroadcast(intent);
	}
    
}