package com.device.security.analytics.androidsecurityanalytics;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.device.security.analytics.androidsecurityanalytics.adapters.FaqAdapter;
import com.device.security.analytics.androidsecurityanalytics.beans.FaqBean;
import com.device.security.analytics.androidsecurityanalytics.helpers.DatabaseHelper;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class FaqActivity extends Activity{
	
	ExpandableListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq_layout);
		setTitle("FAQ/Info");
		
		DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
		
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dbHelper.openDataBase();
		
		ArrayList<FaqBean> faqBeans = dbHelper.getFaqs();
		
		listView = (ExpandableListView) findViewById(R.id.faq_list);
		final FaqAdapter faqAdapter = new FaqAdapter(this, faqBeans);
		listView.setAdapter(faqAdapter);
	}

}
