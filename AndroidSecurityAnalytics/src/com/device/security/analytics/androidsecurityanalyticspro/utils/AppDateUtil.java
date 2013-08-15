package com.device.security.analytics.androidsecurityanalyticspro.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Months;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppDateUtil {

	/**
	 * Get number of months between two apps.
	 * 
	 * @param packageManager
	 * @param packageName
	 * @return
	 */
	public static int getMonthsSinceUpdate(PackageManager packageManager,
			String packageName) {
		Months numberOfMonths = null;

		DateTime now = new DateTime();
		DateTime then = new DateTime(apkUpdateTime(packageManager, packageName));
		//DateTime then = new DateTime(
		//		getInstallTime(packageManager, packageName));

		numberOfMonths = Months.monthsBetween(then, now);

		return numberOfMonths.getMonths();
	}

	// return install time from package manager, or apk file modification time,
	// or null if not found
	public static Date getInstallTime(PackageManager packageManager, String packageName) {
		return firstNonNull(
				installTimeFromPackageManager(packageManager, packageName),
				apkUpdateTime(packageManager, packageName));
	}

	private static Date apkUpdateTime(PackageManager packageManager, String packageName) {
		try {
			ApplicationInfo info = packageManager.getApplicationInfo(
					packageName, 0);
			File apkFile = new File(info.sourceDir);
			return apkFile.exists() ? new Date(apkFile.lastModified()) : null;
		} catch (NameNotFoundException e) {
			return null; // package not found
		}
	}

	private static Date installTimeFromPackageManager(PackageManager packageManager,
			String packageName) {
		// API level 9 and above have the "firstInstallTime" field.
		// Check for it with reflection and return if present.
		try {
			PackageInfo info = packageManager.getPackageInfo(packageName, 0);
			Field field = PackageInfo.class.getField("firstInstallTime");
			long timestamp = field.getLong(info);
			return new Date(timestamp);
		} catch (NameNotFoundException e) {
			return null; // package not found
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		}
		// field wasn't found
		return null;
	}

	private static Date firstNonNull(Date... dates) {
		for (Date date : dates)
			if (date != null)
				return date;
		return null;
	}

}
