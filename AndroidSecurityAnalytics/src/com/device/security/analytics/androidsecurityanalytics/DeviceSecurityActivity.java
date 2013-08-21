package com.device.security.analytics.androidsecurityanalytics;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ExpandableListView;

import com.device.security.analytics.androidsecurityanalytics.adapters.DeviceCatAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.DeviceCatBean;
import com.device.security.analytics.androidsecurityanalytics.helpers.AppAgeHelper;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.LockPatternUtils;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class DeviceSecurityActivity extends Activity {

	ExpandableListView listView;
	ArrayList<DeviceCatBean> catBeanList;
	DatabaseHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_cat_layout);
		setTitle("Device Security Details");

		// doMainAction();
		// doPostAction();
		
		dbHelper = new DatabaseHelper(getApplicationContext());

		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			// Log.d("Exception", e.getMessage());
		}
		dbHelper.openDataBase();
		new ShowDeviceSecurityTask(this, "Getting device details...").execute();
	}

	private void doPostAction() {
		listView = (ExpandableListView) findViewById(R.id.device_cat_list);
		final DeviceCatAdapter catAdapter = new DeviceCatAdapter(this,
				catBeanList);
		listView.setAdapter(catAdapter);
	}

	private void doMainAction() {
		LockPatternUtils lockUtils = new LockPatternUtils(
				getApplicationContext());
		AppAgeHelper appAgeHelper = new AppAgeHelper();
		int numberOfThirdPartyApps = 0;

		final List<PackageInfo> packageList = getPackageManager()
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		List<String> appsList = new ArrayList<String>();

		for (Object obj : packageList) {

			// ResolveInfo resolveInfo = (ResolveInfo) obj;
			PackageInfo packageInfo = (PackageInfo) obj;

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1
					&& !packageInfo.applicationInfo.packageName
							.equals("com.example.android.apis")
					&& !AnalyticsUtils.isThisPackage(packageInfo.packageName)
					&& packageInfo.requestedPermissions != null
					&& !appsList
							.contains(packageInfo.applicationInfo.packageName)) {
				// Gets permissions for the current app
				String[] requestedPermissions = packageInfo.requestedPermissions;
				//Log.d("APP NAME", packageInfo.applicationInfo.packageName);
				// Get permission percentages and permission data

				// Update value in the app age helper
				appAgeHelper.handleAppDate(getPackageManager(),
						packageInfo.packageName);

				// If the installer package name is empty, it is assumed that
				// the app is a
				// third party app. Apps installed from the android play store
				// will not return
				// a null installer package name.
				if (AnalyticsUtils.isAppThirdParty(getPackageManager(),
						packageInfo.packageName) && !dbHelper.isTrustedApp(packageInfo.packageName)) {
					numberOfThirdPartyApps++;
					//Log.d("Third Party",
					//		packageInfo.packageName
					//				+ " : "
					//				+ getPackageManager()
					//						.getInstallerPackageName(
					//								packageInfo.packageName));
				}

				appsList.add(packageInfo.applicationInfo.packageName);
			}
		}

		int pointsForThirdPartyApps = AnalyticsUtils
				.getScoreForNumThirdPartyApps(numberOfThirdPartyApps);
		//Log.d("Num Third Party Apps", "Num: " + numberOfThirdPartyApps);
		//Log.d("PTS THIRD PARTY", "PTS: " + pointsForThirdPartyApps);

		int pointsForUnlockMethod = AnalyticsUtils
				.getScoreForUnlockSettings(lockUtils);
		//Log.d("PTS Unlock Method", "PTS: " + pointsForUnlockMethod);

		int pointsForEncryption = AnalyticsUtils
				.getScoreForEncryptionSettings(lockUtils.getEncryptionScheme());
		//Log.d("PTS ENCRYPTION", "PTS: " + pointsForEncryption);

		
		
		int pointsForAge = AnalyticsUtils.getAppAgeScore(
				appAgeHelper.getTwelveMonthsPercentage(),
				appAgeHelper.getSixMonthsPercentage());
		//Log.d("PTS Age", "PTS: " + pointsForAge);

		int pointsForRoot = AnalyticsUtils.getScoreForRootAccess();
		//Log.d("Pts Root", "Root Pts: " + pointsForRoot);

		catBeanList = new ArrayList<DeviceCatBean>();

		catBeanList.add(getThirdPartyBean(pointsForThirdPartyApps));
		catBeanList.add(getUnlockScoreBean(pointsForUnlockMethod));
		catBeanList.add(getAgeBean(pointsForAge));
		catBeanList.add(getEncryptionBean(pointsForEncryption));
		catBeanList.add(getRootBean(pointsForRoot));
	}

	private DeviceCatBean getRootBean(int pointsForRoot) {
		DeviceCatBean rootBean = new DeviceCatBean();

		String rootCategory = "Device Rooting";
		float rootScore = (pointsForRoot * 100.0f)
				/ AnalyticsUtils.MAX_ROOTED_POINTS;
		String rootExplanation;
		if (pointsForRoot < AnalyticsUtils.MAX_ROOTED_POINTS) {
			rootExplanation = "Whoa! Your device is rooted. Rooting a device gives you complete control of your device. "
					+ "However, it also gives potential malware root access to your device as well, which is not good. "
					+ "You should not root your phone unless it's absolutely necessary. If you do root it, you should "
					+ "make sure to undo that once you're finished doing what you need it for.";
		} else {
			rootExplanation = "Your device is not rooted! Try to keep it that way for improved security.";
		}

		rootBean.setCategory(rootCategory);
		rootBean.setExplanation(rootExplanation);
		rootBean.setScore(rootScore);
		return rootBean;
	}

	private DeviceCatBean getEncryptionBean(int pointsForEncryption) {
		DeviceCatBean encBean = new DeviceCatBean();

		String encCategory = "Device Encryption";
		float encScore = (pointsForEncryption * 100.0f)
				/ AnalyticsUtils.MAX_ENCRYPTION_SCORE;
		String encExplanation;
		if (pointsForEncryption < AnalyticsUtils.MAX_ENCRYPTION_SCORE) {
			encExplanation = "Uh oh. Looks like you're either not using the encryption feature on your device or it doesn't support it. "
					+ "You should enable device encryption on your device in the Settings > Security menu ASAP. "
					+ "This prevents your personal data stored on this device from being readable in the event it's compromised. "
					+ "If your device doesn't support encryption, you should think about upgrading! ";
		} else {
			encExplanation = "Yay! Your device has encryption enabled. "
					+ "If you didn't already know, this prevents from someone reading your "
					+ "valuable data in the event that your device is compromised. Keep it up!";
		}

		encBean.setCategory(encCategory);
		encBean.setExplanation(encExplanation);
		encBean.setScore(encScore);
		return encBean;
	}

	private DeviceCatBean getAgeBean(int pointsForAge) {
		DeviceCatBean ageBean = new DeviceCatBean();

		String ageCategory = "Installed Apps Age";
		float ageScore = (pointsForAge * 100.0f) / AnalyticsUtils.MAX_AGE_SCORE;
		String ageExplanation;
		if (pointsForAge < AnalyticsUtils.MAX_AGE_SCORE) {
			ageExplanation = "It looks like your device may contain apps that are severely out of date. "
					+ "You should make sure that apps installed on your device are updated at least once a year. "
					+ "If an app is not updated regularly there is a chance that it does not contain important "
					+ "security patches or makes use of deprecated libraries in the Android Framework. ";
		} else {
			ageExplanation = "Wow! It looks like all your apps are up to date. Keep it up!";
		}

		ageBean.setCategory(ageCategory);
		ageBean.setExplanation(ageExplanation);
		ageBean.setScore(ageScore);
		return ageBean;
	}

	private DeviceCatBean getUnlockScoreBean(int pointsForUnlockMethod) {
		DeviceCatBean lockMethod = new DeviceCatBean();

		String lockCategory = "Device Lock Method";
		float lockScore = (pointsForUnlockMethod * 100.0f)
				/ AnalyticsUtils.MAX_UNLOCK_SCORE;
		String lockExplanation;
		if (pointsForUnlockMethod < AnalyticsUtils.MAX_UNLOCK_SCORE) {
			lockExplanation = "Your device doesn't seem to be using a screen lock mechanism that is not easily predictable. "
					+ "For better device security you should use a PIN or Password that is at least 6 digits in length.";
		} else {
			lockExplanation = "Woot! Looks like you're using a good locking mechanism with a PIN or Password. "
					+ "Keep it up. For extra security, you should make sure this value is complex and not easily guessable. "
					+ "Values such as birthdays, anniversaries, pet names, etc... are not good candidates. Also, change this value every 3 months.";
		}

		lockMethod.setCategory(lockCategory);
		lockMethod.setExplanation(lockExplanation);
		lockMethod.setScore(lockScore);
		return lockMethod;
	}

	private DeviceCatBean getThirdPartyBean(int pointsForThirdPartyApps) {
		DeviceCatBean thirdParty = new DeviceCatBean();

		String thirdPartyCategory = "Third Party Apps";
		float thirdPartyScore = (pointsForThirdPartyApps * 100.0f)
				/ AnalyticsUtils.MAX_THIRD_PARTY_APP_SCORE;
		String thirdPartyExplanation;
		if (pointsForThirdPartyApps < AnalyticsUtils.MAX_THIRD_PARTY_APP_SCORE) {
			thirdPartyExplanation = "We have detected installed third party apps on your device. "
					+ "A third party app is any that is not downloaded from the official Google Play store. "
					+ "These apps are much more likely to contain Malware and should be uninstalled for better security";
		} else {
			thirdPartyExplanation = "Hooray! We have not detected any third party apps on your device. "
					+ "Keep it up by only downloading apps from the official Google Play store.";
		}

		thirdParty.setCategory(thirdPartyCategory);
		thirdParty.setExplanation(thirdPartyExplanation);
		thirdParty.setScore(thirdPartyScore);
		return thirdParty;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_security, menu);
		return true;
	}

	private class ShowDeviceSecurityTask extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog dialog;
		private DeviceSecurityActivity activity;
		private String message;

		public ShowDeviceSecurityTask(DeviceSecurityActivity activity,
				String message) {
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
