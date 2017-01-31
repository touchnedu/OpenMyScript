package com.touchnedu.gradea.studya.math.util;

import android.os.Build;
import android.util.Log;

public class DeviceUtils {
	public static boolean checkIfDeviceIsGalaxyS2() {
		String DeviceName = Build.MODEL;
		
		if (DeviceName.contains("SHW-M250L")
				|| DeviceName.contains("SHW-M250S")
				|| DeviceName.contains("SHW-M250K")) {
			Log.i("test", "GalaxyS2Check() : GalaxyS2 ! ! ! ! ! ! !");

			return true;
		}
		return false;
	}

}
