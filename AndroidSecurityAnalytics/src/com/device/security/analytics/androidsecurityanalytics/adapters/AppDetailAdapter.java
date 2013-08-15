package com.device.security.analytics.androidsecurityanalytics.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalytics.listeners.AppDetailItemListener;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.AppDateUtil;
import com.device.security.analytics.androidsecurityanalytics.R;

public class AppDetailAdapter extends ArrayAdapter<AppDetailBean> {

	private ArrayList<AppDetailBean> items;
	private PackageManager packageManager;
	private Context context;

	public AppDetailAdapter(Context context, int textViewResourceId,
			ArrayList<AppDetailBean> items, PackageManager packageManager) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.packageManager = packageManager;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.app_detail_row, null);
		}
		AppDetailBean o = null;

		if (items != null)
			o = items.get(position);
		if (o != null) {
			TextView tt = (TextView) v.findViewById(R.id.app_detail_toptext);
			TextView bt = (TextView) v.findViewById(R.id.app_detail_bottomtext);
			TextView at = (TextView) v.findViewById(R.id.app_detail_agetext);
			ImageView it = (ImageView) v.findViewById(R.id.app_detail_icon);

			TextView thirdPartyTextView = (TextView) v.findViewById(R.id.app_detail_thirdparty);

			
			boolean isThirdPartyApp = AnalyticsUtils.isAppThirdParty(packageManager, o.getPackageName());
			
				
			

			
			Button uninstallButton = (Button) v
					.findViewById(R.id.uninstall_button);
			Button permsButton = (Button) v.findViewById(R.id.show_perms);

			uninstallButton.setOnClickListener(new AppDetailItemListener(
					context, o, R.id.uninstall_button, null));
			permsButton.setOnClickListener(new AppDetailItemListener(context,
					o, R.id.show_perms, o.getAppName()));

			context.getPackageManager();

			if (tt != null) {
				tt.setTypeface(null, Typeface.BOLD);
				tt.setText(o.getAppName());
			}
			
			if(thirdPartyTextView != null && isThirdPartyApp){
				thirdPartyTextView.setVisibility(View.VISIBLE);
				thirdPartyTextView.setTextColor(Color.RED);
				thirdPartyTextView.setText("Third party app!");
				//Third party app
			}
			else{
				thirdPartyTextView.setVisibility(View.GONE);
			}
			
			
			if (bt != null) {
				int numTop30 = o.getPermissionBean().getPermissionsInTop30();
				bt.setText("Requests " + numTop30 + " permissions often requested by Android Malware");
				//bt.setText(o.getPackageName());
			}
			if (at != null) {
				at.setText("Updated "
						+ AppDateUtil.getMonthsSinceUpdate(packageManager,
								o.getPackageName()) + " month(s) ago");
			}

			if (it != null) {
				try {

					Drawable dimage = packageManager.getApplicationIcon(o
							.getPackageName());

					Bitmap image = (Bitmap) ((BitmapDrawable) dimage)
							.getBitmap();
					it.setImageBitmap(image);
				} catch (NameNotFoundException e) {
					Log.d("Name Not Found Exception", e.getMessage());
				}

				// it.setImageBitmap(o.getImage());
			}

		}

		return v;
	}

	@Override
	public AppDetailBean getItem(int position) {
		return items.get(position);
	}
}