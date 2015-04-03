/**
 * Copyright (C) 2015 Damien Chazoule
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.doomy.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.preference.PreferenceManager;

import java.util.List;

public class CameraUtils {


    private static Boolean mPrefFlash;
    private static SharedPreferences mPreferences;

    public static Camera getCameraInstance() {
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        }
        catch (Exception e) {
		
        }
        return mCamera;
    }

    public static boolean isFlashSupported(Camera camera, Context context) {

        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (camera != null) {
            Camera.Parameters mParameters = camera.getParameters();

            if (mParameters.getFlashMode() == null) {
                mPrefFlash = mPreferences.edit().putBoolean("mPrefFlash", false).commit();
                return false;
            }

            List<String> mSupportedFlashModes = mParameters.getSupportedFlashModes();
            if (mSupportedFlashModes == null || mSupportedFlashModes.isEmpty() || mSupportedFlashModes.size() == 1 && mSupportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
                mPrefFlash = mPreferences.edit().putBoolean("mPrefFlash", false).commit();
                return false;
            }
        } else {
            mPrefFlash = mPreferences.edit().putBoolean("mPrefFlash", false).commit();
            return false;
        }

        mPrefFlash = mPreferences.edit().putBoolean("mPrefFlash", true).commit();
        return true;
    }
}
