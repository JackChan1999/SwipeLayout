package com.itheima.swipelayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.itheima.swipelayout.adapter.MyAdapter;
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
public class MainActivity extends Activity {

	private static final String TAG = "TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListView mList = (ListView) findViewById(R.id.lv);
		mList.setAdapter(new MyAdapter(MainActivity.this));
		
		
	}

}
