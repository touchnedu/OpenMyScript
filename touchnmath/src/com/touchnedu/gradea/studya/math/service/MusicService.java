package com.touchnedu.gradea.studya.math.service;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicService {
	private Context context;
	private MediaPlayer mp;
	private static MusicService musicService;
	
	private MusicService(Context context) {
		if(this.context == null) {
			this.context = context;
		}
	}
	
	public static MusicService getInstance(Context con) {
		if(musicService == null) {
			musicService = new MusicService(con);
		}
		return musicService;
	}
	
	public void initMusic() {
		mp = MediaPlayer.create(context, R.raw.backgroundmusic);
		mp.setLooping(true);
	}
	
	public void startMusic() {
		if(mp != null)
			mp.start();
		
	}
	
	public void restartMusic() {
		initMusic();
		startMusic();
	}
	
	public void stopMusic() {
		if(mp != null)
			mp.stop();
	}
	
	public void pauseMusic() {
		isMpOn();
		if(mp != null)
			mp.pause();
	}
	
	public void resumeMusic() {
		if(SharedPreferencesManager.getInstance().getBgSound() && mp != null) 
			startMusic();
	}
	
	public void isMpOn() {
//		Log.i("prjt", "mp = " + mp);
	}
	
}
