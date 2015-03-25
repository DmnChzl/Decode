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

package com.doomy.zbar;

import java.util.List;
import java.util.ArrayList;

import net.sourceforge.zbar.Symbol;

public class BarcodeFormat {

    private int mId;
    private String mName;

    public static final BarcodeFormat NONE = new BarcodeFormat(Symbol.NONE, "None");
    public static final BarcodeFormat PARTIAL = new BarcodeFormat(Symbol.PARTIAL, "Partial");
    public static final BarcodeFormat UPCA = new BarcodeFormat(Symbol.UPCA, "UPC A");
    public static final BarcodeFormat UPCE = new BarcodeFormat(Symbol.UPCE, "UPC E");
    public static final BarcodeFormat EAN8 = new BarcodeFormat(Symbol.EAN8, "EAN 8");
    public static final BarcodeFormat EAN13 = new BarcodeFormat(Symbol.EAN13, "EAN 13");
    public static final BarcodeFormat ISBN10 = new BarcodeFormat(Symbol.ISBN10, "ISBN 10");
    public static final BarcodeFormat ISBN13 = new BarcodeFormat(Symbol.ISBN13, "ISBN 13");
    public static final BarcodeFormat QRCODE = new BarcodeFormat(Symbol.QRCODE, "QR Code");
    public static final BarcodeFormat CODE39 = new BarcodeFormat(Symbol.CODE39, "Code 39");
    public static final BarcodeFormat CODE93 = new BarcodeFormat(Symbol.CODE93, "Code 93");
    public static final BarcodeFormat CODE128 = new BarcodeFormat(Symbol.CODE128, "Code 128");
    public static final BarcodeFormat I25 = new BarcodeFormat(Symbol.I25, "I25");
    public static final BarcodeFormat CODABAR = new BarcodeFormat(Symbol.CODABAR, "Codabar");
    public static final BarcodeFormat DATABAR = new BarcodeFormat(Symbol.DATABAR, "Databar");
    public static final BarcodeFormat DATABAR_EXP = new BarcodeFormat(Symbol.DATABAR_EXP, "Databar Expanded");
    public static final BarcodeFormat PDF417 = new BarcodeFormat(Symbol.PDF417, "PDF 417");

    public static final List<BarcodeFormat> ALL_FORMATS = new ArrayList<BarcodeFormat>();

    static {
        ALL_FORMATS.add(BarcodeFormat.PARTIAL);
        ALL_FORMATS.add(BarcodeFormat.UPCA);
        ALL_FORMATS.add(BarcodeFormat.UPCE);
        ALL_FORMATS.add(BarcodeFormat.EAN8);
        ALL_FORMATS.add(BarcodeFormat.EAN13);
        ALL_FORMATS.add(BarcodeFormat.ISBN10);
        ALL_FORMATS.add(BarcodeFormat.ISBN13);
        ALL_FORMATS.add(BarcodeFormat.QRCODE);
        ALL_FORMATS.add(BarcodeFormat.CODE39);
        ALL_FORMATS.add(BarcodeFormat.CODE93);
        ALL_FORMATS.add(BarcodeFormat.CODE128);
        ALL_FORMATS.add(BarcodeFormat.I25);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.DATABAR);
        ALL_FORMATS.add(BarcodeFormat.DATABAR_EXP);
        ALL_FORMATS.add(BarcodeFormat.PDF417);
    }

    public BarcodeFormat(int id, String name) {
        mId = id;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public static BarcodeFormat getFormatById(int id) {
        for(BarcodeFormat mFormat : ALL_FORMATS) {
            if(mFormat.getId() == id) {
                return mFormat;
            }
        }
        return BarcodeFormat.NONE;
    }
}