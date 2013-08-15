package com.device.security.analytics.androidsecurityanalytics.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalytics.FrontPageActivity;
import com.device.security.analytics.androidsecurityanalytics.helpers.AppResultsHelper;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.LockPatternUtils;
import com.device.security.analytics.androidsecurityanalytics.R;

public class AppUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		LockPatternUtils lockUtils = new LockPatternUtils(
				context.getApplicationContext());
		int score = AppResultsHelper.calculateRisk(context.getPackageManager(),
				lockUtils);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.icon)
				.setContentTitle("Device Security Analysis")
				.setContentText(
						"Your score is " + score + "/100 - "
								+ AnalyticsUtils.getLetterGrade(score) + "!");

		Intent resultIntent = new Intent(context, FrontPageActivity.class);

		try {
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(FrontPageActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(1, mBuilder.build());
		} catch (NoClassDefFoundError e) {
			//Log.d("No class def found", "TaskStackBuilder. Must be android version < 3");
		}

	}

}
