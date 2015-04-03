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

    /**
     * Rename the barcode format name.
     *
     * @return The new name of the barcode format.
     */
    public static String renameFormat(String mFormat) {
        switch (mFormat) {
            case "AZTEC" :
                mFormat = "Aztec";
                break;
            case "UPC_A" :
                mFormat = "UPC A";
                break;
            case "UPC_E" :
                mFormat = "UPC E";
                break;
            case "EAN_8" :
                mFormat = "EAN 8";
                break;
            case "EAN_13" :
                mFormat = "EAN 13";
                break;
            case "RSS_14" :
                mFormat = "RSS 14";
                break;
            case "CODE_39" :
                mFormat = "Code 39";
                break;
            case "CODE_93" :
                mFormat = "Code 93";
                break;
            case "CODE_128" :
                mFormat = "Code 128";
                break;
            case "CODABAR" :
                mFormat = "Codabar";
                break;
            case "QR_CODE" :
                mFormat = "QR Code";
                break;
            case "DATA_MATRIX" :
                mFormat = "Data Matrix";
                break;
            case "PDF_417" :
                mFormat = "PDF 417";
                break;
        }
        return mFormat;
    }
}
