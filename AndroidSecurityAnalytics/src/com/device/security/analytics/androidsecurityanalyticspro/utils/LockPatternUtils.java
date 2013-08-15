/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.device.security.analytics.androidsecurityanalyticspro.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.FileObserver;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalyticspro.helpers.DatabaseHelper;

/**
 * Utilities for the lock pattern and its settings.
 */
public class LockPatternUtils {

	private static final String OPTION_ENABLE_FACELOCK = "enable_facelock";

	private static final String TAG = "LockPatternUtils";

	public static final String DATABASE_NAME = "locksettings.db";
	public static final String SYSTEM_DIRECTORY = "/system/";
	private static final String LOCK_PATTERN_FILE = "gesture.key";
	private static final String LOCK_PASSWORD_FILE = "password.key";

	/**
	 * The maximum number of incorrect attempts before the user is prevented
	 * from trying again for {@link #FAILED_ATTEMPT_TIMEOUT_MS}.
	 */
	public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;

	/**
	 * The number of incorrect attempts before which we fall back on an
	 * alternative method of verifying the user, and resetting their lock
	 * pattern.
	 */
	public static final int FAILED_ATTEMPTS_BEFORE_RESET = 20;

	/**
	 * How long the user is prevented from trying again after entering the wrong
	 * pattern too many times.
	 */
	public static final long FAILED_ATTEMPT_TIMEOUT_MS = 30000L;

	/**
	 * The interval of the countdown for showing progress of the lockout.
	 */
	public static final long FAILED_ATTEMPT_COUNTDOWN_INTERVAL_MS = 1000L;

	/**
	 * This dictates when we start telling the user that continued failed
	 * attempts will wipe their device.
	 */
	public static final int FAILED_ATTEMPTS_BEFORE_WIPE_GRACE = 5;

	/**
	 * The minimum number of dots in a valid pattern.
	 */
	public static final int MIN_LOCK_PATTERN_SIZE = 4;

	/**
	 * The minimum number of dots the user must include in a wrong pattern
	 * attempt for it to be counted against the counts that affect
	 * {@link #FAILED_ATTEMPTS_BEFORE_TIMEOUT} and
	 * {@link #FAILED_ATTEMPTS_BEFORE_RESET}
	 */
	public static final int MIN_PATTERN_REGISTER_FAIL = MIN_LOCK_PATTERN_SIZE;

	private final static String LOCKOUT_PERMANENT_KEY = "lockscreen.lockedoutpermanently";
	private final static String LOCKOUT_ATTEMPT_DEADLINE = "lockscreen.lockoutattemptdeadline";
	private final static String PATTERN_EVER_CHOSEN_KEY = "lockscreen.patterneverchosen";
	public final static String PASSWORD_TYPE_KEY = "lockscreen.password_type";
	public static final String PASSWORD_TYPE_ALTERNATE_KEY = "lockscreen.password_type_alternate";
	private final static String LOCK_PASSWORD_SALT_KEY = "lockscreen.password_salt";
	private final static String DISABLE_LOCKSCREEN_KEY = "lockscreen.disabled";
	private final static String LOCKSCREEN_OPTIONS = "lockscreen.options";
	public final static String LOCKSCREEN_BIOMETRIC_WEAK_FALLBACK = "lockscreen.biometric_weak_fallback";
	public final static String BIOMETRIC_WEAK_EVER_CHOSEN_KEY = "lockscreen.biometricweakeverchosen";

	private final static String PASSWORD_HISTORY_KEY = "lockscreen.passwordhistory";

	private final Context mContext;
	private final ContentResolver mContentResolver;
	private DevicePolicyManager mDevicePolicyManager;
	private KeyguardManager keyguardManager;
	private ComponentName adminComponent;
	private static String sLockPatternFilename;
	private static String sLockPasswordFilename;

	private static final AtomicBoolean sHaveNonZeroPatternFile = new AtomicBoolean(
			false);
	private static final AtomicBoolean sHaveNonZeroPasswordFile = new AtomicBoolean(
			false);

	private static FileObserver sPasswordObserver;

	private static class PasswordFileObserver extends FileObserver {
		public PasswordFileObserver(String path, int mask) {
			super(path, mask);
		}

		@Override
		public void onEvent(int event, String path) {
			if (LOCK_PATTERN_FILE.equals(path)) {
				//Log.d(TAG, "lock pattern file changed");
				sHaveNonZeroPatternFile.set(new File(sLockPatternFilename)
						.length() > 0);
			} else if (LOCK_PASSWORD_FILE.equals(path)) {
				//Log.d(TAG, "lock password file changed");
				sHaveNonZeroPasswordFile.set(new File(sLockPasswordFilename)
						.length() > 0);
			}
		}
	}

	public KeyguardManager getKeyguardManager() {
		keyguardManager = (KeyguardManager) mContext
				.getSystemService(Context.KEYGUARD_SERVICE);
		return keyguardManager;
	}

	public DevicePolicyManager getDevicePolicyManager() {
		if (mDevicePolicyManager == null) {
			mDevicePolicyManager = (DevicePolicyManager) mContext
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			if (mDevicePolicyManager == null) {
				Log.e(TAG,
						"Can't get DevicePolicyManagerService: is it running?",
						new IllegalStateException("Stack trace:"));
			}
		}

		// List<ComponentName> list = mDevicePolicyManager.getActiveAdmins();
		// adminComponent = list.get(0);
		// String strValue = null;
		// strValue =
		// String.valueOf(mDevicePolicyManager.getPasswordQuality(adminComponent));
		adminComponent = null;
		return mDevicePolicyManager;
	}

	//
	// public void checkAndSetPolicy(){
	// mDevicePolicyManager.setPasswordQuality(null,
	// DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
	// boolean isSufficient = mDevicePolicyManager.isActivePasswordSufficient();
	// Log.d("sufficient", "sufficient is " + isSufficient );
	// }

	/**
	 * @param contentResolver
	 *            Used to look up and save settings.
	 */
	public LockPatternUtils(Context context) {
		mContext = context;
		mContentResolver = context.getContentResolver();

		// Initialize the location of gesture & PIN lock files
		if (sLockPatternFilename == null) {
			String dataSystemDirectory = android.os.Environment
					.getDataDirectory().getAbsolutePath() + SYSTEM_DIRECTORY;

			//Log.d("Sys Dir", dataSystemDirectory);

			sLockPatternFilename = dataSystemDirectory + LOCK_PATTERN_FILE;
			sLockPasswordFilename = dataSystemDirectory + LOCK_PASSWORD_FILE;
			sHaveNonZeroPatternFile
					.set(new File(sLockPatternFilename).length() > 0);
			sHaveNonZeroPasswordFile.set(new File(sLockPasswordFilename)
					.length() > 0);
			int fileObserverMask = FileObserver.CLOSE_WRITE
					| FileObserver.DELETE | FileObserver.MOVED_TO
					| FileObserver.CREATE;
			sPasswordObserver = new PasswordFileObserver(dataSystemDirectory,
					fileObserverMask);
			sPasswordObserver.startWatching();
		}
	}

	public int getRequestedMinimumPasswordLength() {
		return getDevicePolicyManager()
				.getPasswordMinimumLength(adminComponent);
	}

	public boolean getEncryptionScheme() {

		try {
			int status = getDevicePolicyManager().getStorageEncryptionStatus();
			if (status == 3)
				return true;
			else
				return false;
		} catch (RuntimeException e) {
			//Log.d("No Encryption Supported",
			//		"Device does not support encryption");
		}
		catch(NoSuchMethodError err){
			//Log.d("No such method", "no such method for this device", err);
		}

		return false;

	}

	/**
	 * Gets the device policy password mode. If the mode is non-specific,
	 * returns MODE_PATTERN which allows the user to choose anything.
	 */
	public int getRequestedPasswordQuality() {
		return getDevicePolicyManager().getPasswordQuality(null);
	}

	public int getRequestedPasswordHistoryLength() {
		return getDevicePolicyManager().getPasswordHistoryLength(null);
	}

	public int getRequestedPasswordMinimumLetters() {
		return getDevicePolicyManager().getPasswordMinimumLetters(null);
	}

	public int getRequestedPasswordMinimumUpperCase() {
		return getDevicePolicyManager().getPasswordMinimumUpperCase(null);
	}

	public int getRequestedPasswordMinimumLowerCase() {
		return getDevicePolicyManager().getPasswordMinimumLowerCase(null);
	}

	public int getRequestedPasswordMinimumNumeric() {
		return getDevicePolicyManager().getPasswordMinimumNumeric(null);
	}

	public int getRequestedPasswordMinimumSymbols() {
		return getDevicePolicyManager().getPasswordMinimumSymbols(null);
	}

	public int getRequestedPasswordMinimumNonLetter() {
		return getDevicePolicyManager().getPasswordMinimumNonLetter(null);
	}

	/**
	 * Check to see if a password matches the saved password. If no password
	 * exists, always returns true.
	 * 
	 * @param password
	 *            The password to check.
	 * @return Whether the password matches the stored one.
	 */
	public boolean checkPassword(String password) {
		try {
			// Read all the bytes from the file
			RandomAccessFile raf = new RandomAccessFile(sLockPasswordFilename,
					"r");
			final byte[] stored = new byte[(int) raf.length()];
			int got = raf.read(stored, 0, stored.length);
			raf.close();
			if (got <= 0) {
				return true;
			}
			// Compare the hash from the file with the entered password's hash
			return Arrays.equals(stored, passwordToHash(password));
		} catch (FileNotFoundException fnfe) {
			return true;
		} catch (IOException ioe) {
			return true;
		}
	}

	/**
	 * Check to see if a password matches any of the passwords stored in the
	 * password history.
	 * 
	 * @param password
	 *            The password to check.
	 * @return Whether the password matches any in the history.
	 */
	public boolean checkPasswordHistory(String password) {
		String passwordHashString = new String(passwordToHash(password));
		String passwordHistory = getString(PASSWORD_HISTORY_KEY);
		if (passwordHistory == null) {
			return false;
		}
		// Password History may be too long...
		int passwordHashLength = passwordHashString.length();
		int passwordHistoryLength = getRequestedPasswordHistoryLength();
		if (passwordHistoryLength == 0) {
			return false;
		}
		int neededPasswordHistoryLength = passwordHashLength
				* passwordHistoryLength + passwordHistoryLength - 1;
		if (passwordHistory.length() > neededPasswordHistoryLength) {
			passwordHistory = passwordHistory.substring(0,
					neededPasswordHistoryLength);
		}
		return passwordHistory.contains(passwordHashString);
	}

	/**
	 * Check to see if the user has stored a lock pattern.
	 * 
	 * @return Whether a saved pattern exists.
	 */
	public boolean savedPatternExists() {
		return sHaveNonZeroPatternFile.get();
	}

	/**
	 * Check to see if the user has stored a lock pattern.
	 * 
	 * @return Whether a saved pattern exists.
	 */
	public boolean savedPasswordExists() {
		return sHaveNonZeroPasswordFile.get();
	}

	/**
	 * Return true if the user has ever chosen a pattern. This is true even if
	 * the pattern is currently cleared.
	 * 
	 * @return True if the user has ever chosen a pattern.
	 */
	public boolean isPatternEverChosen() {
		return getBoolean(PATTERN_EVER_CHOSEN_KEY);
	}

	/**
	 * Return true if the user has ever chosen biometric weak. This is true even
	 * if biometric weak is not current set.
	 * 
	 * @return True if the user has ever chosen biometric weak.
	 */
	public boolean isBiometricWeakEverChosen() {
		return getBoolean(BIOMETRIC_WEAK_EVER_CHOSEN_KEY);
	}

	/**
	 * Determine if LockScreen can be disabled. This is used, for example, to
	 * tell if we should show LockScreen or go straight to the home screen.
	 * 
	 * @return true if lock screen is can be disabled
	 */
	public boolean isLockScreenDisabled() {
		return !isSecure() && getLong(DISABLE_LOCKSCREEN_KEY, 0) != 0;
	}

	/**
	 * Calls back SetupFaceLock to delete the temporary gallery file
	 */
	public void deleteTempGallery() {
		Intent intent = new Intent().setClassName("com.android.facelock",
				"com.android.facelock.SetupFaceLock");
		intent.putExtra("deleteTempGallery", true);
		mContext.startActivity(intent);
	}

	/**
	 * Calls back SetupFaceLock to delete the gallery file when the lock type is
	 * changed
	 */
	void deleteGallery() {
		if (usingBiometricWeak()) {
			Intent intent = new Intent().setClassName("com.android.facelock",
					"com.android.facelock.SetupFaceLock");
			intent.putExtra("deleteGallery", true);
			mContext.startActivity(intent);
		}
	}

	/**
	 * Compute the password quality from the given password string.
	 */
	static public int computePasswordQuality(String password) {
		boolean hasDigit = false;
		boolean hasNonDigit = false;
		final int len = password.length();
		for (int i = 0; i < len; i++) {
			if (Character.isDigit(password.charAt(i))) {
				hasDigit = true;
			} else {
				hasNonDigit = true;
			}
		}

		if (hasNonDigit && hasDigit) {
			return DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC;
		}
		if (hasNonDigit) {
			return DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC;
		}
		if (hasDigit) {
			return DevicePolicyManager.PASSWORD_QUALITY_NUMERIC;
		}
		return DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED;
	}

	/**
	 * Retrieves the quality mode we're in. {@see
	 * DevicePolicyManager#getPasswordQuality(android.content.ComponentName)}
	 * 
	 * @return stored password quality
	 */
	public int getKeyguardStoredPasswordQuality() {
		int quality = (int) getLong(PASSWORD_TYPE_KEY,
				DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
		// If the user has chosen to use weak biometric sensor, then return the
		// backup locking
		// method and treat biometric as a special case.
		if (quality == DevicePolicyManager.PASSWORD_QUALITY_BIOMETRIC_WEAK) {
			quality = (int) getLong(PASSWORD_TYPE_ALTERNATE_KEY,
					DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
		}
		return quality;
	}

	/**
	 * @return true if the lockscreen method is set to biometric weak
	 */
	public boolean usingBiometricWeak() {
		int quality = (int) getLong(PASSWORD_TYPE_KEY,
				DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
		return quality == DevicePolicyManager.PASSWORD_QUALITY_BIOMETRIC_WEAK;
	}

	private String getSalt() {
		long salt = getLong(LOCK_PASSWORD_SALT_KEY, 0);
		if (salt == 0) {
			try {
				salt = SecureRandom.getInstance("SHA1PRNG").nextLong();
				setLong(LOCK_PASSWORD_SALT_KEY, salt);
				//Log.v(TAG, "Initialized lock password salt");
			} catch (NoSuchAlgorithmException e) {
				// Throw an exception rather than storing a password we'll never
				// be able to recover
				throw new IllegalStateException(
						"Couldn't get SecureRandom number", e);
			}
		}
		return Long.toHexString(salt);
	}

	/*
	 * Generate a hash for the given password. To avoid brute force attacks, we
	 * use a salted hash. Not the most secure, but it is at least a second level
	 * of protection. First level is that the file is in a location only
	 * readable by the system process.
	 * 
	 * @param password the gesture pattern.
	 * 
	 * @return the hash of the pattern in a byte array.
	 */
	public byte[] passwordToHash(String password) {
		if (password == null) {
			return null;
		}
		String algo = null;
		byte[] hashed = null;
		try {
			byte[] saltedPassword = (password + getSalt()).getBytes();
			byte[] sha1 = MessageDigest.getInstance(algo = "SHA-1").digest(
					saltedPassword);
			byte[] md5 = MessageDigest.getInstance(algo = "MD5").digest(
					saltedPassword);
			hashed = (toHex(sha1) + toHex(md5)).getBytes();
		} catch (NoSuchAlgorithmException e) {
			//Log.w(TAG, "Failed to encode string because of missing algorithm: "
			//		+ algo);
		}
		return hashed;
	}

	private static String toHex(byte[] ary) {
		final String hex = "0123456789ABCDEF";
		String ret = "";
		for (int i = 0; i < ary.length; i++) {
			ret += hex.charAt((ary[i] >> 4) & 0xf);
			ret += hex.charAt(ary[i] & 0xf);
		}
		return ret;
	}

	/**
	 * @return Whether the lock password is enabled, or if it is set as a backup
	 *         for biometric weak
	 */
	public boolean isLockPasswordEnabled() {
		// long mode = getLong(PASSWORD_TYPE_KEY, 0);
		// String modeString = getString(PASSWORD_TYPE_KEY);

		try {
			boolean keyGuardValue = getKeyguardManager().isKeyguardSecure();

			if (keyGuardValue) {
				return true;
			} else {
				//Log.d("Keyguard", "Keyguard value is: " + keyGuardValue);
			}
		} catch (NoSuchMethodError e) {
			//Log.d("method not found",
			//		"isKeyguardSecure not found on this device");
		}

		int mode = getDevicePolicyManager().getPasswordQuality(adminComponent);

		//Log.d("pass mode long --->", "pass mode long --->" + mode);
		// Log.d("pass mode String --->", "pass mode String --->" + modeString);

		// checkAndSetPolicy();

		long backupMode = getLong(PASSWORD_TYPE_ALTERNATE_KEY, 0);
		final boolean passwordEnabled = mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_COMPLEX;

		final boolean backupEnabled = backupMode == DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC
				|| backupMode == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
				|| backupMode == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING
				|| backupMode == DevicePolicyManager.PASSWORD_QUALITY_COMPLEX;

		return savedPasswordExists()
				&& (passwordEnabled || (usingBiometricWeak() && backupEnabled));
	}

	/**
	 * @return Whether the lock pattern is enabled, or if it is set as a backup
	 *         for biometric weak
	 */
	public boolean isLockPatternEnabled() {
		final boolean backupEnabled = getLong(PASSWORD_TYPE_ALTERNATE_KEY,
				DevicePolicyManager.PASSWORD_QUALITY_SOMETHING) == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING;

		return getBoolean(Settings.Secure.LOCK_PATTERN_ENABLED)
				&& (getLong(PASSWORD_TYPE_KEY,
						DevicePolicyManager.PASSWORD_QUALITY_SOMETHING) == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING || (usingBiometricWeak() && backupEnabled));
	}

	public int getPasswordQuality() {
		int result = 0;

		if (getBoolean(Settings.Secure.LOCK_PATTERN_ENABLED)) {

			if (getLong(PASSWORD_TYPE_KEY,
					DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) != 0) {
				result = DevicePolicyManager.PASSWORD_QUALITY_NUMERIC;
			} else if (getLong(PASSWORD_TYPE_KEY,
					DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC) != 0) {
				result = DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC;
			} else if (getLong(PASSWORD_TYPE_KEY,
					DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC) != 0) {
				result = DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC;
			}
		}

		return result;

	}

	/**
	 * @return Whether biometric weak lock is installed and that the front
	 *         facing camera exists
	 */
	public boolean isBiometricWeakInstalled() {
		// Check that the system flag was set
		if (!OPTION_ENABLE_FACELOCK.equals(getString(LOCKSCREEN_OPTIONS))) {
			return false;
		}

		// Check that it's installed
		PackageManager pm = mContext.getPackageManager();
		try {
			pm.getPackageInfo("com.android.facelock",
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}

		// Check that the camera is enabled
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			return false;
		}

		if (getDevicePolicyManager().getCameraDisabled(null)) {
			return false;
		}

		return true;
	}

	/**
	 * Set whether the lock pattern is enabled.
	 */
	public void setLockPatternEnabled(boolean enabled) {
		setBoolean(Settings.Secure.LOCK_PATTERN_ENABLED, enabled);
	}

	/**
	 * @return Whether the visible pattern is enabled.
	 */
	public boolean isVisiblePatternEnabled() {
		return getBoolean(Settings.Secure.LOCK_PATTERN_VISIBLE);
	}

	/**
	 * Set whether the visible pattern is enabled.
	 */
	public void setVisiblePatternEnabled(boolean enabled) {
		setBoolean(Settings.Secure.LOCK_PATTERN_VISIBLE, enabled);
	}

	/**
	 * @return Whether tactile feedback for the pattern is enabled.
	 */
	public boolean isTactileFeedbackEnabled() {
		return getBoolean(Settings.Secure.LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED)
				|| getBoolean(Settings.System.HAPTIC_FEEDBACK_ENABLED);
	}

	/**
	 * Set whether tactile feedback for the pattern is enabled.
	 */
	public void setTactileFeedbackEnabled(boolean enabled) {
		setBoolean(Settings.Secure.LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED,
				enabled);
	}

	/**
	 * Set and store the lockout deadline, meaning the user can't attempt
	 * his/her unlock pattern until the deadline has passed.
	 * 
	 * @return the chosen deadline.
	 */
	public long setLockoutAttemptDeadline() {
		final long deadline = SystemClock.elapsedRealtime()
				+ FAILED_ATTEMPT_TIMEOUT_MS;
		setLong(LOCKOUT_ATTEMPT_DEADLINE, deadline);
		return deadline;
	}

	/**
	 * @return The elapsed time in millis in the future when the user is allowed
	 *         to attempt to enter his/her lock pattern, or 0 if the user is
	 *         welcome to enter a pattern.
	 */
	public long getLockoutAttemptDeadline() {
		final long deadline = getLong(LOCKOUT_ATTEMPT_DEADLINE, 0L);
		final long now = SystemClock.elapsedRealtime();
		if (deadline < now || deadline > (now + FAILED_ATTEMPT_TIMEOUT_MS)) {
			return 0L;
		}
		return deadline;
	}

	/**
	 * @return Whether the user is permanently locked out until they verify
	 *         their credentials. Occurs after
	 *         {@link #FAILED_ATTEMPTS_BEFORE_RESET} failed attempts.
	 */
	public boolean isPermanentlyLocked() {
		return getBoolean(LOCKOUT_PERMANENT_KEY);
	}

	/**
	 * Set the state of whether the device is permanently locked, meaning the
	 * user must authenticate via other means.
	 * 
	 * @param locked
	 *            Whether the user is permanently locked out until they verify
	 *            their credentials. Occurs after
	 *            {@link #FAILED_ATTEMPTS_BEFORE_RESET} failed attempts.
	 */
	public void setPermanentlyLocked(boolean locked) {
		setBoolean(LOCKOUT_PERMANENT_KEY, locked);
	}

	/**
	 * @return A formatted string of the next alarm (for showing on the lock
	 *         screen), or null if there is no next alarm.
	 */
	public String getNextAlarm() {
		String nextAlarm = Settings.System.getString(mContentResolver,
				Settings.System.NEXT_ALARM_FORMATTED);
		if (nextAlarm == null || TextUtils.isEmpty(nextAlarm)) {
			return null;
		}
		return nextAlarm;
	}

	private boolean getBoolean(String secureSettingKey) {
		return 1 == android.provider.Settings.Secure.getInt(mContentResolver,
				secureSettingKey, 0);
	}

	private void setBoolean(String secureSettingKey, boolean enabled) {
		android.provider.Settings.Secure.putInt(mContentResolver,
				secureSettingKey, enabled ? 1 : 0);
	}

	private long getLong(String secureSettingKey, long def) {
		long result = 0;
		result = android.provider.Settings.Secure.getLong(mContentResolver,
				secureSettingKey, def);

		if (result == 0) {
			// result = getLockSettingsLongFromDB();
		}

		return result;
	}

	private void setLong(String secureSettingKey, long value) {
		android.provider.Settings.Secure.putLong(mContentResolver,
				secureSettingKey, value);
	}

	private String getString(String secureSettingKey) {
		return android.provider.Settings.Secure.getString(mContentResolver,
				secureSettingKey);
	}

	private void setString(String secureSettingKey, String value) {
		android.provider.Settings.Secure.putString(mContentResolver,
				secureSettingKey, value);
	}

	public boolean isSecure() {
		long mode = getKeyguardStoredPasswordQuality();
		final boolean isPattern = mode == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING;
		final boolean isPassword = mode == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC
				|| mode == DevicePolicyManager.PASSWORD_QUALITY_COMPLEX;
		final boolean secure = isPattern && isLockPatternEnabled()
				&& savedPatternExists() || isPassword && savedPasswordExists();
		return secure;
	}

	private void finishBiometricWeak() {
		setBoolean(BIOMETRIC_WEAK_EVER_CHOSEN_KEY, true);

		// Launch intent to show final screen, this also
		// moves the temporary gallery to the actual gallery
		Intent intent = new Intent();
		intent.setClassName("com.android.facelock",
				"com.android.facelock.SetupEndScreen");
		mContext.startActivity(intent);
	}

	public long getLockSettingsLongFromDB() {
		long result = 0;
		DatabaseHelper dbHelper = new DatabaseHelper(mContext, DATABASE_NAME,
				android.os.Environment.getDataDirectory().getAbsolutePath()
						+ LockPatternUtils.SYSTEM_DIRECTORY);
		dbHelper.openDataBase();

		result = Long.getLong(dbHelper.getLockSettingFromDB());

		return result;
	}

}
