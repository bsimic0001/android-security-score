package com.device.security.analytics.androidsecurityanalytics;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.device.security.analytics.androidsecurityanalytics.adapters.AppDetailAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class AppDetailAction extends Activity {

	private ListView mainListView;
	
	private ArrayAdapter<String> listAdapter;
	
	private ArrayList<AppDetailBean> m_detailbeans = null;
    private AppDetailAdapter m_adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		
		String type = intent.getStringExtra("type");
		
		if(type != null && type.equals("all"))
			setTitle("All Apps Analyzed");
		else if(type != null && type.equals("trusted"))
			setTitle("Apps Marked Trusted");
		else
			setTitle(type + " Risk Apps");
		
		ArrayList<AppDetailBean> apps = new ArrayList<AppDetailBean>(); 
		apps = intent.getParcelableArrayListExtra("apps");
		setContentView(R.layout.activity_main);
		this.m_adapter = new AppDetailAdapter(this, R.layout.app_detail_row, apps, getPackageManager());
		mainListView = (ListView) findViewById(R.id.list);

		// Set the ArrayAdapter as the ListView's adapter.
		mainListView.setAdapter(m_adapter);

		// listening to single list item on click
		mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// Launching new Activity on selecting single List Item
				Intent i = new Intent(getApplicationContext(),
						ShowPermissionsActivity.class);
				// sending data to new activity
				TextView appNameView = (TextView) view.findViewById(R.id.app_detail_toptext);
				//i.putExtra("appName", appNameView.getText());
				i.putExtra("permissions", m_adapter.getItem(position).getPermissionBean().getPermissions());
				startActivity(i);
			}
		});
	}
}
