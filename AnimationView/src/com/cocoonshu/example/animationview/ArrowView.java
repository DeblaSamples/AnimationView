package com.cocoonshu.example.animationview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ArrowView extends View {

    private static final float Factor = 1E-1F;
    private static final float Error  = 1E-2F;
    
    private Drawable mDblArrow             = null;
    private PointF   mDestinationTranslate = new PointF();
    private PointF   mCurrentTranslate     = new PointF();
    private float    mDestinationRotate    = 0;
    private float    mCurrentRotate        = 0;
    
    public ArrowView(Context context) {
        this(context, null);
    }
    
    public ArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArrowView, defStyleAttr, 0);
        int loopSize = typedArray.getIndexCount();
        for (int i = 0; i < loopSize; i++) {
            int attributeKey = typedArray.getIndex(i);
            switch (attributeKey) {
            case R.styleable.ArrowView_arrow:
                mDblArrow = typedArray.getDrawable(attributeKey);
                if (mDblArrow != null) {
                    mDblArrow.setBounds(0, 0, mDblArrow.getIntrinsicWidth(), mDblArrow.getIntrinsicHeight());
                }
                break;
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int backgroundWidth  = 0;
        int backgroundHeight = 0;
        int widthSpecMode    = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode   = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize    = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize   = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth    = 0;
        int measuredHeight   = 0;

        Drawable background = getBackground();
        if (background != null) {
            backgroundWidth = background.getIntrinsicWidth();
            backgroundHeight = background.getIntrinsicHeight();
        }
        
        // Width
        switch (widthSpecMode) {
        case MeasureSpec.UNSPECIFIED:
            measuredWidth = backgroundWidth;
            break;
        case MeasureSpec.AT_MOST:
            measuredWidth = backgroundWidth < widthSpecSize ? backgroundWidth : widthSpecSize;
            break;
        case MeasureSpec.EXACTLY:
            measuredWidth = widthSpecSize;
            break;
        }
        
        // Height
        switch (heightSpecMode) {
        case MeasureSpec.UNSPECIFIED:
            measuredHeight = backgroundHeight;
            break;
        case MeasureSpec.AT_MOST:
            measuredHeight = backgroundHeight < heightSpecSize ? backgroundHeight : heightSpecSize;
            break;
        case MeasureSpec.EXACTLY:
            measuredHeight = heightSpecSize;
            break;
        }
        
        setMeasuredDimension(measuredWidth, measuredHeight);
        mDestinationTranslate.x = measuredWidth * 0.5f;
        mDestinationTranslate.y = measuredHeight * 0.5f;
        mCurrentTranslate.x     = mDestinationTranslate.x;
        mCurrentTranslate.y     = mDestinationTranslate.y;        
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        boolean hasMoreFrames = false;
        mCurrentTranslate.x = mCurrentTranslate.x + (mDestinationTranslate.x - mCurrentTranslate.x) * Factor;
        mCurrentTranslate.y = mCurrentTranslate.y + (mDestinationTranslate.y - mCurrentTranslate.y) * Factor;
        mCurrentRotate      = mCurrentRotate + (mDestinationRotate - mCurrentRotate) * Factor;
        hasMoreFrames |= Math.abs(mDestinationTranslate.x - mCurrentTranslate.x) > Error;
        hasMoreFrames |= Math.abs(mDestinationTranslate.y - mCurrentTranslate.y) > Error;
        hasMoreFrames |= Math.abs(mDestinationRotate - mCurrentRotate) > Error;
        
        if (mDblArrow != null) {
            float arrowWidth      = mDblArrow.getIntrinsicWidth();
            float arrowHeight     = mDblArrow.getIntrinsicHeight();
            float halfArrowWidth  = arrowWidth * 0.5f;
            float halfArrowHeight = arrowHeight * 0.5f;
            mDblArrow.setBounds(0, 0, (int)arrowWidth, (int)arrowHeight);

            canvas.translate(mCurrentTranslate.x - halfArrowWidth, mCurrentTranslate.y - halfArrowHeight);
            canvas.rotate(mCurrentRotate + 90, halfArrowWidth, halfArrowHeight);
            mDblArrow.draw(canvas);
        }
        
        if (hasMoreFrames) {
            invalidate();
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            float  currentX = event.getX();
            float  currentY = event.getY();
            double angle    = Math.atan2(currentY - mCurrentTranslate.y, currentX - mCurrentTranslate.x);

            mDestinationRotate      = (float) Math.toDegrees(angle);
            mDestinationTranslate.x = currentX;
            mDestinationTranslate.y = currentY;
            invalidate();
            break;
        }
        return true;
    }
}
