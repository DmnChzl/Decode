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
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback  {

    private Camera mCamera;
    private CameraPreview mPreview;
    private ViewFinderView mViewFinderView;
    private Rect mFramingRectInPreview;

    public BarcodeScannerView(Context context) {
        super(context);
        setupLayout();
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setupLayout();
    }

    public void setupLayout() {
        mPreview = new CameraPreview(getContext());
        mViewFinderView = new ViewFinderView(getContext());
        addView(mPreview);
        addView(mViewFinderView);
    }

    public void startCamera() {
        mCamera = CameraUtils.getCameraInstance();
        if(mCamera != null) {
            mViewFinderView.setupViewFinder();
            mPreview.setCamera(mCamera, this);
            mPreview.initCameraPreview();
        }
    }

    public void stopCamera() {
        if(mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
    }

    public synchronized Rect getFramingRectInPreview(int width, int height) {
        if (mFramingRectInPreview == null) {
            Rect mFramingRect = mViewFinderView.getFramingRect();
            if (mFramingRect == null) {
                return null;
            }
            Rect mRect = new Rect(mFramingRect);
            Point mScreenResolution = DisplayUtils.getScreenResolution(getContext());
            Point mCameraResolution = new Point(width, height);

            if (mCameraResolution == null || mScreenResolution == null) {
                return null;
            }

            mRect.left = mRect.left * mCameraResolution.x / mScreenResolution.x;
            mRect.right = mRect.right * mCameraResolution.x / mScreenResolution.x;
            mRect.top = mRect.top * mCameraResolution.y / mScreenResolution.y;
            mRect.bottom = mRect.bottom * mCameraResolution.y / mScreenResolution.y;

            mFramingRectInPreview = mRect;
        }
        return mFramingRectInPreview;
    }

    public void setFlash(boolean mode) {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera, getContext())) {
            Camera.Parameters mParameters = mCamera.getParameters();
            if(mode) {
                if(mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if(mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(mParameters);
        }
    }

    public boolean getFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera, getContext())) {
            Camera.Parameters mParameters = mCamera.getParameters();
            if(mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void toggleFlash() {
        if(mCamera != null && CameraUtils.isFlashSupported(mCamera, getContext())) {
            Camera.Parameters mParameters = mCamera.getParameters();
            if(mParameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            } else {
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            }
            mCamera.setParameters(mParameters);
        }
    }

    public void setAutoFocus(boolean state) {
        if(mPreview != null) {
            mPreview.setAutoFocus(state);
        }
    }
}
