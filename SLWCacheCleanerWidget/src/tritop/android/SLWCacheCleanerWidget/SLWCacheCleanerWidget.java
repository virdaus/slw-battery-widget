package tritop.android.SLWCacheCleanerWidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SLWCacheCleanerWidget extends AppWidgetProvider {
    static final int UPDATETIME=5;
	
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(CacheCleanerService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234567890, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
	}

	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(CacheCleanerService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 234567890, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000*60*UPDATETIME, pendingIntent);
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		context.startService(new Intent(context,CacheCleanerService.class));
	}

}
