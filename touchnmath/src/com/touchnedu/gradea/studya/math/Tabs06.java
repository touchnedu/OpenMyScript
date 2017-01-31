package com.touchnedu.gradea.studya.math;

import com.touchnedu.gradea.studya.math.modules.BookCaseActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("ValidFragment")
public class Tabs06 extends Fragment {
	Context mContext;

	public Tabs06(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
																										Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_tabs06, container, false);
		ImageView startBtn = (ImageView)view.findViewById(R.id.start_app);
		startBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BookCaseActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				/** Fragment일 때는 (Activity)mContext가 아닌 getActivity()를 쓴다.
				 * mContext가 ApplicationContext일 경우 Activity로 형변환이 불가능하다.
				 * */
				getActivity().finish();
			}
		});

		return view;
	}

}