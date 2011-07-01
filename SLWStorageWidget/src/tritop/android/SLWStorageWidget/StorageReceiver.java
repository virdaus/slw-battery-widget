package tritop.android.SLWStorageWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StorageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context,StorageService.class));
	}

}
