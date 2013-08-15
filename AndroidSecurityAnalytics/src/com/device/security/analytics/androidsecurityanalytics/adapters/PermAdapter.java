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

import com.device.security.analytics.androidsecurityanalytics.R;
import com.device.security.analytics.androidsecurityanalytics.beans.PermBean;

public class PermAdapter extends BaseExpandableListAdapter{
	
	private Activity context;
	private ArrayList<PermBean> permBeans;
	
	public PermAdapter(Activity context, ArrayList<PermBean> permBeans){
		this.context = context;
		this.permBeans = permBeans;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		
		return permBeans.get(groupPosition).getInfo();
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
			convertView = inflater.inflate(R.layout.perm_child_item, null);
		}
		
		TextView item = (TextView) convertView.findViewById(R.id.perm_info);
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
		return permBeans.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return permBeans.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		PermBean bean = (PermBean) getGroup(groupPosition);
		
		String question = bean.getDisplayName();
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.perm_group_item, null);
		}
		
		TextView questionView = (TextView) convertView.findViewById(R.id.perm_display);
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
