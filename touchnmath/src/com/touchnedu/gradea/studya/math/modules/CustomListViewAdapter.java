package com.touchnedu.gradea.studya.math.modules;

import java.util.ArrayList;

import com.touchnedu.gradea.studya.math.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListViewAdapter extends BaseAdapter {
	//Adapter에 추가된 데이터를 저장하기 위한 ArrayList
  private ArrayList<CustomListViewItem> listViewItemList 
  																				= new ArrayList<CustomListViewItem>();

  // 생성자
  public CustomListViewAdapter() {}

  // Adapter에 사용되는 데이터의 개수 리턴
	@Override
	public int getCount() {
		return listViewItemList.size();
	}

	// 지정한 위치(position)에 있는 데이터 리턴
	@Override
	public Object getItem(int position) {
		return listViewItemList.get(position);
	}

	// 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
	@Override
	public long getItemId(int position) {
		return position;
	}

	// position에 위치한 데이터를 화면에 출력하는 데 사용될 View 리턴
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Context context = parent.getContext();
		
		// listview_item.xml을 inflate하여 convertView 참조 획득
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater)context
														.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listview_item, parent, false);
		}
		
		// 화면에 표시될 view(Layout이 inflate된)로부터 위젯에 대한 참조 획득
		ImageView iconView = (ImageView)convertView.findViewById(R.id.listVw_icon);
		TextView titleView = (TextView)convertView.findViewById(R.id.listVw_title);
		
		// Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
		CustomListViewItem listViewItem = listViewItemList.get(position);
		
		// 아이템 내 각 위젯에 데이터 반영
		iconView.setImageDrawable(listViewItem.getdIcon());
		titleView.setText(listViewItem.getTitle());
				
		return convertView;
	}
	
	public void addItem(Drawable icon, String title) {
		CustomListViewItem item = new CustomListViewItem();
		
		item.setdIcon(icon);
		item.setTitle(title);
		
		listViewItemList.add(item);
	}

}
