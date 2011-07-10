package tritop.android.SLWTrafficMeterWidget;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SLWTrafficMeterWidget extends AppWidgetProvider {
	public static final int UPDATETIME=1;
	
	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(TrafficMeterService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2345678, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
	}

	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(TrafficMeterService.REFRESH_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2345678, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000*60*UPDATETIME, pendingIntent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		context.startService(new Intent(context,TrafficMeterService.class));
	}

}
