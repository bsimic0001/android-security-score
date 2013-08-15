package com.device.security.analytics.androidsecurityanalytics.utils;

import java.util.ArrayList;
import java.util.Iterator;

import com.device.security.analytics.androidsecurityanalytics.beans.PermBean;
import com.stericson.RootTools.RootTools;

import android.content.pm.PackageManager;
import android.util.Log;

public class AnalyticsUtils {

	public static String INTERNET = "INTERNET";
	public static String READ_PHONE_STATE = "READ_PHONE_STATE";
	public static String SEND_SMS = "SEND_SMS";
	public static String WRITE_EXTERNAL_STORAGE = "WRITE_EXTERNAL_STORAGE";
	public static String READ_EXTERNAL_STORAGE = "READ_EXTERNAL_STORAGE";
	public static String VIBRATE = "VIBRATE";
	public static String RECEIVE_SMS = "RECEIVE_SMS";
	public static String READ_SMS = "READ_SMS";
	public static String RECEIVE_BOOT_COMPLETED = "RECEIVE_BOOT_COMPLETED";
	public static String ACCESS_NETWORK_STATE = "ACCESS_NETWORK_STATE";
	public static String ACCESS_WIFI_STATE = "ACCESS_WIFI_STATE";
	public static String ACCESS_COARSE_LOCATION = "ACCESS_COARSE_LOCATION";
	public static String READ_CONTACTS = "READ_CONTACTS";
	public static String ACCESS_FINE_LOCATION = "ACCESS_FINE_LOCATION";
	public static String WRITE_SMS = "WRITE_SMS";
	public static String CALL_PHONE = "CALL_PHONE";
	public static String WAKE_LOCK = "WAKE_LOCK";
	public static String CHANGE_CONFIGURATION = "CHANGE_CONFIGURATION";
	public static String READ_LOGS = "READ_LOGS";
	public static String WRITE_CONTACTS = "WRITE_CONTACTS";
	public static String RECEIVE_WAP_PUSH = "RECEIVE_WAP_PUSH";
	public static String WRITE_APN_SETTINGS = "WRITE_APN_SETTINGS";
	public static String SYSTEM_ALERT_WINDOW = "SYSTEM_ALERT_WINDOW";
	public static String CAMERA = "CAMERA";
	public static String GET_TASKS = "GET_TASKS";
	public static String WRITE_SETTINGS = "WRITE_SETTINGS";
	public static String MOUNT_UNMOUNT_FILESYSTEMS = "MOUNT_UNMOUNT_FILESYSTEMS";
	public static String RECEIVE_MMS = "RECEIVE_MMS";
	public static String BLUETOOTH = "BLUETOOTH";
	public static String MODIFY_AUDIO_SETTINGS = "MODIFY_AUDIO_SETTINGS";
	public static String CHANGE_WIFI_STATE = "CHANGE_WIFI_STATE";
	public static String WRITE_CALENDAR = "WRITE_CALENDAR";
	public static String CHANGE_NETWORK_STATE = "CHANGE_NETWORK_STATE";
	public static String READ_CALENDAR = "READ_CALENDAR";
	public static String MODIFY_PHONE_STATE = "MODIFY_PHONE_STATE";
	public static String READ_CALL_LOG = "READ_CALL_LOG";
	public static String WRITE_CALL_LOG = "WRITE_CALL_LOG";
	public static String INSTALL_PACKAGES = "INSTALL_PACKAGES";
	public static String RESTART_PACKAGES = "RESTART_PACKAGES";
	
	
	/*
	
	READ_PHONE_STATE	1385	94.93%
	INTERNET	1380	94.59%
	READ_EXTERNAL_STORAGE	1299	89.03%
	WRITE_EXTERNAL_STORAGE	1299	89.03%
	ACCESS_NETWORK_STATE	1137	77.93%
	ACCESS_WIFI_STATE	817	62.46%
	READ_SMS	797	54.63%
	RECEIVE_BOOT_COMPLETED	708	48.53%
	WRITE_SMS	670	45.92%
	SEND_SMS	655	44.89%
	RECEIVE_SMS	520	35.64%
	VIBRATE	496	34.00%
	ACCESS_COARSE_LOCATION	491	33.65%
	READ_CALL_LOG	467	32.01%
	READ_CONTACTS	467	32.01%
	WAKE_LOCK	441	30.27%
	ACCESS_FINE_LOCATION	430	29.51%
	CALL_PHONE	426	29.24%
	CHANGE_WIFI_STATE	392	26.91%
	WRITE_CALL_LOG	376	25.81%
	WRITE_CONTACTS	376	25.81%
	INSTALL_PACKAGES	358	24.57%
	WRITE_APN_SETTINGS	357	24.50%
	RESTART_PACKAGES	341	23.40% 
	*/
	
	public static String[] top30Permissions = {
		READ_PHONE_STATE, 			// 1
		INTERNET, 					// 2
		READ_EXTERNAL_STORAGE, 		// 3
		WRITE_EXTERNAL_STORAGE, 	// 4
		ACCESS_NETWORK_STATE, 		// 5
		ACCESS_WIFI_STATE, 			// 6
		READ_SMS, 					// 7
		RECEIVE_BOOT_COMPLETED, 	// 8
		WRITE_SMS, 					// 9
		RECEIVE_SMS, 				// 10
		SEND_SMS, 					// 11
		ACCESS_COARSE_LOCATION, 	// 12
		READ_CALL_LOG, 				// 13
		READ_CONTACTS, 				// 14
		WAKE_LOCK, 					// 15
		ACCESS_FINE_LOCATION, 		// 16
		CALL_PHONE, 				// 17
		CHANGE_WIFI_STATE, 			// 18
		WRITE_CALL_LOG, 			// 19
		WRITE_CONTACTS, 			// 20
		INSTALL_PACKAGES, 			// 21
		WRITE_APN_SETTINGS,			// 22
		RESTART_PACKAGES, 			// 23
		CHANGE_CONFIGURATION,		// 24
		READ_LOGS,					// 25
		RECEIVE_WAP_PUSH,			// 26
		SYSTEM_ALERT_WINDOW,		// 27
		CAMERA,						// 28
		GET_TASKS,					// 29
		WRITE_SETTINGS};			// 30
	
	/*
	public static String[] top30Permissions = { 
	
	INTERNET, 
	READ_PHONE_STATE,
	SEND_SMS, 
	WRITE_EXTERNAL_STORAGE, 
	RECEIVE_SMS, 
	READ_SMS,
	ACCESS_COARSE_LOCATION, 
	READ_CONTACTS, 
	ACCESS_FINE_LOCATION,
	WRITE_SMS, 
	CALL_PHONE, 
	WAKE_LOCK, 
	CHANGE_CONFIGURATION, 
	READ_LOGS,
	WRITE_CONTACTS, 
	RECEIVE_WAP_PUSH, 
	WRITE_APN_SETTINGS,
	SYSTEM_ALERT_WINDOW, 
	CAMERA, 
	GET_TASKS, 
	WRITE_SETTINGS,
	MOUNT_UNMOUNT_FILESYSTEMS, 
	RECEIVE_MMS, 
	BLUETOOTH,
	MODIFY_AUDIO_SETTINGS, 
	CHANGE_WIFI_STATE, 
	WRITE_CALENDAR,
	CHANGE_NETWORK_STATE, 
	READ_CALENDAR, 
	MODIFY_PHONE_STATE };
	
	*/
	public static String VERY_HIGH_RISK = "Very High";
	public static String HIGH_RISK = "High";
	public static String MEDIUM_RISK = "Medium";
	public static String LOW_RISK = "Low";
	
	private static String TOP10_PERM_TYPE = "TOP_10";
	private static String TOP20_PERM_TYPE = "TOP_20";
	private static String TOP30_PERM_TYPE = "TOP_30";
	
	private static int MAX_PERM_SCORE = 30;
	public static int MAX_ROOTED_POINTS = 15;
	
	private static int TOP30_MAX = 5;
	private static int TOP30_VERY_HIGH = 5;
	private static int TOP30_HIGH = 4;
	private static int TOP30_MED = 2;
	private static int TOP30_LOW = 1;
	
	private static int TOP20_MAX = 10;
	private static int TOP20_VERY_HIGH = 10;
	private static int TOP20_HIGH = 8;
	private static int TOP20_MED = 5;
	private static int TOP20_LOW = 2;
	
	private static int TOP10_MAX = 15;
	private static int TOP10_VERY_HIGH = 15;
	private static int TOP10_HIGH = 11;
	private static int TOP10_MED = 8;
	private static int TOP10_LOW = 4;
	
	public static int CRITICAL = 3;
	public static int HIGH = 2;
	public static int MEDIUM = 1;
	public static int LOW = 0;
	
	public static int getScoreForPerm(float percentage, String PERM_TYPE){
		int score = 0;
		int veryHigh = 0;
		int high = 0;
		int med = 0;
		int low = 0;
		
		if(PERM_TYPE.equals(TOP30_PERM_TYPE)){
			score = TOP30_MAX;
			veryHigh = TOP30_VERY_HIGH;
			high = TOP30_HIGH;
			med = TOP30_MED;
			low = TOP30_LOW;
		}
		else if(PERM_TYPE.equals(TOP20_PERM_TYPE)){
			score = TOP20_MAX;
			veryHigh = TOP20_VERY_HIGH;
			high = TOP20_HIGH;
			med = TOP20_MED;
			low = TOP20_LOW;
		}
		else{
			score = TOP10_MAX;
			veryHigh = TOP10_VERY_HIGH;
			high = TOP10_HIGH;
			med = TOP10_MED;
			low = TOP10_LOW;
		}
		
		if(percentage > 50)
			return score - veryHigh;
		if(percentage > 25)
			return score - high;
		if(percentage > 10)
			return score - med;
		if(percentage > 0)
			return score - low;
		
		return score;
	}
	
	public static int getScoreForAllPerms(float top10Percentage, float top20Percentage, float top30Percentage){
		int overallScore = 0;
		overallScore = getScoreForPerm(top30Percentage, TOP30_PERM_TYPE);
		System.out.println("Overall 1: " + overallScore);
		overallScore = overallScore + getScoreForPerm(top20Percentage, TOP20_PERM_TYPE);
		System.out.println("Overall 2: " + overallScore);
		overallScore = overallScore + getScoreForPerm(top10Percentage, TOP10_PERM_TYPE);
		System.out.println("Overall 3: " + overallScore);
		return overallScore;
	}
	
	public static int getScoreForAllClassifications(float critPercent, float highPercent, float medPercent, float lowPercent){
		int score = 0;

		score = score + getScoreForAppClassification(critPercent, CRITICAL);
		score = score + getScoreForAppClassification(highPercent, HIGH);
		score = score + getScoreForAppClassification(medPercent, MEDIUM);
		score = score + getScoreForAppClassification(lowPercent, LOW);
		
		return score;
	}
	
	public static int getScoreForAppClassification(float percentage, int type){
		int score = 0;
		//total worth is 12
		if(type == CRITICAL){
			if(percentage >= 40)
				score = 12 - 12;
			else if(percentage >= 20)
				score = 12 - 8;
			else if(percentage > 0)
				score = 12 - 4;
			else if(percentage == 0)
				score = 12;
		}
		//total worth 9
		if(type == HIGH){
			if(percentage >= 40)
				score = 9 - 9;
			else if(percentage >= 20)
				score = 9 - 6;
			else if(percentage > 0)
				score = 9 - 3;
			else if(percentage == 0)
				score = 9;
		}
		//total worth 6
		if(type == MEDIUM){
			if(percentage >= 40)
				score = 6 - 6;
			else if(percentage >= 20)
				score = 6 - 4;
			else if(percentage > 0)
				score = 6 - 2;
			else if(percentage == 0)
				score = 6;
		}
		//total worth 3
		if(type == LOW){
			if(percentage >= 40)
				score = 3 - 3;
			else if(percentage >= 20)
				score = 3 - 2;
			else if(percentage > 0)
				score = 3 - 1;
			else if(percentage == 0)
				score = 4;
		}
		
		Log.d("NEW PERM SCORE", type + " - " + score);
		
		return score;
	}
	
	private static int MAX_DEVICE_FACTOR_SCORE = 40;

	public static int MAX_THIRD_PARTY_APP_SCORE = 15;
	private static int MEDIUM_THIRD_PARTY_APP_SCORE = 7;

	public static int MAX_UNLOCK_SCORE = 15;
	private static int NO_UNLOCK_POINTS = 15;
	private static int PATTERN_UNLOCK_POINTS = 10;
	private static int WEAK_PASS_UNLOCK_POINTS = 8;
	
	public static String UNLOCK_SETTING_NONE = "NONE";
	public static String UNLOCK_SETTING_PASS = "PASSWORD";
	public static String UNLOCK_SETTING_PATTERN = "PATTERN";
	
	public static int MAX_ENCRYPTION_SCORE = 10;
	
	public static int getDeviceFactorScore(int numberThirdPartyApps, 
			String unlockSettingType, 
			int unlockSettingLength, 
			boolean encryptionUsed){
		
		int score = MAX_DEVICE_FACTOR_SCORE;
		
		score = score - getScoreForNumThirdPartyApps(numberThirdPartyApps);
		score = score - getScoreForUnlockSettings(unlockSettingType, unlockSettingLength);
		score = score - getScoreForEncryptionSettings(encryptionUsed);
		
		return score;
	}
	
	public static int getScoreForNumThirdPartyApps(int numberThirdPartyApps){
		int score = MAX_THIRD_PARTY_APP_SCORE;
		
		if(numberThirdPartyApps > 5)
			return score - MAX_THIRD_PARTY_APP_SCORE;
		if(numberThirdPartyApps > 0)
			return score - MEDIUM_THIRD_PARTY_APP_SCORE;
		
		return score;
	}
	
	
	public static int getScoreForUnlockSettings(LockPatternUtils lockUtils){
		int result = 0;
		
		if(lockUtils.isLockPasswordEnabled()){
			result = AnalyticsUtils.getScoreForUnlockSettings(AnalyticsUtils.UNLOCK_SETTING_PASS, 
							lockUtils.getRequestedMinimumPasswordLength());
		}
		else if(lockUtils.isLockPatternEnabled()){
			result = AnalyticsUtils.getScoreForUnlockSettings(AnalyticsUtils.UNLOCK_SETTING_PATTERN, 0);
		}
		else{
			result = AnalyticsUtils.getScoreForUnlockSettings(AnalyticsUtils.UNLOCK_SETTING_NONE, 0);
		}
		
		return result;
	}
	
	public static int getScoreForUnlockSettings(String unlockSettingType, int length){
		int score = MAX_UNLOCK_SCORE;
		
		if(unlockSettingType.equals(UNLOCK_SETTING_NONE))
			return score - NO_UNLOCK_POINTS;
		if(unlockSettingType.equals(UNLOCK_SETTING_PATTERN))
			return score - PATTERN_UNLOCK_POINTS;
		if(unlockSettingType.equals(UNLOCK_SETTING_PASS))
			return score;
		
		return score;
	
	}
		
	public static int getScoreForEncryptionSettings(boolean encryptionUsed){
		int score = MAX_ENCRYPTION_SCORE;
		if(encryptionUsed)
			return score;
		else
			return score - MAX_ENCRYPTION_SCORE;
	}

	public static int MAX_AGE_SCORE = 15;
	private static int AGE_HIGH = 12;
	private static int AGE_MED_HIGH = 9;
	private static int AGE_MED = 6;
	private static int AGE_LOW = 3;
	
	public static int getAppAgeScore(float percentOneYearOld, float percentSixMonthsOld){
		int score = MAX_AGE_SCORE;
		
		if(percentOneYearOld > 30)
			return score - MAX_AGE_SCORE;
		else if(percentOneYearOld > 20)
			return score - AGE_HIGH;
		if(percentSixMonthsOld > 30)
			return score - AGE_MED_HIGH;
		if(percentSixMonthsOld > 20 || percentOneYearOld > 10)
			return score - AGE_MED;
		if(percentSixMonthsOld > 10)
			return score - AGE_LOW;
		return score;
	}
	
	private static int CRIT_TOP10_AMOUNT = 6; //3
	private static int CRIT_TOP20_AMOUNT = 12; //7
	private static int CRIT_TOP30_AMOUNT = 20; //10

	private static int HIGH_TOP10_AMOUNT = 3; //1
	private static int HIGH_TOP20_AMOUNT = 7; //3
	private static int HIGH_TOP30_AMOUNT = 10; //5

	private static int MED_TOP10_AMOUNT = 1; //0
	private static int MED_TOP20_AMOUNT = 4; //0
	private static int MED_TOP30_AMOUNT = 7; //3
	
	private static int LOW_TOP10_AMOUNT = 0; //0
	private static int LOW_TOP20_AMOUNT = 2; //0
	private static int LOW_TOP30_AMOUNT = 5; //0	
	
	public static int getAppClassification(int top10, int top20, int top30){
		int classification = 0;
		
		if(top10 >= CRIT_TOP10_AMOUNT || top20 >= CRIT_TOP20_AMOUNT || top30 >= CRIT_TOP30_AMOUNT)
			classification = CRITICAL;
		else if(top10 >= HIGH_TOP10_AMOUNT || top20 >= HIGH_TOP20_AMOUNT || top30 >= HIGH_TOP30_AMOUNT)
			classification = HIGH;
		else if(top10 >= MED_TOP10_AMOUNT || top20 >= MED_TOP20_AMOUNT || top30 >= MED_TOP30_AMOUNT)
			classification = MEDIUM;
		else if(top20 >= LOW_TOP20_AMOUNT || top30 >= LOW_TOP30_AMOUNT)
			classification = LOW;
		return classification;
	}
	
	public static String getLetterGrade(int score){
		if(score >= 90)
			return "A";
		else if(score >= 80)
			return "B";
		else if(score >= 70)
			return "C";
		else if(score >= 60)
			return "D";
		else
			return "F";
	}
	
	public static int getScoreForRootAccess(){
		if (RootTools.isRootAvailable()) {
		    return MAX_ROOTED_POINTS - MAX_ROOTED_POINTS;
		} else {
		    return MAX_ROOTED_POINTS;
		}
	}
	
	public static PermBean getMatchingPermBean(ArrayList<PermBean> beans,
			String permission) {
		for (Iterator iterator = beans.iterator(); iterator.hasNext();) {
			PermBean permBean = (PermBean) iterator.next();
			if (permission.contains(permBean.getName())) {
				return permBean;
			}
		}
		return null;
	}
	
	public static boolean isAppThirdParty(PackageManager packageManager, String packageName){
		boolean result = false;
		
		String installerPackage = packageManager.getInstallerPackageName(packageName);
		
		if(installerPackage == null || (!installerPackage.equals("com.android.vending") && !installerPackage.equals("de.androidpit.app"))){
			result = true;
		}
		
		return result;
	}
	
	public static boolean isThisPackage(String packageName){
		return packageName.equals("com.device.security.analytics.androidsecurityanalyticspro") || packageName.equals("com.device.security.analytics.androidsecurityanalytics");
	}
}
