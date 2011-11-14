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



import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

public class AudioVolumeService extends IntentService {
	public static final String REFRESH_INTENT="tritop.android.audiovolumeservice.action.REFRESH";
	public static final String ADJUST_INTENT="tritop.android.audiovolumeservice.action.ADJUST";
	public static final String ADJUST_DIRECTION="ADJUST_DIRECTION";
	public static final String STREAM_TYPE="STREAM_TYPE";
	public static final int MAXHEIGHT=60;
	
	private AudioManager mAudioManager;
	private int[] mAudioLevel=new int[6];
	private int[] mMaxAudioLevel=new int[6];
	
	public AudioVolumeService() {
		super("AudioVolumeService");
	}

	@Override
	public void onCreate() {
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int mStreamType=0;
		int mAdjustDirection=0;
		
		if(ADJUST_INTENT.equals(intent.getAction())){
			if(intent.hasExtra(STREAM_TYPE) && intent.hasExtra(ADJUST_DIRECTION)){
				mStreamType=intent.getIntExtra(STREAM_TYPE, 0);
				mAdjustDirection=intent.getIntExtra(ADJUST_DIRECTION, 0);
				switch(mAdjustDirection){
					case 0:mAudioManager.adjustStreamVolume(mStreamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);break;
					case 1:mAudioManager.adjustStreamVolume(mStreamType, AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);break;
				}
			}
		}
		else if(REFRESH_INTENT.equals(intent.getAction())){
			//Nothing :-)
		}
		requestAudioLevels();
		updateWidget();
	}
	
	private void requestAudioLevels(){
		for(int i=0;i<6;i++){
			mAudioLevel[i]=mAudioManager.getStreamVolume(i);
			mMaxAudioLevel[i]=mAudioManager.getStreamMaxVolume(i);
		}
	}
	
	
	private void updateWidget(){
		int[] color={Color.rgb(0, 200, 0)};
		AppWidgetManager appmanager = AppWidgetManager.getInstance(this);
		ComponentName cmpName = new ComponentName(this, SLWAudioVolumeWidget.class);
		int[] widgetIds=appmanager.getAppWidgetIds(cmpName);
		for(int wid:widgetIds){
			RemoteViews rView = new RemoteViews(getPackageName(),R.layout.main);

			for(int i=0;i<6;i++){
				Bitmap onePixel= Bitmap.createBitmap(color, 1, 1, Bitmap.Config.ARGB_8888);
				Matrix matrix = new Matrix();
				float currentlevel=(float)mAudioLevel[i]/mMaxAudioLevel[i]*MAXHEIGHT;
				if(currentlevel<1){currentlevel=1;}
				matrix.postScale(50, currentlevel);
				Bitmap manyPixels = Bitmap.createBitmap(onePixel, 0, 0,1, 1, matrix, true); 
				manyPixels.setDensity(DisplayMetrics.DENSITY_HIGH);
				Intent raiseIntent = new Intent(ADJUST_INTENT);
				raiseIntent.putExtra(ADJUST_DIRECTION, 1);
				raiseIntent.putExtra(STREAM_TYPE, i);
				PendingIntent pendingIntRaise = PendingIntent.getBroadcast(this, 200+i, raiseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				Intent lowerIntent = new Intent(ADJUST_INTENT);
				lowerIntent.putExtra(ADJUST_DIRECTION, 0);
				lowerIntent.putExtra(STREAM_TYPE, i);
				PendingIntent pendingIntLower = PendingIntent.getBroadcast(this, 300+i, lowerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				switch(i){
					case AudioManager.STREAM_ALARM: rView.setImageViewBitmap(R.id.imageViewAlarmBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewAlarmRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewAlarmLower, pendingIntLower);break;
					case AudioManager.STREAM_MUSIC: rView.setImageViewBitmap(R.id.imageViewMusicBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewMusicRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewMusicLower, pendingIntLower);break;
					case AudioManager.STREAM_NOTIFICATION: rView.setImageViewBitmap(R.id.imageViewNotificationBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewNotificationRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewNotificationLower, pendingIntLower);break;
					case AudioManager.STREAM_RING: rView.setImageViewBitmap(R.id.imageViewRingBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewRingRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewRingLower, pendingIntLower);break;
					case AudioManager.STREAM_SYSTEM: rView.setImageViewBitmap(R.id.imageViewSystemBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewSystemRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewSystemLower, pendingIntLower);break;
					case AudioManager.STREAM_VOICE_CALL: rView.setImageViewBitmap(R.id.imageViewVoiceBack, manyPixels);rView.setOnClickPendingIntent(R.id.textViewVoiceRaise, pendingIntRaise);rView.setOnClickPendingIntent(R.id.textViewVoiceLower, pendingIntLower);break;
					default:break;
				}
				
			}
			
			appmanager.updateAppWidget(wid, rView);
		}
	}

}
