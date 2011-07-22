package tritop.android.SLWCacheCleanerWidget;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CacheCleanerReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent nIntent =new Intent(context,CacheCleanerService.class);
		nIntent.fillIn(intent, 0);
		context.startService(nIntent);
	}

}
