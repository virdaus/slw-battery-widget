package tritop.androidSLWCpuWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CpuReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		context.startService(new Intent(context,CpuService.class));
	}

}
