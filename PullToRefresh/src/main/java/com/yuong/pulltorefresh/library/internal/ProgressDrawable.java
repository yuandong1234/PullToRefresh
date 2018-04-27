package com.yuong.pulltorefresh.library.internal;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 旋转动画
 * Created by SCWANG on 2017/6/16.
 */
@SuppressWarnings("WeakerAccess")
public class ProgressDrawable extends Drawable{

    private int mWidth = 0;
    private int mHeight = 0;
    private Path mPath = new Path();
    private Paint mPaint = new Paint();

    public ProgressDrawable() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffaaaaaa);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Drawable drawable = ProgressDrawable.this;
        final Rect bounds = drawable.getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        final int r = Math.max(1, width / 20);

        if (mWidth != width || mHeight != height) {
            mPath.reset();
            mPath.addCircle(width - r, height / 2, r, Path.Direction.CW);
            mPath.addRect(width - 5 * r, height / 2 - r, width - r, height / 2 + r, Path.Direction.CW);
            mPath.addCircle(width - 5 * r, height / 2, r, Path.Direction.CW);
            mWidth = width;
            mHeight = height;
        }

        canvas.save();
        canvas.rotate(0, (width) / 2, (height) / 2);
        for (int i = 0; i < 12; i++) {
            mPaint.setAlpha((i+5) * 0x11);
            canvas.rotate(30, (width) / 2, (height) / 2);
            canvas.drawPath(mPath, mPaint);
        }
        canvas.restore();
    }
}
