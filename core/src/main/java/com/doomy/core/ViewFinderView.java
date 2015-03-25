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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewFinderView extends View {

    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;

    private static final float LANDSCAPE_WIDTH_RATIO = 5f/8;
    private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
    private static final int LANDSCAPE_MAX_FRAME_WIDTH = (int) (1920 * LANDSCAPE_WIDTH_RATIO);
    private static final int LANDSCAPE_MAX_FRAME_HEIGHT = (int) (1080 * LANDSCAPE_HEIGHT_RATIO);

    private static final float PORTRAIT_WIDTH_RATIO = 7f/8;
    private static final float PORTRAIT_HEIGHT_RATIO = 3f/8;
    private static final int PORTRAIT_MAX_FRAME_WIDTH = (int) (1080 * PORTRAIT_WIDTH_RATIO);
    private static final int PORTRAIT_MAX_FRAME_HEIGHT = (int) (1920 * PORTRAIT_HEIGHT_RATIO);

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int mScannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 100;

    public ViewFinderView(Context context) {
        super(context);
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(mFramingRect == null) {
            return;
        }

        drawMask(canvas);
        drawBorder(canvas);
    }

    public void drawMask(Canvas canvas) {
        Paint mPaint = new Paint();
        Resources mResources = getResources();
        mPaint.setColor(mResources.getColor(R.color.mask));

        int mWidth = canvas.getWidth();
        int mHeight = canvas.getHeight();

        canvas.drawRect(0, 0, mWidth, mFramingRect.top, mPaint);
        canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mPaint);
        canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, mWidth, mFramingRect.bottom + 1, mPaint);
        canvas.drawRect(0, mFramingRect.bottom + 1, mWidth, mHeight, mPaint);
    }

    public void drawBorder(Canvas canvas) {
        Paint mPaint = new Paint();
        Resources mResources = getResources();
        mPaint.setColor(mResources.getColor(R.color.border));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mResources.getInteger(R.integer.width));
        int mLineLength = mResources.getInteger(R.integer.length);

        canvas.drawLine(mFramingRect.left + 50, mFramingRect.top + 45, mFramingRect.left + 50, mFramingRect.top + 50 + mLineLength, mPaint);
        canvas.drawLine(mFramingRect.left + 45, mFramingRect.top + 50, mFramingRect.left + 50 + mLineLength, mFramingRect.top + 50, mPaint);

        canvas.drawLine(mFramingRect.left + 50, mFramingRect.bottom - 45, mFramingRect.left + 50, mFramingRect.bottom - 50 - mLineLength, mPaint);
        canvas.drawLine(mFramingRect.left + 45, mFramingRect.bottom - 50, mFramingRect.left + 50 + mLineLength, mFramingRect.bottom - 50, mPaint);

        canvas.drawLine(mFramingRect.right - 50, mFramingRect.top + 45, mFramingRect.right - 50, mFramingRect.top + 50 + mLineLength, mPaint);
        canvas.drawLine(mFramingRect.right - 45, mFramingRect.top + 50, mFramingRect.right - 50 - mLineLength, mFramingRect.top + 50, mPaint);

        canvas.drawLine(mFramingRect.right - 50, mFramingRect.bottom - 45, mFramingRect.right - 50, mFramingRect.bottom - 50 - mLineLength, mPaint);
        canvas.drawLine(mFramingRect.right - 45, mFramingRect.bottom - 50, mFramingRect.right - 50 - mLineLength, mFramingRect.bottom - 50, mPaint);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point mViewResolution = new Point(getWidth(), getHeight());
        if (mViewResolution == null) {
            return;
        }
        int mWidth;
        int mHeight;
        int mOrientation = DisplayUtils.getScreenOrientation(getContext());

        if(mOrientation != Configuration.ORIENTATION_PORTRAIT) {
            mWidth = findDesiredDimensionInRange(LANDSCAPE_WIDTH_RATIO, mViewResolution.x, MIN_FRAME_WIDTH, LANDSCAPE_MAX_FRAME_WIDTH);
            mHeight = findDesiredDimensionInRange(LANDSCAPE_HEIGHT_RATIO, mViewResolution.y, MIN_FRAME_HEIGHT, LANDSCAPE_MAX_FRAME_HEIGHT);
        } else {
            mWidth = findDesiredDimensionInRange(PORTRAIT_WIDTH_RATIO, mViewResolution.x, MIN_FRAME_WIDTH, PORTRAIT_MAX_FRAME_WIDTH);
            mHeight = findDesiredDimensionInRange(PORTRAIT_HEIGHT_RATIO, mViewResolution.y, MIN_FRAME_HEIGHT, PORTRAIT_MAX_FRAME_HEIGHT);
        }

        int mLeftOffSet = (mViewResolution.x - mWidth) / 2;
        int mTopOffSet = (mViewResolution.y - mHeight) / 2;
        mFramingRect = new Rect(mLeftOffSet, mTopOffSet, mLeftOffSet + mWidth, mTopOffSet + mHeight);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int min, int max) {
        int mDimension = (int) (ratio * resolution);
        if (mDimension < min) {
            return min;
        }
        if (mDimension > max) {
            return max;
        }
        return mDimension;
    }
}
