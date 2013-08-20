package com.device.security.analytics.androidsecurityanalytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.device.security.analytics.androidsecurityanalytics.adapters.FaqAdapter;
import com.device.security.analytics.androidsecurityanalytics.adapters.PermAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.PermBean;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;

public class ShowPermissionsActivity extends Activity {

	private ListView permissionListView;
	private ArrayAdapter<String> listAdapter;
	ExpandableListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		String appName = i.getStringExtra("appName");
		setTitle(appName + " Permissions");
		String[] permissions = i.getStringArrayExtra("permissions");
		
		if (permissions != null) {

			setContentView(R.layout.perm_layout);

			DatabaseHelper dbHelper = new DatabaseHelper(
					getApplicationContext());

			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
				//Log.d("Exception", e.getMessage());
			}
			dbHelper.openDataBase();

			ArrayList<PermBean> permBeans = dbHelper.getPerms();

			ArrayList<PermBean> matchingPerms = new ArrayList<PermBean>();

			for (int j = 0; j < permissions.length; j++) {
				String string = permissions[j];

				PermBean pb = AnalyticsUtils.getMatchingPermBean(permBeans, string);
				if (pb != null) {
					matchingPerms.add(pb);
				}

			}

			listView = (ExpandableListView) findViewById(R.id.perm_list);
			final PermAdapter faqAdapter = new PermAdapter(this, matchingPerms);
			listView.setAdapter(faqAdapter);

		}

	}



}
