package com.touchnedu.gradea.studya.math.modules;

import com.touchnedu.gradea.studya.math.OptionActivity;
import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.TutorialMenuActivity;
import com.touchnedu.gradea.studya.math.service.BaseActivityManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class SlideMenuController implements OnClickListener, 
																													OnItemClickListener {
	public static final int MENU_ETC = 0;
	public static final int MENU_GRADE = 1;
	public static final int MENU_OPTION = 5;
	public static final int MAIN_ACTIVITY = 99;
	public static int DEFAULT_STATE = 1;
	public static int TEMP_STATE = 1;
	public static boolean DRAWER_STATE = false;
	private DrawerLayout mDrawerLayout;
	private ListView customListView;
	private CustomListViewAdapter adapter;
	private ImageView drawerOpenBtn, drawerCloseBtn, homepageBtn;
	private View listViewHeader;
	private Context context;
	private Activity activity;
	private WebViewBookCase wvbc;
	private BaseActivityManager am = BaseActivityManager.getInstance();
	
	public SlideMenuController(Context context) {
		this.context = context;
		activity = (Activity)context;
	}
	
	public void initMenu() {
		listViewHeader = 
						activity.getLayoutInflater().inflate(R.layout.listview_item_header, 
								 		(ViewGroup)activity.findViewById(R.id.listVwHeader), false);
		
		adapter = new CustomListViewAdapter();
		customListView = (ListView)activity.findViewById(R.id.left_drawer);
		customListView.setAdapter(adapter);
		
		customListView.addHeaderView(listViewHeader, null, false);
		customListView.setOnItemClickListener(this);
		
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_tutorial),
																							"터치앤매쓰 소개");
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_grade), 
																							"학년 선택");
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_chapter), 
																							"단원 목차");
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_share), 
																							"친구에게 추천하기");
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_review), 
																							"스토어에 리뷰 남기기");
		adapter.addItem(ContextCompat.getDrawable(context, 
																							R.drawable.option_option), 
																							"설정");
		
		homepageBtn = (ImageView)activity.findViewById(R.id.open_homepage);
		drawerOpenBtn = (ImageView)activity.findViewById(R.id.drawer_open_btn);
		drawerOpenBtn.setVisibility(View.VISIBLE);
		drawerCloseBtn = (ImageView)activity.findViewById(R.id.drawer_close);
		drawerOpenBtn.setOnClickListener(this);
		drawerCloseBtn.setOnClickListener(this);
		homepageBtn.setOnClickListener(this);
		
		mDrawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);
		
		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		
		// 제스쳐로(swipe)로 메뉴 열기 기능 끄기
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		// 다시 켜기
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		
//		setGradeItems();
		
	}

	@Override
	public void onItemClick(AdapterView<?> par, View v,	int pos, long id) {
		CustomListViewItem item = (CustomListViewItem)par.getItemAtPosition(pos);
		item.getTitle();
		goToMenu(id);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.drawer_open_btn:
			openDrawer();
			break;
			
		case R.id.drawer_close:
			closeDrawer();
			break;
			
		// Open Homepage after touch LOGO
		case R.id.open_homepage:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(context.getString(R.string.homepage_url));
			intent.setData(uri);
			activity.startActivity(intent);
			break;
			
		}
		
	}
	
	private void goToMenu(long id) {
		switch((int)id) {
		case 0:
			closeDrawer();
			Intent tutorialIntent = new Intent(context, TutorialMenuActivity.class);
			tutorialIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
			activity.startActivity(tutorialIntent);
			break;
		case 1:
			// 뒤로 갈 수 있으면 처리
			wvbc = WebViewBookCase.getInstance();
			if(wvbc.canGoBack()) {
				closeDrawer();
				wvbc.goBack();
				wvbc.setTitle(context.getResources().getString(R.string.navi_section1));
			} else {
				setGradeItems();
			}
			break;
		case 2:
			selectChapter();
			break;
		case 3:
			closeDrawer();
			new ShareApplication(context).shareApp();
			break;
		case 4:
			closeDrawer();
			writeReview();
			break;
		case 5:
			if(DEFAULT_STATE != MENU_OPTION) {
				closeDrawer();
				Intent optionIntent = new Intent(context, OptionActivity.class);
				optionIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				activity.startActivity(optionIntent);
			}
			break;
			
		}
	}
	
	private void writeReview() {
		final String uri = "https://play.google.com/store/apps/details?"
																				+ "id=com.touchnedu.gradea.studya.math";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		activity.startActivity(intent);
	}

	private void setGradeItems() {
		switch(DEFAULT_STATE) {
		case MAIN_ACTIVITY:
			activity.finish();
			activity.startActivity(new Intent(context, BookCaseActivity.class));
			break;
			
		case MENU_GRADE:
			break;
			
		case MENU_OPTION:
			activity.finish();
			break;
			
		case MENU_ETC:
			if(TEMP_STATE == 1) {
				activity.finish();
			} else {
				am.finishAllActivity();
				activity.startActivity(new Intent(context, BookCaseActivity.class));
			}
			break;
			
		default:
			am.finishAllActivity();
			activity.startActivity(new Intent(context, BookCaseActivity.class));
			break;
		}
	}
	
	private void selectChapter() {
		if(DEFAULT_STATE == MAIN_ACTIVITY) {
			closeDrawer();     
			activity.finish();
		}
	}
	
	private void openDrawer() {
		DRAWER_STATE = true;
		mDrawerLayout.openDrawer(customListView);
		activity.invalidateOptionsMenu();
	}
	
	public void closeDrawer() {
		DRAWER_STATE = false;
		mDrawerLayout.closeDrawers();
		activity.invalidateOptionsMenu();
	}
	
	public void controlDrawer() {
		if(DRAWER_STATE) 
			closeDrawer();
	}
	
//	public void setGestureOpen(boolean b) {
//		if(!b)
//			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//		else
//			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//	}
	
//	public boolean isDrawerOpened() {
//		return mDrawerLayout.isDrawerOpen(GravityCompat.START);
//	}
	
}
