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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppHistoryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent nIntent =new Intent(context,AppHistoryService.class);
		nIntent.fillIn(intent, 0);
		context.startService(nIntent);
	}

}
