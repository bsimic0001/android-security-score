package com.device.security.analytics.androidsecurityanalytics;

import java.io.IOException;
import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.device.security.analytics.androidsecurityanalytics.adapters.ImageAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalytics.beans.TrustedApp;
import com.device.security.analytics.androidsecurityanalytics.helpers.AppResultsHelper;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;

public class AppResultsActivity extends ListActivity {

	private ArrayList<AppDetailBean> allAppsList;
	private ArrayList<AppDetailBean> criticalAppsList;
	private ArrayList<AppDetailBean> highAppsList;
	private ArrayList<AppDetailBean> mediumAppsList;
	private ArrayList<AppDetailBean> lowAppsList;
	private ArrayList<AppDetailBean> trustedApps;
	private String[] listItems;
	private DatabaseHelper dbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.app_category_main);
		new ShowAppResultsTask(this, "Getting app details...").execute();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		setContentView(R.layout.app_category_main);
		new ShowAppResultsTask(this, "Getting app details...").execute();
	}

	private void doPostAction() {
		setListAdapter(new ImageAdapter(this, R.layout.app_category_main,
				R.id.app_cat_text, R.id.next_item, listItems));

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int pos,
					long arg3) {
				//Log.d("Item Click", "item: " + pos);
				
				if (pos == 0)
					viewAllApps(v);
				else if (pos == 1)
					viewCriticalApps(v);
				else if (pos == 2)
					viewHighApps(v);
				else if (pos == 3)
					viewMediumApps(v);
				else if (pos == 4)
					viewLowApps(v);
				else if (pos == 5)
					viewTrustedApps(v);

			}
		});
	}

	private void doMainAction() {
		allAppsList = new ArrayList<AppDetailBean>();
		criticalAppsList = new ArrayList<AppDetailBean>();
		highAppsList = new ArrayList<AppDetailBean>();
		mediumAppsList = new ArrayList<AppDetailBean>();
		lowAppsList = new ArrayList<AppDetailBean>();
		trustedApps = new ArrayList<AppDetailBean>();
		
		ArrayList<AppDetailBean> appDetailList;

		dbHelper = new DatabaseHelper(getApplicationContext());

		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			// Log.d("Exception", e.getMessage());
		}
		dbHelper.openDataBase();
				
		appDetailList = AppResultsHelper.calculateAppsList(getPackageManager(), dbHelper);

		AppResultsHelper.calculateAppResults(appDetailList, allAppsList,
				criticalAppsList, highAppsList, mediumAppsList, lowAppsList, trustedApps, dbHelper);

		String[] listItemsTemp = {
				allAppsList.size() + " Total Apps Analyzed",
				criticalAppsList.size() + " Critical Risk Apps",
				highAppsList.size() + " High Risk Apps",
				mediumAppsList.size() + " Medium Risk Apps",
				lowAppsList.size() + " Low Risk Apps",
				trustedApps.size() + " Apps Marked Trusted"
				};
		
		listItems = listItemsTemp;
	}

	public void viewAllApps(View v) {
		//Log.d("viewAllApps", "View all apps");
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", allAppsList);
		myIntent.putExtra("type", "all");
		AppResultsActivity.this.startActivity(myIntent);
	}

	public void viewCriticalApps(View v) {
		//Log.d("viewCriticalApps", "View critical apps");
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", criticalAppsList);
		myIntent.putExtra("type", "Critical");

		AppResultsActivity.this.startActivity(myIntent);
	}

	public void viewHighApps(View v) {
		//Log.d("viewHighApps", "View high apps");
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", highAppsList);
		myIntent.putExtra("type", "High");

		AppResultsActivity.this.startActivity(myIntent);
	}

	public void viewMediumApps(View v) {
		//Log.d("viewMediumApps", "View medium apps");
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", mediumAppsList);
		myIntent.putExtra("type", "Medium");

		AppResultsActivity.this.startActivity(myIntent);
	}

	public void viewLowApps(View v) {
		//Log.d("viewLowApps", "View low apps");
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", lowAppsList);
		myIntent.putExtra("type", "Low");

		AppResultsActivity.this.startActivity(myIntent);
	}
	
	public void viewTrustedApps(View v){
		Intent myIntent = new Intent(AppResultsActivity.this,
				AppDetailAction.class);
		myIntent.putParcelableArrayListExtra("apps", trustedApps);
		myIntent.putExtra("type", "trusted");
		AppResultsActivity.this.startActivity(myIntent);
	}

	public void onListItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
		//Log.d("app cat click", "pos: " + pos);

		if (pos == 0)
			viewAllApps(v);
		else if (pos == 1)
			viewCriticalApps(v);
		else if (pos == 2)
			viewHighApps(v);
		else if (pos == 3)
			viewMediumApps(v);
		else if (pos == 4)
			viewLowApps(v);
		else if (pos == 5)
			viewTrustedApps(v);
	}
	
	private class ShowAppResultsTask extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog dialog;
		private AppResultsActivity activity;
		private String message;

		public ShowAppResultsTask(AppResultsActivity activity, String message) {
			this.activity = activity;
			dialog = new ProgressDialog(this.activity);
			this.message = message;
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage(message);
			this.dialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			activity.doMainAction();
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0) {
				if (dialog.isShowing())
					dialog.dismiss();
				activity.doPostAction();
			}
		}

	}
	
	
	

}
