package com.device.security.analytics.androidsecurityanalytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.device.security.analytics.androidsecurityanalytics.helpers.AppResultsHelper;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;
import com.device.security.analytics.androidsecurityanalytics.helpers.FileCreationHelper;
import com.device.security.analytics.androidsecurityanalytics.tasks.ReportExportTask;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.LockPatternUtils;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class FrontPageActivity extends Activity {

	LockPatternUtils lockUtils = null;
	ShapeDrawable gradeBackground;
	int score;
	File reportFile;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
		//sendExistingStack();
		setContentView(R.layout.front_page);
		new CalculateScoreTask(this, "Calculating score...").execute();
		// doMainActivity();
	}

	private void sendExistingStack() {
		String line;
		String trace = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					FrontPageActivity.this.openFileInput("stack.trace")));
			while ((line = reader.readLine()) != null) {
				trace += line + "\n";
			}
		} catch (FileNotFoundException fnfe) {
			return;
		} catch (IOException ioe) {
			return;
		}

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		String subject = "Error report";
		String body = "Mail this to bsimic0001@gmail.com: " + "\n\n" + trace
				+ "\n\n";

		sendIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "bsimic0001@gmail.com" });
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		sendIntent.setType("message/rfc822");

		FrontPageActivity.this.startActivity(Intent.createChooser(sendIntent,
				"Title:"));

		FrontPageActivity.this.deleteFile("stack.trace");
	}

	public void doMainActivity(DatabaseHelper dbHelper) {
		lockUtils = new LockPatternUtils(getApplicationContext());
		score = AppResultsHelper.calculateRisk(getPackageManager(), lockUtils, dbHelper);
	}

	public void updateMainUI() {
		TextView resultView = (TextView) findViewById(R.id.analytics_result);
		TextView gradeView = (TextView) findViewById(R.id.analytics_grade);

		resultView.setText("Your security score is " + score + " / 100");
		setGradeBackground(score);

		if (android.os.Build.VERSION.SDK_INT >= 16) {

			gradeView.setBackground(gradeBackground);
		} else
			gradeView.setBackgroundColor(Color.RED);

		gradeView.setTextColor(Color.BLACK);
		gradeView.setText(AnalyticsUtils.getLetterGrade(score));
	}

	private void setGradeBackground(int score) {
		// The corners are ordered top-left, top-right, bottom-right,
		// bottom-left. For each corner, the array contains 2 values, [X_radius,
		// Y_radius]
		float[] radii = new float[8];
		radii[0] = 10;
		radii[1] = 10;
		radii[2] = 10;
		radii[3] = 10;
		radii[4] = 10;
		radii[5] = 10;
		radii[6] = 10;
		radii[7] = 10;

		gradeBackground = new ShapeDrawable();
		gradeBackground.setShape(new RoundRectShape(radii, null, null));
		// int color = ((Application) activity.getApplication()).getColor();

		String letterGrade = AnalyticsUtils.getLetterGrade(score);

		if (letterGrade.equals("F"))
			gradeBackground.getPaint().setColor(Color.RED);
		else if (letterGrade.equals("D"))
			gradeBackground.getPaint().setColor(Color.parseColor("#CC3232"));
		else if (letterGrade.equals("C"))
			gradeBackground.getPaint().setColor(Color.parseColor("#FFFF00"));
		else if (letterGrade.equals("B"))
			gradeBackground.getPaint().setColor(Color.parseColor("#9ACD32"));
		else if (letterGrade.equals("A"))
			gradeBackground.getPaint().setColor(Color.parseColor("#00FF00"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemRefresh:
			//Log.d("Refresh", "refresh");
			new CalculateScoreTask(this, "Calculating score...").execute();
			// recalculateScore(null);
			return true;
		default:
			return true;
		}
	}

	public void recalculateScore(View view) {
		//Log.d("recalculateScore", "in recalculate score method");

		DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			// Log.d("Exception", e.getMessage());
		}
		dbHelper.openDataBase();
		
		int score = AppResultsHelper.calculateRisk(getPackageManager(),
				lockUtils, dbHelper);

		TextView gradeView = (TextView) findViewById(R.id.analytics_grade);
		setGradeBackground(score);
		gradeView.setBackground(gradeBackground);
		gradeView.setText(AnalyticsUtils.getLetterGrade(score));
	}

	public void goToAppSecurity(View view) {
		//Log.d("goToAppSecurity", "in app security method");

		Intent myIntent = new Intent(FrontPageActivity.this,
				AppResultsActivity.class);
		FrontPageActivity.this.startActivity(myIntent);
	}

	public void goToDeviceSecurity(View view) {
		//Log.d("goToDeviceSecurity", "in device security method");
		Intent deviceSecurityIntent = new Intent(FrontPageActivity.this,
				DeviceSecurityActivity.class);
		FrontPageActivity.this.startActivity(deviceSecurityIntent);
	}

	public void exportResults(View view) {
		//Log.d("exportResults", "exportResults method");

		new ReportExportTask(this, "Generating report...").execute();
	}

	public void generateReportFile() {
		LockPatternUtils lockUtils = new LockPatternUtils(
				getApplicationContext());
		reportFile = FileCreationHelper.createFile(getPackageManager(),
				lockUtils, getApplicationContext());
	}

	public void showReportFileIntent() {
		if (reportFile != null) {

			Uri uri = Uri.fromFile(reportFile);

			Intent i = new Intent(Intent.ACTION_SEND);
			i.putExtra(Intent.EXTRA_SUBJECT,
					"Android Security Analysis Results");
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("text/plain");
			startActivity(Intent.createChooser(i, "Send mail"));
		} else {
			//Log.d("Error exporting", "There was an error exporting");
		}
	}

	public void goToFaqInfo(View view) {
		//Log.d("goToFaqInfo", "go to fax info page method");

		Intent faqIntent = new Intent(FrontPageActivity.this, FaqActivity.class);
		FrontPageActivity.this.startActivity(faqIntent);
	}

	private class CalculateScoreTask extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog dialog;
		private FrontPageActivity activity;
		private String message;
		DatabaseHelper dbHelper;

		public CalculateScoreTask(FrontPageActivity activity, String message) {
			this.activity = activity;
			dialog = new ProgressDialog(this.activity);
			this.message = message;
			
			dbHelper = new DatabaseHelper(getApplicationContext());
			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
				// Log.d("Exception", e.getMessage());
			}
			dbHelper.openDataBase();
			
		}

		@Override
		protected void onPreExecute() {
			this.dialog.setMessage(message);
			this.dialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			activity.doMainActivity(dbHelper);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0) {
				if (dialog.isShowing())
					dialog.dismiss();
				activity.updateMainUI();
			}
		}

	}

}
