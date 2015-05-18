package com.example.androidlibraryutils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

import com.example.androidlibraryutils.view.HaloToast;


public class BaseActivity extends ActionBarActivity {
	private ProgressDialog progressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   
    }
    /**
     * 初始化
     */
    protected void init() {
    	
		initViews();
		bindViews();

	}
    
    protected void initViews() {
		// TODO Auto-generated method stub

	}
    protected void bindViews() {
		// TODO Auto-generated method stub
    	
	}
    
    
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dismissLoadingDlg();
		ApplicationController.getInstance().cancelPendingRequests(getClass().getName());

	}
	
	
	/**
	 * Show loading dialog
	 * 
	 * @param sMsg
	 *            the message to display
	 */
	public void displayLoadingDlg(String sMsg) {
		if (isFinishing()) {
			return;
		}
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.setMessage(sMsg);
		} else {
			progressDlg = new ProgressDialog(this);
			progressDlg.setMessage(sMsg);
			progressDlg.setIndeterminate(true);
			progressDlg.setCancelable(true);
			progressDlg.show();
		}
	}
	public void displayLoadingDlgNocancel(String sMsg) {
		if (isFinishing()) {
			return;
		}
		if (progressDlg != null && progressDlg.isShowing()) {
			progressDlg.setMessage(sMsg);
		} else {
			progressDlg = new ProgressDialog(this);
			progressDlg.setMessage(sMsg);
			progressDlg.setIndeterminate(true);
			progressDlg.setCancelable(false);
			progressDlg.show();
			
		}
	}
	/**
	 * Show loading dialog
	 * 
	 * @param resId
	 *            message resId in string.xml to display
	 */
	public void displayLoadingDlg(int resId) {
		displayLoadingDlg(getString(resId));
	}
	/**
	 * 
	 * @param listener
	 */
	public void setOnDismissListener(OnCancelListener listener){
		if (progressDlg != null ){
			progressDlg.setOnCancelListener(listener);
		}
	}
	/**
	 * 
	 * @param listener
	 */
	public void setOnKeyListener(OnKeyListener listener){
		if (progressDlg != null )
			progressDlg.setOnKeyListener(listener);
	}
	/**
	 * Dismiss the loading dialog
	 */
	public void dismissLoadingDlg() {
		if (progressDlg != null && progressDlg.isShowing()){
			progressDlg.cancel();
			progressDlg.setCanceledOnTouchOutside(false);
		}
		
	}
	public void setCanceledOnTouchOutside(boolean b) {
		if (progressDlg != null && progressDlg.isShowing()){
			progressDlg.setCanceledOnTouchOutside(false);
		}
			
	}
	public void showToast(String msg) {
		if (!isFinishing()) {
			HaloToast.show(this, msg);
		}
	}
	/**
     * 程序是否在前台运行
     * @return
     */
    public boolean isAppOnForeground() { 
    	ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE); 
        // Returns a list of application processes that are running on the device 
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
        if (appProcesses == null) return false; 
        
        for (RunningAppProcessInfo appProcess : appProcesses) { 
            // The name of the process that this object is associated with. 
            if (appProcess.processName.equals(getPackageName()) 
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) { 
                return true; 
            } 
        } 
        
        return false; 
    } 
    /**
     * 是否联网
     * @return
     */
    public boolean checkNetworkInfo() {
		  ConnectivityManager conMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		  State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		  State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		  if (mobile == State.CONNECTED || mobile == State.CONNECTING){
			  return true;
		  }
		  if (wifi == State.CONNECTED || wifi == State.CONNECTING){
			  return true;
		  }
		  return false;
	}
    /**
     * 是否退出程序，显示“退出”还是“取消”
     * @param isIndex
     */
    public void dealNet(final boolean isIndex){
    	String negative;
    	if(isIndex){
    		negative = "退出";
    	}else{
    		negative = "取消";
    	}
    	if(!checkNetworkInfo()){
    		AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("提示");
			builder.setMessage("设备未接入互联网");
		
			builder.setPositiveButton("去设置网络", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					 Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
			         startActivity(intent);
				}
			});
			builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if(isIndex){
						BaseActivity.this.finish();
						android.os.Process.killProcess(android.os.Process.myPid());
					}
					dialog.dismiss();
				}
			});
			builder.show();
    	}
    }
    
    
 
}
