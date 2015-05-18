package com.example.androidlibraryutils.scrollview.activity;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.androidlibraryutils.BaseActivity;
import com.example.androidlibraryutils.R;
import com.example.androidlibraryutils.view.LinearLayoutForListView;
import com.example.androidlibraryutils.view.PRListView;
import com.example.androidlibraryutils.view.PRScrollView;
import com.example.androidlibraryutils.view.PRScrollView.OnRefreshLoadingMoreListener;

public class RefreshScrollViewActivity extends BaseActivity{
	private PRScrollView mPrScrollView;
	private LinearLayoutForListView mLayoutForListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refresh_scrollview);
		init();
	}
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
//		list.addAll(Arrays.asList(datas));
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		
		mPrScrollView.setOnRefresh();
		
	}
	@Override
	protected void initViews() {
		mLayoutForListView = (LinearLayoutForListView)findViewById(R.id.linear_list);
		mPrScrollView = (PRScrollView) findViewById(R.id.scrollview);
		mPrScrollView.setAutoPullRefresh(true);
		mPrScrollView.setAutoFetchMore(true);
		mPrScrollView.setFootViewVisible(false);
	}
	@Override
	protected void bindViews() {
		mPrScrollView.setOnRefreshListener(new OnRefreshLoadingMoreListener() {
			
			@Override
			public void onRefresh() {
				new GetDataAsyn().execute(PRListView.REFRESH_TYPE);
			}
			
			@Override
			public void onLoadMore() {
				new GetDataAsyn().execute(PRListView.LOAD_MORE_TYPE);
			}
		});
	}
	/**
	 * 模块数据
	 */
	private String[] datas = 
		{"RefreshListViewExample","RefreshListViewExample","RefreshListViewExample",
		"RefreshListViewExample","RefreshListViewExample","RefreshListViewExample",
		"RefreshListViewExample","RefreshListViewExample","RefreshListViewExample",
		"RefreshListViewExample","RefreshListViewExample","RefreshListViewExample","RefreshListViewExample"};
	
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter;
	class GetDataAsyn extends AsyncTask<Integer, Integer, Integer>{
		@Override
		protected Integer doInBackground(Integer... params) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			return params[0];//测试例子为方便，返回Listview加载类型，实际自己定义。
		}
		@Override
		protected void onPostExecute(Integer result) {
			mPrScrollView.setFootViewVisible(true);
			
			if (result == PRListView.REFRESH_TYPE) {
				mPrScrollView.onRefreshComplete();
				list.clear();
				list.addAll(Arrays.asList(datas));
			}
			else
			{
				list.addAll(Arrays.asList(datas));
			}
			
			mPrScrollView.onLoadMoreComplete(false);
			mAdapter.notifyDataSetChanged();
			mLayoutForListView.setAdapter(mAdapter);
		}
	}
	
}

