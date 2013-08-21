package com.device.security.analytics.androidsecurityanalytics.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.device.security.analytics.androidsecurityanalytics.beans.FaqBean;
import com.device.security.analytics.androidsecurityanalyticspro.R;

public class FaqAdapter extends BaseExpandableListAdapter{
	
	private Activity context;
	private ArrayList<FaqBean> faqBeans;
	
	public FaqAdapter(Activity context, ArrayList<FaqBean> faqBeans){
		this.context = context;
		this.faqBeans = faqBeans;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return faqBeans.get(groupPosition).getAnswer();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.faq_child_item, null);
		}
		
		TextView item = (TextView) convertView.findViewById(R.id.faq_answer);
		String answer = (String) getChild(groupPosition, childPosition);
		item.setText(answer);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return faqBeans.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return faqBeans.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		FaqBean bean = (FaqBean) getGroup(groupPosition);
		
		String question = bean.getQuestion();
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.faq_group_item, null);
		}
		
		TextView questionView = (TextView) convertView.findViewById(R.id.faq_question);
		questionView.setTypeface(null, Typeface.BOLD);
		questionView.setText(question);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
