package com.itheima.swipelayout.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.swipelayout.R;
import com.itheima.swipelayout.ui.SwipeLayout;
import com.itheima.swipelayout.ui.SwipeLayout.OnSwipeLayoutListener;

import java.util.ArrayList;

import static com.itheima.swipelayout.bean.Cheeses.NAMES;
/**
 * ============================================================
 * Copyright：Google有限公司版权所有 (c) 2017
 * Author：   陈冠杰
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChen1999
 * 博客：     http://blog.csdn.net/axi295309066
 * 微博：     AndroidDeveloper
 * <p>
 * Project_Name：SwipeLayout
 * Package_Name：com.itheima.swipelayout
 * Version：1.0
 * time：2016/2/15 16:44
 * des ：${TODO}
 * gitVersion：$Rev$
 * updateAuthor：$Author$
 * updateDate：$Date$
 * updateDes：${TODO}
 * ============================================================
 **/
public class MyAdapter extends BaseAdapter {

	protected static final String TAG = "TAG";


	public MyAdapter(Context context) {
		super();
		this.context = context;
		
		opendItems = new ArrayList<SwipeLayout>();
	}

	private Context context;
	private ArrayList<SwipeLayout> opendItems;
	
	@Override
	public int getCount() {
		return NAMES.length;
	}

	@Override
	public Object getItem(int position) {
		return NAMES[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		if(convertView == null){
			view = View.inflate(context, R.layout.item_list, null);
			
		}
		ViewHolder mHolder = ViewHolder.getHolder(view);
		
		SwipeLayout sl = (SwipeLayout)view;
		sl.setSwipeLayoutListener(new OnSwipeLayoutListener() {
			
			@Override
			public void onStartOpen(SwipeLayout mSwipeLayout) {
				Log.d(TAG, "onStartOpen");

				// 要去开启时,先遍历所有已打开条目, 逐个关闭
				
				for (SwipeLayout layout : opendItems) {
					layout.close();
				}
				
				opendItems.clear();
			}
			
			@Override
			public void onStartClose(SwipeLayout mSwipeLayout) {
				Log.d(TAG, "onStartClose");
			}
			
			@Override
			public void onOpen(SwipeLayout mSwipeLayout) {
				Log.d(TAG, "onOpen");
				// 添加进集合
				opendItems.add(mSwipeLayout);
			}
			@Override
			public void onDraging(SwipeLayout mSwipeLayout) {
			}
			
			@Override
			public void onClose(SwipeLayout mSwipeLayout) {
				Log.d(TAG, "onClose");
				// 移除集合
				opendItems.remove(mSwipeLayout);
			}
		});
		return view;
	}
	
	static class ViewHolder {
		TextView tv_call;
		TextView tv_del;

		public static ViewHolder getHolder(View view) {
			Object tag = view.getTag();
			if(tag == null){
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.tv_call = (TextView)view.findViewById(R.id.tv_call);
				viewHolder.tv_del = (TextView)view.findViewById(R.id.tv_del);
				tag = viewHolder;
				view.setTag(tag);
			}
			return (ViewHolder)tag;
		}
	}

}
