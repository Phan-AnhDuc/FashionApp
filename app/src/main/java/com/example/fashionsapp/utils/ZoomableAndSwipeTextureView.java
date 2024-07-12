package com.example.fashionsapp.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;

import com.example.fashionsapp.R;

public class ZoomableAndSwipeTextureView extends TextureView {

    private static final String SUPERSTATE_KEY = "superState";
    private static final String MIN_SCALE_KEY = "minScale";
    private static final String MAX_SCALE_KEY = "maxScale";

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mMode = NONE;

    private Context mContext;

    private float mMinScale = 1f;
    private float mMaxScale = 5f;
    private float mSaveScale = 1f;

    private GestureDetector mGestureDetector;
    private ISwipeTouchListener mSwipeTouchListener;

    private Matrix mMatrix = new Matrix();
    private ScaleGestureDetector mScaleDetector;
    private float[] m;

    private PointF last = new PointF();
    private PointF start = new PointF();
    private float right, bottom;


    public ZoomableAndSwipeTextureView(Context context) {
        super(context);
        mContext = context;
        initView(null);
    }

    public ZoomableAndSwipeTextureView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }

    public ZoomableAndSwipeTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView(attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPERSTATE_KEY, super.onSaveInstanceState());
        bundle.putFloat(MIN_SCALE_KEY, mMinScale);
        bundle.putFloat(MAX_SCALE_KEY, mMaxScale);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mMinScale = bundle.getFloat(MIN_SCALE_KEY);
            mMinScale = bundle.getFloat(MAX_SCALE_KEY);
            state = bundle.getParcelable(SUPERSTATE_KEY);
        }
        super.onRestoreInstanceState(state);
    }

    private void initView(AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ZoomableTextureView,
                0, 0);
        try {
            mMinScale = a.getFloat(R.styleable.ZoomableTextureView_minScale, mMinScale);
            mMaxScale = a.getFloat(R.styleable.ZoomableTextureView_maxScale, mMaxScale);
        } finally {
            a.recycle();
        }

        setOnTouchListener(new ZoomOnTouchListeners());
    }

    public void setMinScale(float scale) {
        if (scale < 1.0f || scale > mMaxScale)
            throw new RuntimeException("minScale can't be lower than 1 or larger than maxScale(" + mMaxScale + ")");
        else mMinScale = scale;
    }

    public void setMaxScale(float scale) {
        if (scale < 1.0f || scale < mMinScale)
            throw new RuntimeException("maxScale can't be lower than 1 or minScale(" + mMinScale + ")");
        else mMaxScale = scale;
    }

    public void setSwipeListener(Context ctx, ISwipeTouchListener listener) {
        mGestureDetector = new GestureDetector(ctx, new GestureListener());
        mSwipeTouchListener = listener;
    }

    private class ScaleGestureDetectorNew extends ScaleGestureDetector {

        public ScaleGestureDetectorNew(Context context, OnScaleGestureListener listener) {
            super(context, listener);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return mGestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
        }
    }

    private class ZoomOnTouchListeners implements OnTouchListener {
        public ZoomOnTouchListeners() {
            super();
            m = new float[9];
            mScaleDetector = new ScaleGestureDetectorNew(mContext, new ScaleListener());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            mScaleDetector.onTouchEvent(motionEvent);

            mMatrix.getValues(m);
            float x = m[Matrix.MTRANS_X];
            float y = m[Matrix.MTRANS_Y];
            PointF curr = new PointF(motionEvent.getX(), motionEvent.getY());

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    last.set(motionEvent.getX(), motionEvent.getY());
                    start.set(last);
                    mMode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_UP:
                    mMode = NONE;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    last.set(motionEvent.getX(), motionEvent.getY());
                    start.set(last);
                    mMode = ZOOM;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mMode == ZOOM || (mMode == DRAG && mSaveScale > mMinScale)) {
                        float deltaX = curr.x - last.x;// x difference
                        float deltaY = curr.y - last.y;// y difference
                        if (y + deltaY > 0)
                            deltaY = -y;
                        else if (y + deltaY < -bottom)
                            deltaY = -(y + bottom);

                        if (x + deltaX > 0)
                            deltaX = -x;
                        else if (x + deltaX < -right)
                            deltaX = -(x + right);
                        mMatrix.postTranslate(deltaX, deltaY);
                        last.set(curr.x, curr.y);
                    }
                    break;
            }
            ZoomableAndSwipeTextureView.this.setTransform(mMatrix);
            ZoomableAndSwipeTextureView.this.invalidate();
            return true;
        }

        private class ScaleListener extends ScaleGestureDetectorNew.SimpleOnScaleGestureListener {

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mMode = ZOOM;
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float mScaleFactor = detector.getScaleFactor();
                float origScale = mSaveScale;
                mSaveScale *= mScaleFactor;
                if (mSaveScale > mMaxScale) {
                    mSaveScale = mMaxScale;
                    mScaleFactor = mMaxScale / origScale;
                } else if (mSaveScale < mMinScale) {
                    mSaveScale = mMinScale;
                    mScaleFactor = mMinScale / origScale;
                }
                right = getWidth() * mSaveScale - getWidth();
                bottom = getHeight() * mSaveScale - getHeight();
                if (0 <= getWidth() || 0 <= getHeight()) {
                    mMatrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
                    if (mScaleFactor < 1) {
                        mMatrix.getValues(m);
                        float x = m[Matrix.MTRANS_X];
                        float y = m[Matrix.MTRANS_Y];
                        if (mScaleFactor < 1) {
                            if (0 < getWidth()) {
                                if (y < -bottom)
                                    mMatrix.postTranslate(0, -(y + bottom));
                                else if (y > 0)
                                    mMatrix.postTranslate(0, -y);
                            } else {
                                if (x < -right)
                                    mMatrix.postTranslate(-(x + right), 0);
                                else if (x > 0)
                                    mMatrix.postTranslate(-x, 0);
                            }
                        }
                    }
                } else {
                    mMatrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
                    mMatrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (mScaleFactor < 1) {
                        if (x < -right)
                            mMatrix.postTranslate(-(x + right), 0);
                        else if (x > 0)
                            mMatrix.postTranslate(-x, 0);
                        if (y < -bottom)
                            mMatrix.postTranslate(0, -(y + bottom));
                        else if (y > 0)
                            mMatrix.postTranslate(0, -y);
                    }
                }
                return true;
            }
        }
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 10;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mSwipeTouchListener.onSingleTapUp(e);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mSaveScale != mMinScale) {
                return false;
            }
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            mSwipeTouchListener.onSwipeRight();
                        } else {
                            mSwipeTouchListener.onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        mSwipeTouchListener.onSwipeBottom();
                    } else {
                        mSwipeTouchListener.onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public interface ISwipeTouchListener {
        void onSwipeRight();

        void onSwipeLeft();

        void onSwipeTop();

        void onSwipeBottom();

        void onSingleTapUp(MotionEvent event);
    }
}

