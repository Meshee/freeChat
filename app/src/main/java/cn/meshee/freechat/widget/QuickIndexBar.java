package cn.meshee.freechat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import cn.meshee.freechat.R;

public class QuickIndexBar extends View {

    private Paint mPaint;

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());

    private static final String[] LETTERS = new String[] { "↑", "☆", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };

    private int mCellWidth;

    private float mCellHeight;

    private int mTouchIndex = -1;

    public interface OnLetterUpdateListener {

        void onLetterUpdate(String letter);

        void onLetterCancel();
    }

    private OnLetterUpdateListener mListener;

    public OnLetterUpdateListener getOnLetterUpdateListener() {
        return mListener;
    }

    public void setOnLetterUpdateListener(OnLetterUpdateListener listener) {
        mListener = listener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.side_bar));
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCellWidth = getMeasuredWidth();
        mCellHeight = getMeasuredHeight() * 1.0f / LETTERS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);
        for (int i = 0; i < LETTERS.length; i++) {
            String text = LETTERS[i];
            int x = (int) (mCellWidth / 2.0f - mPaint.measureText(text) / 2.0f);
            Rect bounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), bounds);
            int textHeight = bounds.height();
            int y = (int) (mCellHeight / 2.0f + textHeight / 2.0f + i * mCellHeight);
            canvas.drawText(text, x, y, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(LETTERS[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setBackgroundColor(getResources().getColor(R.color.side_bar_pressed));
                break;
            case MotionEvent.ACTION_MOVE:
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < LETTERS.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(LETTERS[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setBackgroundColor(getResources().getColor(R.color.side_bar_pressed));
                break;
            case MotionEvent.ACTION_UP:
                mTouchIndex = -1;
                if (mListener != null) {
                    mListener.onLetterCancel();
                }
                setBackgroundColor(Color.TRANSPARENT);
                break;
        }
        return true;
    }
}
