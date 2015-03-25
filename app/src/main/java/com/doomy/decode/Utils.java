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

package com.doomy.decode;

import android.os.Build;

public class Utils {

    /**
     * Check the version of device.
     *
     * @return The style theme of MainActivity.
     */
    public static int setThemeVersion() {

        int mTheme;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTheme = R.style.MaterialTheme;
        } else {
            mTheme = R.style.HoloTheme;
        }
        return mTheme;
    }

    /**
     * Check the version of device.
     *
     * @return The style of AlertDialog.
     */
    public static int setThemeDialog() {

        int mTheme;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTheme = R.style.MaterialDialog;
        } else {
            mTheme = R.style.HoloDialog;
        }
        return mTheme;
    }
}
