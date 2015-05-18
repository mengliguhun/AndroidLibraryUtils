package com.example.androidlibraryutils.view;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidlibraryutils.R;
import com.example.androidlibraryutils.utlis.GraphicUtils;

public class HaloToast {
	private static Toast sToast = null;
	public static void systemshow(Context c, String s) {
		Toast.makeText(c, s, Toast.LENGTH_SHORT).show();
	}
	public static void show(Context c, String s) {
		show(c, s, Toast.LENGTH_SHORT);
	}

	public static void show(Context c, int res) {
		show(c, res, Toast.LENGTH_SHORT);
	}

	public static void show(Context c, int res, int duration) {
		show(c, c.getString(res), duration);
	}

	public static void show(Context c, String s, int duration) {
		View layout = getToastView(c);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setVisibility(View.VISIBLE);
		text.setText(s);
		showToast(layout, duration);
	}

	private static View getToastView(Context c) {
		return LayoutInflater.from(c).inflate(R.layout.toast, null);
	}

	private static void showToast(View view, int duration) {
		showToast(view, duration, Gravity.CENTER, 0);
	}

	private static void showToast(View view, int duration, int gravity,
			int yOffset) {
		if (sToast == null) {
			sToast = new Toast(view.getContext());
		}
		sToast.setGravity(gravity, 0, yOffset);
		sToast.setDuration(duration);
		sToast.setView(view);
		sToast.show();
	}

	public static void showSuccess(Context c) {
		showSuccess(c, "");
	}

	public static void showSuccess(Context c, int res) {
		showSuccess(c, c.getString(res));
	}

	public static void showSuccess(Context c, String s) {
		showIcon(c, s, true);
	}

	private static void showIcon(Context c, String s, boolean success) {
		View layout = getToastView(c);
		ImageView iv = (ImageView) layout.findViewById(R.id.icon);
		iv.setVisibility(View.VISIBLE);
		iv.setImageResource(success ? R.drawable.toast_done: R.drawable.toast_error);
		if (!TextUtils.isEmpty(s)) {
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setVisibility(View.VISIBLE);
			text.setText(s);
		}
		showToast(layout, Toast.LENGTH_SHORT);
	}

	public static void showMessage(Context c, int res, int gravity, int yOffset) {
		showMessage(c, c.getString(res), gravity, yOffset);
	}

	public static void showMessage(Context c, String s, int gravity, int yOffset) {
		TextView layout = new TextView(c);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(lp);

		if (!TextUtils.isEmpty(s)) {
			layout.setText(s);
			layout.setTextSize(18);
			layout.setTextColor(Color.WHITE);
			layout.setGravity(Gravity.CENTER);
			layout.setBackgroundColor(GraphicUtils.getColor(0x696969));
			layout.setPadding(0, 10, 0, 10);
		}
		showToast(layout, Toast.LENGTH_LONG, Gravity.BOTTOM, yOffset);
	}

	public static void showFailed(Context c) {
		showFailed(c, "");
	}

	public static void showFailed(Context c, int res) {
		showFailed(c, c.getString(res));
	}

	public static void showFailed(Context c, String s) {
		if (s == null)
			return;
		showIcon(c, s, false);
	}

}
