package com.example.androidlibraryutils.listview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.androidlibraryutils.BaseActivity;
import com.example.androidlibraryutils.R;

public class ListViewExample extends BaseActivity {
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas));
	}
	@Override
	protected void initViews() {
		mListView = (ListView) findViewById(R.id.list);
	}
	@Override
	protected void bindViews() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onClickAction(arg2);
			}
			
		});
	}
	/**
	 * 模块数据
	 */
	private String[] datas = {"RefreshListViewExample"};
	private void onClickAction(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			startActivity(new Intent(this, RefreshListViewActivity.class));
			break;
		case 1:
			
			break;
		case 2:
	
			break;
		case 3:
	
			break;
		default:
			break;
		}
	}
}
