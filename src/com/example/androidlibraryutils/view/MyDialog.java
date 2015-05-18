package com.example.androidlibraryutils.view;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MyDialog extends Dialog{
	
	
	public MyDialog(Context context, int theme) {
		super(context, theme);
		
	}
	

	/*
	 * @activity dialog显示的载体
	 * @dialog 要显示的dialog
	 * @high,width dialog的长宽占屏幕的比例
	 * @alpha dialog的alpha值
	 * @dimAmount 背景的alpha值
	 */
	public void paramsCotroller(Activity activity ,MyDialog dialog,float height ,float width ,float alpha ,float dimAmount){
		Window dialogWindow = dialog.getWindow();
	    WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * height); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * width); // 宽度设置为屏幕的0.65
        p.alpha = alpha;                //设置本身透明度
        p.dimAmount = dimAmount;                //设置黑暗度
        dialogWindow.setAttributes(p);
       // getWindow().setGravity(Gravity.RIGHT);                 //设置靠右对齐
	}

	

}
