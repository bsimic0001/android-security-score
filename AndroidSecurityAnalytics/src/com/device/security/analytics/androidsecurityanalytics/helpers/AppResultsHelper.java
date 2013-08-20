package com.device.security.analytics.androidsecurityanalytics.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalytics.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalytics.beans.AppPermissionBean;
import com.device.security.analytics.androidsecurityanalytics.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.LockPatternUtils;
import com.device.security.analytics.androidsecurityanalytics.utils.PermissionTracker;

public class AppResultsHelper {

	public static void calculateAppResults(ArrayList<AppDetailBean> appDetails,
			ArrayList<AppDetailBean> allAppsList,
			ArrayList<AppDetailBean> criticalAppsList,
			ArrayList<AppDetailBean> highAppsList,
			ArrayList<AppDetailBean> mediumAppsList,
			ArrayList<AppDetailBean> lowAppsList,
			ArrayList<AppDetailBean> trustedAppsList,
			DatabaseHelper dbHelper) {

		for (Iterator iterator = appDetails.iterator(); iterator.hasNext();) {
			AppDetailBean appDetailBean = (AppDetailBean) iterator.next();

			allAppsList.add(appDetailBean);
			
			if(dbHelper.isTrustedApp(appDetailBean.getPackageName())){
				trustedAppsList.add(appDetailBean);
				continue;
			}
			
			int classification = AnalyticsUtils.getAppClassification(
					appDetailBean.getPermissionBean().getTop10Permissions(),
					appDetailBean.getPermissionBean().getTop20Permissions(),
					appDetailBean.getPermissionBean().getTop30Permissions());


			if (classification == AnalyticsUtils.CRITICAL) {
				criticalAppsList.add(appDetailBean);
			}
			if (classification == AnalyticsUtils.HIGH) {
				highAppsList.add(appDetailBean);
			}
			if (classification == AnalyticsUtils.MEDIUM) {
				mediumAppsList.add(appDetailBean);
			}
			if (classification == AnalyticsUtils.LOW) {
				lowAppsList.add(appDetailBean);
			}

		}
	}

	public static ArrayList<AppDetailBean> calculateAppsList(
			PackageManager packageManager, DatabaseHelper dbHelper) {
		ArrayList<AppDetailBean> appDetails = new ArrayList<AppDetailBean>();

		// final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		// final List pkgAppsList = packageManager.queryIntentActivities(
		// mainIntent, 0);

		final List<PackageInfo> packageList = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		List<String> appsList = new ArrayList<String>();

		for (Object obj : packageList) {

			// ResolveInfo resolveInfo = (ResolveInfo) obj;
			PackageInfo packageInfo = (PackageInfo) obj;

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1
					&& !packageInfo.applicationInfo.packageName
							.equals("com.example.android.apis")
					&& !AnalyticsUtils.isThisPackage(packageInfo.packageName)
					//&& !dbHelper.isTrustedApp(packageInfo.packageName)
					&& packageInfo.requestedPermissions != null
					&& !appsList
							.contains(packageInfo.applicationInfo.packageName)) {

				String[] requestedPermissions = packageInfo.requestedPermissions;

				PermissionTracker permissionTracker = new PermissionTracker();
				AppPermissionBean appPermissionBean = new AppPermissionBean();
				AppDetailBean appDetailBean = new AppDetailBean();

				appPermissionBean.setPermissions(requestedPermissions);
				permissionTracker.handleAppPermissions(requestedPermissions);

				appPermissionBean.setTop10Permissions(permissionTracker
						.getTop10DeviceAmount());
				appPermissionBean.setTop20Permissions(permissionTracker
						.getTop20DeviceAmount());
				appPermissionBean.setTop30Permissions(permissionTracker
						.getTop30DeviceAmount());

				appPermissionBean
						.setNumberOfPermissions(requestedPermissions.length);

				appDetailBean.setPermissionBean(appPermissionBean);
				appDetailBean.setAppName(packageInfo.applicationInfo.loadLabel(
						packageManager).toString());
				appDetailBean.setPackageName(packageInfo.packageName);

				appDetails.add(appDetailBean);
				appsList.add(packageInfo.applicationInfo.packageName);
			}
		}

		return appDetails;
	}

	public static int calculateRisk(PackageManager packageManager,
			LockPatternUtils lockUtils, DatabaseHelper dbHelper) {
		int result = 0;

		final List<PackageInfo> packageList = packageManager
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		List<String> appsList = new ArrayList<String>();

		PermissionTracker permissionTracker = new PermissionTracker();
		AppAgeHelper appAgeHelper = new AppAgeHelper();

		// This value keeps track of the number of third party apps installed
		// on the device.
		int numberOfThirdPartyApps = 0;

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
				// Log.d("APP NAME", packageInfo.applicationInfo.packageName);
				// Get permission percentages and permission data
				permissionTracker.handleAppPermissions(requestedPermissions);

				// Update value in the app age helper
				appAgeHelper.handleAppDate(packageManager,
						packageInfo.packageName);

				// If the installer package name is empty, it is assumed that
				// the app is a
				// third party app. Apps installed from the android play store
				// will not return
				// a null installer package name.
				if (AnalyticsUtils.isAppThirdParty(packageManager,
						packageInfo.packageName) && !dbHelper.isTrustedApp(packageInfo.packageName)) {
					numberOfThirdPartyApps++;
					// Log.d("Third Party", packageInfo.packageName + " : " +
					// packageManager.getInstallerPackageName(packageInfo.packageName));
				}

				appsList.add(packageInfo.applicationInfo.packageName);
			}
		}

		// android.util.Log.d("Perm", permissionTracker.toString());

		// Log.d("Age", appAgeHelper.toString());

		/*
		 * Log.d("Points for Permissions", "Points: " +
		 * AnalyticsUtils.getScoreForAllPerms(
		 * permissionTracker.getPercentTop10(),
		 * permissionTracker.getPercentTop20(),
		 * permissionTracker.getPercentTop30()));
		 * 
		 * int pointsForPermissions = AnalyticsUtils.getScoreForAllPerms(
		 * permissionTracker.getPercentTop10(),
		 * permissionTracker.getPercentTop20(),
		 * permissionTracker.getPercentTop30());
		 */
		// ---------------- NEW ----------------------------

		ArrayList<AppDetailBean> allAppsList = new ArrayList<AppDetailBean>();
		ArrayList<AppDetailBean> criticalAppsList = new ArrayList<AppDetailBean>();
		ArrayList<AppDetailBean> highAppsList = new ArrayList<AppDetailBean>();
		ArrayList<AppDetailBean> mediumAppsList = new ArrayList<AppDetailBean>();
		ArrayList<AppDetailBean> lowAppsList = new ArrayList<AppDetailBean>();
		ArrayList<AppDetailBean> trustedAppsList = new ArrayList<AppDetailBean>();

		ArrayList<AppDetailBean> appDetailList;
		appDetailList = AppResultsHelper.calculateAppsList(packageManager, dbHelper);
		AppResultsHelper.calculateAppResults(appDetailList, allAppsList,
				criticalAppsList, highAppsList, mediumAppsList, lowAppsList, trustedAppsList, dbHelper);

		float percentCritical = (criticalAppsList.size() * 100.0f)
				/ allAppsList.size();
		float percentHigh = (highAppsList.size() * 100.0f) / allAppsList.size();
		float percentMedium = (mediumAppsList.size() * 100.0f)
				/ allAppsList.size();
		float percentLow = (lowAppsList.size() * 100.0f) / allAppsList.size();

		int pointsForPermissions = AnalyticsUtils
				.getScoreForAllClassifications(percentCritical, percentHigh,
						percentMedium, percentLow);

		// Log.d("Pts for perms", "Pts for perms 2 - " + pointsForPermissions);

		// ----------------- NEW END ---------------------------

		int pointsForUnlockMethod = AnalyticsUtils
				.getScoreForUnlockSettings(lockUtils);

		// Log.d("PTS LOCK METHORD", "PTS: " + pointsForUnlockMethod);
		int pointsForThirdPartyApps = AnalyticsUtils
				.getScoreForNumThirdPartyApps(numberOfThirdPartyApps);

		// Log.d("Num Third Party Apps", "Num: " + numberOfThirdPartyApps);
		// Log.d("PTS THIRD PARTY", "PTS: " + pointsForThirdPartyApps);

		int pointsForEncryption = AnalyticsUtils
				.getScoreForEncryptionSettings(lockUtils.getEncryptionScheme());

		// Log.d("PTS ENCRYPTION", "PTS: " + pointsForEncryption);
		
		//appAgeHelper.setTotalApps(allAppsList.size());
		
		int pointsForAge = AnalyticsUtils.getAppAgeScore(
				appAgeHelper.getTwelveMonthsPercentage(),
				appAgeHelper.getSixMonthsPercentage());

		// Log.d("PTS Age", "PTS: " + pointsForAge);

		int rootedPoints = AnalyticsUtils.getScoreForRootAccess();
		// Log.d("Root Access Points", "Root Points: " + rootedPoints);

		int totalPoints = pointsForPermissions + pointsForUnlockMethod
				+ pointsForThirdPartyApps + pointsForEncryption + pointsForAge
				+ rootedPoints;
		// Log.d("Total Points", "Pts: " + totalPoints + " / 100");

		System.out.println(permissionTracker.toString());

		result = totalPoints;
		return result;
	}

}
