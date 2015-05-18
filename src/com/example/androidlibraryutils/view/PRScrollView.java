package com.example.androidlibraryutils.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.androidlibraryutils.R;

/***
 * 自定义拖拉ListView
 * 
 * @author zhangjia
 * 
 */
public class PRScrollView extends ScrollView implements OnClickListener {
	// 拖拉ListView枚举所有状态
	private enum DListViewState {
		LV_NORMAL, // 普通状态
		LV_PULL_REFRESH, // 下拉状态（为超过mHeadViewHeight）
		LV_RELEASE_REFRESH, // 松开可刷新状态（超过mHeadViewHeight）
		LV_LOADING;// 加载状态
	}

	// 点击加载更多枚举所有状态
	public enum DListViewLoadingMore {
		LV_NORMAL, // 普通状态
		LV_LOADING, // 加载状态
		LV_OVER; // 结束状态
	}
	
	public static final int LOAD_MORE_TYPE = 1;
	public static final int REFRESH_TYPE = 2;
	private ViewGroup mContentView;
	private View mHeadView;// 头部headView
	private TextView mRefreshTextview; // 刷新msg（mHeadView）
	private TextView mLastUpdateTextView;// 更新事件（mHeadView）
	private ImageView mArrowImageView;// 下拉图标（mHeadView）
	private ProgressBar mHeadProgressBar;// 刷新进度体（mHeadView）

	private int mHeadViewWidth; // headView的宽（mHeadView）
	private int mHeadViewHeight;// headView的高（mHeadView）

	private View mFootView;// 尾部mFootView
	private View mLoadMoreView;// mFootView 的view(mFootView)
	private TextView mLoadMoreTextView;// 加载更多.(mFootView)
	private View mLoadingView;// 加载中...View(mFootView)

	private Animation animation, reverseAnimation;// 旋转动画，旋转动画之后旋转动画.

	/**
	 * 用于保证startY的值在一个完整的touch事件中只被记录一次
	 */
	private boolean mIsRecord = false;

	private int mStartY, mMoveY;// 按下是的y坐标,move时的y坐标

	private DListViewState mlistViewState = DListViewState.LV_NORMAL;// 拖拉状态.(自定义枚举)

	private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;// 加载更多默认状态.
	/**
	 * 手势下拉距离比
	 */
	private final static int RATIO = 2;

	private boolean mBack = false;// headView是否返回.

	private OnRefreshLoadingMoreListener onRefreshLoadingMoreListener;// 下拉刷新接口（自定义）

	private boolean isScroller = true;// 是否屏蔽ListView滑动。
	private boolean isAutoMore = false;// 是否上拉加载更多。
	private boolean isAutoRefresh = false;// 是否下拉刷新。
	private boolean isFootViewVisible = true;//是否显示加载更多

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"MM-dd HH:mm");

	public PRScrollView(Context context) {
		super(context, null);
		
	}

	public PRScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	/***
	 * 注入下拉刷新接口
	 */
	public void setOnRefreshListener(
			OnRefreshLoadingMoreListener onRefreshLoadingMoreListener) {
		this.onRefreshLoadingMoreListener = onRefreshLoadingMoreListener;
	}
	public void setOnRefresh() {
		mlistViewState = DListViewState.LV_RELEASE_REFRESH;
		doActionUp();
	}
	/**
	 * 是否正在刷新
	 * @return
	 */
	public boolean isRefreshing() {
		return mlistViewState == DListViewState.LV_LOADING ;
	}
	/**
	 * 
	 * @param isFootViewVisible
	 */
	public void setFootViewVisible(boolean isFootViewVisible) {
		this.isFootViewVisible = isFootViewVisible;
		if (isFootViewVisible) {
			mFootView.setVisibility(View.VISIBLE);
		}
		else
		{
			mFootView.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			mContentView = (ViewGroup) getChildAt(0);
			removeAllViews();
			RelativeLayout layout = new RelativeLayout(getContext());
			
			RelativeLayout.LayoutParams RL_MW = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  
		                RelativeLayout.LayoutParams.MATCH_PARENT);//尤其注意这个位置，用的是父容器的布局参数  
			LinearLayout linearLayout=new LinearLayout(getContext());
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			
			
			initHeadView(getContext(), linearLayout);// 初始化该head.
			
			linearLayout.addView(mContentView);//初始化congtent
			
			
			initLoadMoreView(getContext(),linearLayout);// 初始化footer
			
			layout.addView(linearLayout,RL_MW);
			addView(layout);
		}
	}
	/***
	 * 初始话头部HeadView
	 * 
	 * @param context
	 *            上下文
	 * @param time
	 *            上次更新时间
	 */
	public void initHeadView(Context context, ViewGroup group) {
		mHeadView = LayoutInflater.from(context).inflate(R.layout.listview_head, null);
		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(60);

		mHeadProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);

		mRefreshTextview = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);

		mLastUpdateTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);
		// 显示更新事件
		mLastUpdateTextView.setText("更新于:" + dateFormat.format(new Date(System.currentTimeMillis())));

		measureView(mHeadView);
		// 获取宽和高
		mHeadViewWidth = mHeadView.getMeasuredWidth();
		mHeadViewHeight = mHeadView.getMeasuredHeight();

		group.addView(mHeadView, 0);;// 将初始好的ListView add进拖拽ListView
		// 在这里我们要将此headView设置到顶部不显示位置.
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

		initAnimation();// 初始化动画
	}

	/***
	 * 初始化底部加载更多控件
	 */
	private void initLoadMoreView(Context context,ViewGroup group) {
		mFootView = LayoutInflater.from(context).inflate(R.layout.listview_footer, null);

		mLoadMoreView = mFootView.findViewById(R.id.load_more_view);

		mLoadMoreTextView = (TextView) mFootView
				.findViewById(R.id.load_more_tv);

		mLoadingView = (LinearLayout) mFootView
				.findViewById(R.id.loading_layout);

		mLoadMoreView.setOnClickListener(this);

		group.addView(mFootView);
	}

	/***
	 * 初始化动画
	 */
	private void initAnimation() {
		// 旋转动画
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());// 匀速
		animation.setDuration(250);
		animation.setFillAfter(true);// 停留在最后状态.
		// 反向旋转动画
		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
	}

	/***
	 * 作用：测量 headView的宽和高.
	 * 
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/***
	 * touch 事件监听
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isAutoRefresh) {
			mLastUpdateTextView.setText("更新于:" + dateFormat.format(new Date(System.currentTimeMillis())));
			switch (ev.getAction()) {
			// 按下
			case MotionEvent.ACTION_DOWN:
				doActionDown(ev);
				break;
			// 移动
			case MotionEvent.ACTION_MOVE:
				doActionMove(ev);
				break;
			// 抬起
			case MotionEvent.ACTION_UP:
				doActionUp();
				break;
			default:
				break;
			}
			/***
			 * 如果是ListView本身的拉动，那么返回true，这样ListView不可以拖动.
			 * 如果不是ListView的拉动，那么调用父类方法，这样就可以上拉执行.
			 */
			if (isScroller) {
				return super.onTouchEvent(ev);
			} else {
				return true;
			}
		} else {
			return super.onTouchEvent(ev);
		}

	}

	/***
	 * 摁下操作
	 * 
	 * 作用：获取摁下是的y坐标
	 * 
	 * @param event
	 */
	void doActionDown(MotionEvent event) {
		if (mIsRecord == false) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
	}

	/***
	 * 拖拽移动操作
	 * 
	 * @param event
	 */
	void doActionMove(MotionEvent event) {
		mMoveY = (int) event.getY();// 获取实时滑动y坐标
		// 检测是否是一次touch事件.
		if (mIsRecord == false) {
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
		/***
		 * 如果touch关闭或者正处于Loading状态的话 return.
		 */
		if (mIsRecord == false || mlistViewState == DListViewState.LV_LOADING) {
			return;
		}
		// 向下啦headview移动距离为y移动的一半.（比较友好）
		int offset = (mMoveY - mStartY) / RATIO;

		switch (mlistViewState) {
		// 普通状态
		case LV_NORMAL: {
			// 如果<0，则意味着上滑动.
			if (offset > 0) {
				// 设置headView的padding属性.
				mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
				switchViewState(DListViewState.LV_PULL_REFRESH);// 下拉状态
			}

		}
			break;
		// 下拉状态
		case LV_PULL_REFRESH: {
			
			// 设置headView的padding属性.
			mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
			if (offset < 0) {
				/***
				 * 要明白为什么isScroller = false;
				 */
				isScroller = false;
				switchViewState(DListViewState.LV_NORMAL);// 普通状态
				Log.e("jj", "isScroller=" + isScroller);
			} else if (offset > mHeadViewHeight) {// 如果下拉的offset超过headView的高度则要执行刷新.
				switchViewState(DListViewState.LV_RELEASE_REFRESH);// 更新为可刷新的下拉状态.
			}
		}
			break;
		// 可刷新状态
		case LV_RELEASE_REFRESH: {
			// 设置headView的padding属性.
			mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
			// 下拉offset>0，但是没有超过headView的高度.那么要goback 原装.
			if (offset >= 0 && offset <= mHeadViewHeight) {
				mBack = true;
				switchViewState(DListViewState.LV_PULL_REFRESH);
			} else if (offset < 0) {
				switchViewState(DListViewState.LV_NORMAL);
			} else {

			}
		}
			break;
		default:
			return;
		}
		;
	}

	/***
	 * 手势抬起操作
	 * 
	 * @param event
	 */
	public void doActionUp() {
		mIsRecord = false;// 此时的touch事件完毕，要关闭。
		isScroller = true;// ListView可以Scrooler滑动.
		mBack = false;
		// 如果下拉状态处于loading状态.
		if (mlistViewState == DListViewState.LV_LOADING) {
			return;
		}
		// 处理相应状态.
		switch (mlistViewState) {
		// 普通状态
		case LV_NORMAL:

			break;
		// 下拉状态
		case LV_PULL_REFRESH:
			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			switchViewState(mlistViewState.LV_NORMAL);
			break;
		// 刷新状态
		case LV_RELEASE_REFRESH:
			mHeadView.setPadding(0, 0, 0, 0);
			switchViewState(mlistViewState.LV_LOADING);
			onRefresh();// 下拉刷新
			break;
		}

	}

	// 切换headview视图
	private void switchViewState(DListViewState state) {

		switch (state) {
		// 普通状态
		case LV_NORMAL: {
			mArrowImageView.clearAnimation();// 清除动画
			mArrowImageView.setImageResource(R.drawable.arrow);
		}
			break;
		// 下拉状态
		case LV_PULL_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);// 隐藏进度条
			mArrowImageView.setVisibility(View.VISIBLE);// 下拉图标
			mRefreshTextview.setText("下拉可以刷新");
			mArrowImageView.clearAnimation();// 清除动画

			// 是有可刷新状态（LV_RELEASE_REFRESH）转为这个状态才执行，其实就是你下拉后在上拉会执行.
			if (mBack) {
				mBack = false;
				mArrowImageView.clearAnimation();// 清除动画
				mArrowImageView.startAnimation(reverseAnimation);// 启动反转动画
			}
		}
			break;
		// 松开刷新状态
		case LV_RELEASE_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);// 隐藏进度条
			mArrowImageView.setVisibility(View.VISIBLE);// 显示下拉图标
			mRefreshTextview.setText("松开刷新数据");
			mArrowImageView.clearAnimation();// 清除动画
			mArrowImageView.startAnimation(animation);// 启动动画
		}
			break;
		// 加载状态
		case LV_LOADING: {
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_LOADING");
			mHeadProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mRefreshTextview.setText("正在刷新...");
		}
			break;
		default:
			return;
		}
		// 切记不要忘记时时更新状态。
		mlistViewState = state;

	}

	/***
	 * 下拉刷新
	 */
	private void onRefresh() {
		if (onRefreshLoadingMoreListener != null) {
			onRefreshLoadingMoreListener.onRefresh();
		}
	}

	/***
	 * 下拉刷新完毕
	 */
	public void onRefreshComplete() {
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);// 回归.
		switchViewState(mlistViewState.LV_NORMAL);//
		updateLoadMoreViewState(DListViewLoadingMore.LV_NORMAL); // 恢复正常状态
	}

	/***
	 * 点击加载更多
	 * 
	 * @param flag
	 *            数据是否已全部加载完毕
	 */
	public void onLoadMoreComplete(boolean flag) {
		if (flag) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_OVER);
		} else {
			updateLoadMoreViewState(DListViewLoadingMore.LV_NORMAL);
		}

	}

	/***
	 * 更新Footview视图
	 * 
	 * @param state
	 *            状态
	 */
	public void updateLoadMoreViewState(DListViewLoadingMore state) {
		switch (state) {
		// 普通状态
		case LV_NORMAL:
			mLoadMoreView.setVisibility(View.VISIBLE);
			mLoadingView.setVisibility(View.GONE);
			mLoadMoreTextView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setText("查看更多");
			break;
		// 加载中状态
		case LV_LOADING:
			mLoadingView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setVisibility(View.GONE);
			break;
		// 加载完毕状态
		case LV_OVER:
			mLoadingView.setVisibility(View.GONE);
			mLoadMoreTextView.setVisibility(View.VISIBLE);
//			mLoadMoreTextView.setText("没有更多数据了");
			// removeFooterView(mFootView);
			mLoadMoreView.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		loadingMoreState = state;
	}

	/***
	 * 设置自动上拉加载更多
	 */
	public void setAutoFetchMore(boolean flag) {
		isAutoMore = flag;
	};

	/***
	 * 设置下拉刷新功能
	 */
	public void setAutoPullRefresh(boolean flag) {
		isAutoRefresh = flag;
	};

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		//滚动到底部
		if (t+getHeight() >= computeVerticalScrollRange()) {
			if (isAutoMore && !loadingMoreState.equals(DListViewLoadingMore.LV_OVER)) {
				// 防止重复
				if (onRefreshLoadingMoreListener != null
						&& loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
					updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
					onRefreshLoadingMoreListener.onLoadMore();// 对外提供方法加载更多.
				}
			}
		}
		
	}

	/***
	 * 底部点击事件
	 */
	@Override
	public void onClick(View v) {
		// 防止重复点击
		if (onRefreshLoadingMoreListener != null
				&& loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
			onRefreshLoadingMoreListener.onLoadMore();// 对外提供方法加载更多.
		}

	}

	/***
	 * 自定义接口
	 */
	public interface OnRefreshLoadingMoreListener {
		/***
		 * // 下拉刷新执行
		 */
		public void onRefresh();

		/***
		 * 点击加载更多
		 */
		public void onLoadMore();

		/***
		 * 数据处理
		 */
		// public List<Object> handData();

	}

}
