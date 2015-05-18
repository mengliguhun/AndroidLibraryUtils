package com.example.androidlibraryutils.utlis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.androidlibraryutils.R;



public class UtiNetIO {

	public static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");

	public static boolean isNetworkAvailable(Activity mActivity) {
		Context context = mActivity.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {

			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if(info[i].getState() == null){
						continue;
					}
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean isNetworkAvailableC(Context mContext) {
		if(mContext == null)
			return false;
		
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {

			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if(info[i].getState() == null){
						continue;
					}
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public static boolean isNetworkAvailableS(Service mService) {
		Context context = mService.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void showNoNetDialog(final Activity activity) {
		if (!isNetworkAvailable(activity)) {
			new AlertDialog.Builder(activity).setCancelable(false).setTitle(
					R.string.alert_dialog_tip).setMessage(
					R.string.unavailablenetwork).setPositiveButton(
					R.string.alert_dialog_button_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							activity.finish();
						}
					}).show();
			return;
		}
	}

	public static void showNoNetDialog2(final Activity activity) {
		new AlertDialog.Builder(activity).setCancelable(false).setTitle(
				R.string.alert_dialog_tip).setMessage(
				R.string.unavailablenetwork).setPositiveButton(
				R.string.alert_dialog_button_ok, null).show();
	}

	/**
	 * 监测网络是否存在wifi状态
	 * 
	 * @param paramActivity
	 * @return
	 */
	public static boolean isNetworkWifi(Context context) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
				.name().equals("CONNECTED")) {
			flag = true;
		}
		return flag;
	}

	public static boolean showNoNetDialogForTask(final Activity activity,
			final AsyncTask<?, ?, ?> task) {
		boolean f = false;
		if (!isNetworkAvailable(activity)) {
			if (task != null) {
				task.cancel(true);
			}
			new AlertDialog.Builder(activity).setCancelable(true).setTitle(
					R.string.alert_dialog_tip).setMessage(
					R.string.unavailablenetwork).setPositiveButton(
					R.string.alert_dialog_button_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					}).show();
			f = true;
		}
		return f;
	}
	
	public static boolean showNoNetDialogForTaskEsc(final Activity activity,
			final AsyncTask<?, ?, ?> task) {
		boolean f = false;
		if (!isNetworkAvailable(activity)) {
			if (task != null) {
				task.cancel(true);
			}
			new AlertDialog.Builder(activity).setCancelable(true).setTitle(
					R.string.alert_dialog_tip).setMessage(
					R.string.unavailablenetwork).setPositiveButton(
					R.string.alert_dialog_button_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							activity.finish();
						}
					}).show();
			f = true;
		}
		return f;
	}
}
