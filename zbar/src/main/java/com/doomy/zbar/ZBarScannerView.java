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

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import com.doomy.core.BarcodeScannerView;
import com.doomy.core.DisplayUtils;

public class ZBarScannerView extends BarcodeScannerView {

    public interface ResultHandler {
        public void handleResult(Result rawResult);
    }

    static {
        System.loadLibrary("iconv");
    }

    private ImageScanner mScanner;
    private List<BarcodeFormat> mFormats;
    private ResultHandler mResultHandler;

    public ZBarScannerView(Context context) {
        super(context);
        setupScanner();
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupScanner();
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        setupScanner();
    }

    public void setResultHandler(ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if(mFormats == null) {
            return BarcodeFormat.ALL_FORMATS;
        }
        return mFormats;
    }

    public void setupScanner() {
        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for(BarcodeFormat format : getFormats()) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters mParameters = camera.getParameters();
        Camera.Size mSize = mParameters.getPreviewSize();
        int mWidth = mSize.width;
        int mHeight = mSize.height;

        if(DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            byte[] mRotatedData = new byte[data.length];
            for (int y = 0; y < mHeight; y++) {
                for (int x = 0; x < mWidth; x++)
                    mRotatedData[x * mHeight + mHeight - y - 1] = data[x + y * mWidth];
            }
            int mTemp = mWidth;
            mWidth = mHeight;
            mHeight = mTemp;
            data = mRotatedData;
        }

        Image mBarcode = new Image(mWidth, mHeight, "Y800");
        mBarcode.setData(data);

        int mResult = mScanner.scanImage(mBarcode);

        if (mResult != 0) {
            stopCamera();
            if(mResultHandler != null) {
                SymbolSet mSymbolSet = mScanner.getResults();
                Result mRawResult = new Result();
                for (Symbol mSymbol : mSymbolSet) {
                    String mSymbolData = mSymbol.getData();
                    if (!TextUtils.isEmpty(mSymbolData)) {
                        mRawResult.setContents(mSymbolData);
                        mRawResult.setBarcodeFormat(BarcodeFormat.getFormatById(mSymbol.getType()));
                        break;
                    }
                }
                mResultHandler.handleResult(mRawResult);
            }
        } else {
            camera.setOneShotPreviewCallback(this);
        }
    }
}