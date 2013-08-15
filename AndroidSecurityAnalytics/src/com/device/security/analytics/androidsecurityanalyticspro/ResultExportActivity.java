package com.device.security.analytics.androidsecurityanalyticspro;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalyticspro.helpers.FileCreationHelper;
import com.device.security.analytics.androidsecurityanalyticspro.utils.LockPatternUtils;

public class ResultExportActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LockPatternUtils lockUtils = new LockPatternUtils(getApplicationContext());
		File file = FileCreationHelper.createFile(getPackageManager(), lockUtils, getApplicationContext());
		
		if(file != null){
		
		Uri uri = Uri.fromFile(file);
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.putExtra(Intent.EXTRA_SUBJECT, "Android Security Analysis Results");
		//i.putExtra(Intent.EXTRA_TEXT, "Content");
		i.putExtra(Intent.EXTRA_STREAM, uri);
		i.setType("text/plain");
		startActivity(Intent.createChooser(i, "Send mail"));
		}
		else{
			//Log.d("Error exporting", "There was an error exporting");
		}
		
	}
	
}
