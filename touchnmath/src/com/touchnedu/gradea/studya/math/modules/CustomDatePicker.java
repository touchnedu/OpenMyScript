package com.touchnedu.gradea.studya.math.modules;

import java.lang.reflect.Field;
import java.util.Calendar;

import com.touchnedu.gradea.studya.math.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;

public class CustomDatePicker {
	private static Context mContext;
	
	public static void showDatePicker(final Context context) {
		mContext = context;
		
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		
		final Dialog birthDialog = new Dialog(context);
		birthDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		birthDialog.setContentView(R.layout.dialog_birth);
		
		final NumberPicker np = 
										(NumberPicker)birthDialog.findViewById(R.id.birthdayPicker);
		Button okBtn 		 = (Button)birthDialog.findViewById(R.id.birthday_btn_ok);
		Button cancelBtn = (Button)birthDialog.findViewById(R.id.birthday_btn_cancel);
		
		np.setMinValue(year - 100);
		np.setMaxValue(year - 1);
		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		setDividerColor(np);
		np.setWrapSelectorWheel(false);
		np.setValue(year - 16);
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) { }
		});
		
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateDate(String.valueOf(np.getValue()));
				birthDialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				birthDialog.dismiss();
			}
		});
		birthDialog.show();
		
	}
	
	private static void setDividerColor(NumberPicker picker) {
		Field[] pickerFields = NumberPicker.class.getDeclaredFields();
		for(Field pf : pickerFields) {
			if(pf.getName().equals("mSelectionDivider")) {
				pf.setAccessible(true);
				try {
					// 두 번째 파라미터는 Divider Color인데 안 먹는다. 
					pf.set(pickerFields, mContext.getResources()
																			 .getDrawable(R.drawable.divider_line_01));
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch(Resources.NotFoundException e) {
					e.printStackTrace();
				} catch(IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	private static void updateDate(String year) {
		Activity activity = (Activity)mContext;
		Button button = (Button)activity.findViewById(R.id.join_birth);
		button.setText(year);
	}
	
}
