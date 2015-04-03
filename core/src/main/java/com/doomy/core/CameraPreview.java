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
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Camera mCamera;
    private Handler mAutoFocusHandler;
    private boolean mPreviewing = true;
    private boolean mAutoFocus = true;
    private boolean mSurfaceCreated = false;
    private Camera.PreviewCallback mPreviewCallback;

    public CameraPreview(Context context) {
        super(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCamera(Camera camera, Camera.PreviewCallback previewCallback) {
        mCamera = camera;
        mPreviewCallback = previewCallback;
        mAutoFocusHandler = new Handler();
    }

    public void initCameraPreview() {
        if(mCamera != null) {
            getHolder().addCallback(this);
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            if(mPreviewing) {
                requestLayout();
            } else {
                startCameraPreview();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if(surfaceHolder.getSurface() == null) {
            return;
        }
        stopCameraPreview();
        startCameraPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = false;
        stopCameraPreview();
    }

    public void startCameraPreview() {
        if(mCamera != null) {
            try {
                mPreviewing = true;
                setupCameraParameters();
                mCamera.setPreviewDisplay(getHolder());
                mCamera.setDisplayOrientation(getDisplayOrientation());
                mCamera.setOneShotPreviewCallback(mPreviewCallback);
                mCamera.startPreview();
                if(mAutoFocus) {
                    mCamera.autoFocus(autoFocusCB);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void stopCameraPreview() {
        if(mCamera != null) {
            try {
                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setOneShotPreviewCallback(null);
                mCamera.stopPreview();
            } catch(Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public void setupCameraParameters() {
        Camera.Size mOptimalSize = getOptimalPreviewSize();
        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setPreviewSize(mOptimalSize.width, mOptimalSize.height);
        mCamera.setParameters(mParameters);
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo mInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, mInfo);
        WindowManager mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();

        int mRotation = mDisplay.getRotation();
        int mDegrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0: mDegrees = 0; 
				break;
            case Surface.ROTATION_90: mDegrees = 90; 
				break;
            case Surface.ROTATION_180: mDegrees = 180; 
				break;
            case Surface.ROTATION_270: mDegrees = 270; 
				break;
        }

        int mResult;
        if (mInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mResult = (mInfo.orientation + mDegrees) % 360;
            mResult = (360 - mResult) % 360;
        } else {
            mResult = (mInfo.orientation - mDegrees + 360) % 360;
        }
        return mResult;
    }

    private Camera.Size getOptimalPreviewSize() {
        if(mCamera == null) {
            return null;
        }

        List<Camera.Size> mSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Point mScreenResolution = DisplayUtils.getScreenResolution(getContext());
        int mWidth = mScreenResolution.x;
        int mHeight = mScreenResolution.y;
        if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            mWidth = mScreenResolution.y;
            mHeight = mScreenResolution.x;
        }


        final double ASPECT_TOLERANCE = 0.1;
        double mTargetRatio = (double) mWidth / mHeight;
        if (mSizes == null) return null;

        Camera.Size mOptimalSize = null;
        double mMinDiff = Double.MAX_VALUE;

        int targetHeight = mHeight;

        for (Camera.Size mSize : mSizes) {
            double ratio = (double) mSize.width / mSize.height;
            if (Math.abs(ratio - mTargetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(mSize.height - targetHeight) < mMinDiff) {
                mOptimalSize = mSize;
                mMinDiff = Math.abs(mSize.height - targetHeight);
            }
        }

        if (mOptimalSize == null) {
            mMinDiff = Double.MAX_VALUE;
            for (Camera.Size mSize : mSizes) {
                if (Math.abs(mSize.height - targetHeight) < mMinDiff) {
                    mOptimalSize = mSize;
                    mMinDiff = Math.abs(mSize.height - targetHeight);
                }
            }
        }
        return mOptimalSize;
    }

    public void setAutoFocus(boolean state) {
        if(mCamera != null && mPreviewing) {
            if(state == mAutoFocus) {
                return;
            }
            mAutoFocus = state;
            if(mAutoFocus) {
                Log.v(TAG, "Starting autoFocus");
                mCamera.autoFocus(autoFocusCB);
            } else {
                Log.v(TAG, "Stopping autoFocus");
                mCamera.cancelAutoFocus();
            }
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if(mCamera != null && mPreviewing && mAutoFocus && mSurfaceCreated) {
                mCamera.autoFocus(autoFocusCB);
            }
        }
    };

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}