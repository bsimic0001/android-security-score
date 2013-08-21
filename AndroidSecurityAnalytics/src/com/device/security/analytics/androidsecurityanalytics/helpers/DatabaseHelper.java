package com.device.security.analytics.androidsecurityanalytics.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalytics.beans.FaqBean;
import com.device.security.analytics.androidsecurityanalytics.beans.PermBean;
import com.device.security.analytics.androidsecurityanalytics.beans.TrustedApp;

public class DatabaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private String DB_PATH;

	private static String DB_NAME;

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.DB_NAME = "AndroidSecurityAnalytics.sqlite";
		this.DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		this.myContext = context;
	}

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context, String dbName, String dbPath) {
		super(context, DB_NAME, null, 1);
		this.DB_NAME = dbName;
		this.DB_PATH = dbPath;
		this.myContext = context;
		// copyDatabaseToLocal();
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created
			// into the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			String dbPath = myContext.getFilesDir() + File.separator + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.OPEN_READWRITE);

		} catch (SQLiteException e) {
			// Log.d("DB ERROR", e.getMessage());
		}

		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		String path = myContext.getFilesDir().getAbsolutePath()
				+ File.separator + "databases" + File.separator + DB_NAME;
		File f = new File(path);

		// InputStream outInput = myContext.openFileInput(path);
		// myContext.openFileOutput(DB_NAME, 0);

		// Open the empty db as the output stream
		OutputStream myOutput = myContext.openFileOutput(DB_NAME, 0);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}



	public void openDataBase() {

		// Open the database
		String myPath = DB_PATH + DB_NAME;

		String dbPath = myContext.getFilesDir() + File.separator + DB_NAME;

		try {

			myDataBase = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.OPEN_READWRITE);
			
			
			myDataBase.execSQL("CREATE TABLE IF NOT EXISTS 'TRUSTED_APPS' ('package' VARCHAR PRIMARY KEY  NOT NULL  UNIQUE , 'trusted' BOOL)");
			
		} catch (SQLException e) {
			Log.i("Could not open DB", "could not open DB", e);
		}
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public String getLockSettingFromDB() {
		Cursor cursor;
		String result = "0";
		if ((cursor = myDataBase.query("locksettings", new String[] { "_id",
				"name", "value", "user" }, "NAME" + "=?",
				new String[] { "lockscreen.password_type" }, null, null, null)) != null) {
			if (cursor.moveToFirst()) {
				result = cursor.getString(0);
			}
			cursor.close();
		}
		return result;
	}

	public ArrayList<FaqBean> getFaqs() {
		ArrayList<FaqBean> beans = new ArrayList<FaqBean>();

		Cursor c = myDataBase.query("FAQ", new String[] { "id", "question",
				"answer" }, null, null, null, null, null);

		if (c != null) {

			c.moveToFirst();
			while (c.isAfterLast() == false) {

				String id = c.getString(0);
				String question = c.getString(1);
				String answer = c.getString(2);

				FaqBean bean = new FaqBean(id, question, answer);
				beans.add(bean);
				c.moveToNext();
			}

		}

		return beans;
	}

	public ArrayList<PermBean> getPerms() {
		ArrayList<PermBean> beans = new ArrayList<PermBean>();

		Cursor c = myDataBase.query("Permissions", new String[] { "id", "name",
				"displayName", "rank", "info" }, null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
			while (c.isAfterLast() == false) {

				int id = c.getInt(0);
				String name = c.getString(1);
				String displayName = c.getString(2);
				int rank = c.getInt(3);
				String info = c.getString(4);

				PermBean pb = new PermBean(id, name, displayName, rank, info);
				beans.add(pb);
				c.moveToNext();
			}
		}

		return beans;
	}

	public ArrayList<TrustedApp> getTrustedApps() {
		ArrayList<TrustedApp> beans = new ArrayList<TrustedApp>();

		Cursor c = myDataBase.query("TRUSTED_APPS", new String[] { "package",
				"trusted" }, null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
			while (c.isAfterLast() == false) {

				String packageName = c.getString(0);
				boolean trusted = c.getInt(1) > 0;

				TrustedApp tb = new TrustedApp(packageName, trusted);
				beans.add(tb);
				c.moveToNext();
			}
		}

		return beans;
	}

	public void insertTrustedApp(String packageName, boolean value) {
		ContentValues values = new ContentValues();
		values.put("package", packageName); // Contact Name
		values.put("trusted", value); // Contact Phone Number

		// Inserting Row
		try {
			myDataBase.insert("TRUSTED_APPS", null, values);
			// myDataBase.close(); // Closing database connection
		} catch (SQLiteConstraintException e) {
			// Log.d("it exists", "item exists", e);
		} finally {
			// myDataBase.close();
		}
	}

	public void deleteTrustedApp(String packageName) {
		String whereClause = "package"+"=?";
		String[]whereArgs = new String[] {packageName};
		
		myDataBase.delete("TRUSTED_APPS", whereClause, whereArgs);
	}

	public boolean isTrustedApp(String packageName) {
		boolean result = false;

		//Cursor c = myDataBase.rawQuery(
		//		"SELECT count(*) from TRUSTED_APPS where package=?",
		//		new String[] { packageName });
		
		
		Cursor c = myDataBase.query("TRUSTED_APPS",
		          new String[] {"package"},
		          "package"+"="+"?",
		          new String[] { packageName },
		          null,
		          null,
		          null
		         );

		c.moveToFirst();
		int count = c.getCount();
		//int count = c.getCount();

		if (count > 0)
			result = true;

		return result;
	}

}