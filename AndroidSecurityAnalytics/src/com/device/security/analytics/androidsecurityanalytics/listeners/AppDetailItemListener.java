package com.device.security.analytics.androidsecurityanalytics.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.device.security.analytics.androidsecurityanalytics.ShowPermissionsActivity;
import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalytics.R;

public class AppDetailItemListener implements OnClickListener {

	private Context context;
	private AppDetailBean appDetailBean;
	private int ACTION;
	private String appName;

	public AppDetailItemListener(Context context, AppDetailBean appDetailBean, int ACTION, String appName) {
		this.context = context;
		this.appDetailBean = appDetailBean;
		this.ACTION = ACTION;
		this.appName = appName;
	}

	@Override
	public void onClick(View view) {

		if (ACTION == R.id.show_perms) {
			// Launching new Activity on selecting single List Item
			Intent i = new Intent(context, ShowPermissionsActivity.class);
			// sending data to new activity
			i.putExtra("permissions", appDetailBean.getPermissionBean()
					.getPermissions());
			i.putExtra("appName", appName);
			context.startActivity(i);
		}
		else if (ACTION == R.id.uninstall_button){
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(Uri
					.parse("package:" + appDetailBean.getPackageName()));
			context.startActivity(intent);
		}
	}

}
