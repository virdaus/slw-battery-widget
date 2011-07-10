package tritop.android.SLWTrafficMeterWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class TrafficMeterReceiver extends BroadcastReceiver {
    private final static String SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(SHUTDOWN.equals(intent.getAction())){
			SharedPreferences mPref=context.getSharedPreferences(TrafficMeterService.PREFS,Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mPref.edit();
			editor.putBoolean("SHUTDOWN", true);
			editor.commit();
			//This was the minimum now try a bit more before shutdown...
			Intent nIntent =new Intent(context,TrafficMeterService.class);
			nIntent.putExtra("ERSAVE", "SAVE");
			context.startService(nIntent);
		}
		else{
			Intent nIntent =new Intent(context,TrafficMeterService.class);
			nIntent.fillIn(intent, 0);
			context.startService(nIntent);
		}
	}

}
