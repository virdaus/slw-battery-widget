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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AudioVolumeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent nIntent =new Intent(context,AudioVolumeService.class);
		nIntent.fillIn(intent, 0);
		context.startService(nIntent);
	}

}
