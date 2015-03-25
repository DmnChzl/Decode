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
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DisplayUtils {

    public static Point getScreenResolution(Context context) {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        Point mScreenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            mDisplay.getSize(mScreenResolution);
        } else {
            mScreenResolution.set(mDisplay.getWidth(), mDisplay.getHeight());
        }

        return mScreenResolution;
    }

    public static int getScreenOrientation(Context context)
    {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();

        int mOrientation = Configuration.ORIENTATION_UNDEFINED;
        if (mDisplay.getWidth()==mDisplay.getHeight()){
            mOrientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (mDisplay.getWidth() < mDisplay.getHeight()){
                mOrientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                mOrientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return mOrientation;
    }
}
