package tritop.androidSLWCpuWidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SLWCpuWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		
	}

	@Override
	public void onEnabled(Context context) {
		context.startService(new Intent(context,CpuService.class));
	}

	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context,CpuService.class));
	}

}
