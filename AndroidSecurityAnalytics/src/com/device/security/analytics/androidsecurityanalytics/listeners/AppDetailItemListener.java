package com.device.security.analytics.androidsecurityanalytics.listeners;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import com.device.security.analytics.androidsecurityanalytics.ShowPermissionsActivity;
import com.device.security.analytics.androidsecurityanalytics.adapters.AppDetailAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class AppDetailItemListener implements OnClickListener {

	private Context context;
	private AppDetailBean appDetailBean;
	private int ACTION;
	private String appName;
	boolean needToRemove;
	private AppDetailAdapter adapter;

	public AppDetailItemListener(Context context, AppDetailBean appDetailBean,
			int ACTION, String appName, AppDetailAdapter adapter) {
		this.context = context;
		this.appDetailBean = appDetailBean;
		this.ACTION = ACTION;
		this.appName = appName;
		this.adapter = adapter;
	}

	@Override
	public void onClick(final View view) {

		if (ACTION == R.id.show_perms) {
			// Launching new Activity on selecting single List Item
			Intent i = new Intent(context, ShowPermissionsActivity.class);
			// sending data to new activity
			i.putExtra("permissions", appDetailBean.getPermissionBean()
					.getPermissions());
			i.putExtra("appName", appName);
			context.startActivity(i);
		} else if (ACTION == R.id.uninstall_button) {
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri.parse("package:"
					+ appDetailBean.getPackageName()));
			context.startActivity(intent);
		} else if (ACTION == R.id.trust_button) {

			// System.out.println(trustedApps.size());

			final DatabaseHelper dbHelper = new DatabaseHelper(context);

			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
				// Log.d("Exception", e.getMessage());
			}
			dbHelper.openDataBase();

			final boolean isAppTrusted = dbHelper.isTrustedApp(appDetailBean
					.getPackageName());

			String title = "Are you sure?";
			String message;
			String buttonText;

			if (isAppTrusted) {
				message = "Mark this app as untrusted?";
				buttonText = "Mark Untrusted";
			} else {
				message = "You should only mark an app as trusted if you're 100% sure.";
				buttonText = "Mark Trusted";
			}

			new AlertDialog.Builder(context)
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton(buttonText,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									if (!isAppTrusted) {

										dbHelper.insertTrustedApp(
												appDetailBean.getPackageName(),
												true);
									} else {
										dbHelper.deleteTrustedApp(appDetailBean
												.getPackageName());
									}
									needToRemove = true;
									adapter.removeItemAndUpdate(appDetailBean);
								}
							})
					.setNegativeButton("Do Nothing",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();

			
			if(needToRemove){
				view.setVisibility(View.GONE);
			}
		}
	}

}
