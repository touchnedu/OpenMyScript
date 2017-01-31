package com.touchnedu.gradea.studya.math;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakRefHandler extends Handler {
	private final WeakReference<IOnHandlerMessage> mHandlerActivity;
	
	public WeakRefHandler(IOnHandlerMessage activity) {
		mHandlerActivity = new WeakReference<IOnHandlerMessage>(activity);
	}
	
	@Override
	public void dispatchMessage(Message msg) {
		super.dispatchMessage(msg);
		IOnHandlerMessage activity = (IOnHandlerMessage)mHandlerActivity.get();
		if(activity == null)
			return;
		activity.dispatchMessage(msg);
	}

}
/** WeakRefHandler를 사용하는 이유 :
 * Main Thread에서 Handler를 사용 시 
 * 'handler should be static, else it is prone to memory leaks'
 * 와 유사한 lint warning이 발생한다.
 * 
 * Handler는 Activity 또는 Context를 참조하게 되는데 MessageQueue에 해당 Handler가
 * 참조하는 소비하지 않은 Message가 남아있는 경우 Activity가 종료되어도 Handler는
 * GC가 일어나지 않는다.
 * Handler가 GC가 일어나지 않으며 Handler가 참조하는 Activity나 Context도 GC가
 * 일어나지 않고 이것이 반복되면서 Memory Leak을 발생시킬 수 있다.
 * 따라서 WeakRefHandler를 사용하여 Activity(Context)를 참조함으로써 간접적인
 * 참조를 통해 Activity가 GC가 일어날 수 있도록 허용하고, Activity가 null인 경우
 * Handler는 아무런 일도 하지 않도록 한다.
 * */
