package com.touchnedu.gradea.studya.math.service;

import java.util.ArrayList;

import com.touchnedu.gradea.studya.math.R;
import com.touchnedu.gradea.studya.math.util.SharedPreferencesManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class PlaySound {
	private static PlaySound playSound;
	private SoundPool soundPool;
	private Context context;
	private int /*backgroundMusic,*/ writeSound, rightSound, wrongSound, finalSound,
							/*backgroundIndex,*/ writeIndex, rightIndex, wrongIndex, finalIndex;
	private ArrayList<Integer> soundId;
	
	private PlaySound(Context context) {
		if(this.context == null) {
			this.context = context; 
		}
		initSound();
	}
	
	public static PlaySound getInstance(Context con) {
		if(playSound == null) {
			playSound = new PlaySound(con);
		}
		return playSound;
		
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void initSound() {
		soundId = new ArrayList<Integer>();
		soundId.add(R.raw.backgroundmusic);
		soundId.add(R.raw.write01);
		soundId.add(R.raw.dingdong01);
		soundId.add(R.raw.ddang01);
    soundId.add(R.raw.dingdongdeng01);
		
		/** parameter : maxStream, streamType, srcQuality */
		if(soundPool == null) {
			if(android.os.Build.VERSION.SDK_INT >= 21) {
				soundPool = new SoundPool.Builder().setMaxStreams(5).build();
			} else { 
				soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			}
		} 
		
//		backgroundMusic = soundPool.load(context, soundId.get(0), 1);
		writeSound = soundPool.load(context, soundId.get(1), 1);
		rightSound = soundPool.load(context, soundId.get(2), 1);
		wrongSound = soundPool.load(context, soundId.get(3), 1);
		finalSound = soundPool.load(context, soundId.get(4), 1);
		
	}
	
	/** 좀 더 최적화할 수 없을까? */
	public void playSound(int index) {
		if(SharedPreferencesManager.getInstance().efxSound) {
			switch(index) {
			case 0:
//			backgroundIndex = soundPool.play(backgroundMusic, 1f, 1f, 1, -1, 1);
				break;
			case 1:
				writeIndex = soundPool.play(writeSound, 1f, 1f, 1, -1, 1);
				break;
			case 2:
				rightIndex = soundPool.play(rightSound, 1f, 1f, 1, 0, 1);
				break;
			case 3:
				wrongIndex = soundPool.play(wrongSound, 1f, 1f, 1, 0, 1);
				break;
			case 4:
				finalIndex = soundPool.play(finalSound, 1f, 1f, 1, 0, 1);
				break;
			}
			
		}
		
	}
	
	public void stopSound(int index) {
		/** play 시 리턴되는 값을 변수를 stop()의 파라미터로 사용해야 한다. */
		switch(index) {
		case 0:
//			soundPool.stop(backgroundIndex);
			break;
		case 1:
			soundPool.stop(writeIndex);
			break;
		case 2:
			soundPool.stop(rightIndex);
			break;
		case 3:
			soundPool.stop(wrongIndex);
			break;
		case 4:
			soundPool.stop(finalIndex);
			break;
		}
		
	}
	
}
