package cn.meshee.freechat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {

    private int mDuration = 100;

    private int mProgress = 30;

    private Paint mPaint = new Paint();

    private RectF mRectF = new RectF();

    private int mBackgroundColor = Color.LTGRAY;

    private int mPrimaryColor = Color.parseColor("#6DCAEC");

    private float mStrokeWidth = 10F;

    public interface OnProgressChangeListener {

        void onChange(int duration, int progress, float rate);
    }

    private OnProgressChangeListener mOnChangeListener;

    public void setOnProgressChangeListener(OnProgressChangeListener l) {
        mOnChangeListener = l;
    }

    public CircularProgressBar(Context context) {
        super(context);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        mDuration = max;
    }

    public int getMax() {
        return mDuration;
    }

    public void setProgress(int progress) {
        if (progress > mDuration) {
            progress = mDuration;
        }
        mProgress = progress;
        if (mOnChangeListener != null) {
            mOnChangeListener.onChange(mDuration, progress, getRateOfProgress());
        }
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setPrimaryColor(int color) {
        mPrimaryColor = color;
    }

    public void setCircleWidth(float width) {
        mStrokeWidth = width;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfWidth = getWidth() / 2;
        int halfHeight = getHeight() / 2;
        int radius = halfWidth < halfHeight ? halfWidth : halfHeight;
        float halfStrokeWidth = mStrokeWidth / 2;
        mPaint.setColor(mBackgroundColor);
        mPaint.setDither(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(halfWidth, halfHeight, radius - halfStrokeWidth, mPaint);
        mPaint.setColor(mPrimaryColor);
        mRectF.top = halfHeight - radius + halfStrokeWidth;
        mRectF.bottom = halfHeight + radius - halfStrokeWidth;
        mRectF.left = halfWidth - radius + halfStrokeWidth;
        mRectF.right = halfWidth + radius - halfStrokeWidth;
        canvas.drawArc(mRectF, -90, getRateOfProgress() * 360, false, mPaint);
        canvas.save();
    }

    private float getRateOfProgress() {
        return (float) mProgress / mDuration;
    }
}
