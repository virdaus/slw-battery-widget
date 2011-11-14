/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight Audio Volume Widget. (SWL Audio Volume Widget)
 * 
 * Simple Lightweight Audio Volume Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight Audio Volume Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Audio Volume Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.slwaudiovolumewidget;




import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SLWAudioVolumeWidget extends AppWidgetProvider {
	final static int UPDATETIME=5;
	
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(AudioVolumeService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111222333, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
	}

	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(AudioVolumeService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 111222333, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000*60*UPDATETIME, pendingIntent);
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		context.startService(new Intent(context,AudioVolumeService.class));
	}

}
