package com.device.security.analytics.androidsecurityanalytics.tasks;

import com.device.security.analytics.androidsecurityanalytics.FrontPageActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class ReportExportTask extends AsyncTask<Void, Void, Integer> {
	
	private ProgressDialog dialog;
	private FrontPageActivity activity;
	private String message;
	
	public ReportExportTask(FrontPageActivity activity, String message){
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
		activity.generateReportFile();
		return 0;
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (result == 0) {
			if(dialog.isShowing())
				dialog.dismiss();
			activity.showReportFileIntent();
		}
	}
}
