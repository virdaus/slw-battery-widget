/* 
 * Copyright (C) 2010 Christian Schneider
 * 
 * This file is part of Simple Lightweight gps Widget. (SWL gps Widget)
 * 
 * Simple Lightweight gps Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Simple Lightweight gps Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Simple Lightweight Battery Widget.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package tritop.android.SLWGpsWidget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class SLWGpsWidget extends AppWidgetProvider {

	
	@Override
	public void onDisabled(Context context) {
		
		super.onDisabled(context);
	}

	
	@Override
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
	}

	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent nIntent =new Intent(context,SLWGpsService.class);
		nIntent.putExtra("Init", true);
		context.startService(nIntent);
	}

}
