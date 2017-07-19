/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.tiger.analogclocktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;


/**
 * A class that draws an analog clock face with information about the current
 * time.
 */
public class AnalogClock extends View implements Runnable {

    private static final String TAG = "AnalogClock";

    private Drawable mClockDial;
    //private Drawable mClockPoint;
    private Drawable mClockHour;
    private Drawable mClockSecond;
    private Drawable mClockMinute;
    private boolean mRegisterReceiver = false;
    private Paint mPaint;

    private int mCenterX = 0;
    private int mCenterY = 0;

    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock, defStyle, 0);
        mClockDial = a.getDrawable(R.styleable.AnalogClock_clock_dial);
        int dialWidth = mClockDial.getIntrinsicWidth();
        mCenterX = dialWidth / 2;
        int dialHeight = mClockDial.getIntrinsicHeight();
        mCenterY = dialHeight / 2;
        mClockSecond = a.getDrawable(R.styleable.AnalogClock_clock_s);
        mClockMinute = a.getDrawable(R.styleable.AnalogClock_clock_m);
        mClockHour = a.getDrawable(R.styleable.AnalogClock_clock_h);
        //mClockPoint = a.getDrawable(R.styleable.AnalogClock_clock_p);
        Rect rect = mClockDial.getBounds();
        a.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    @Override
    public void run() {
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "width : " + mClockDial.getIntrinsicWidth() + " height : " + mClockDial.getIntrinsicHeight());
        setMeasuredDimension(mClockDial.getIntrinsicWidth() + 10, mClockDial.getIntrinsicHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPic(canvas, mPaint, 0, mClockDial);
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR);
        int second = cal.get(Calendar.SECOND);

        float secondRadian = second / 60.0f * 360.0f;
        float minuteRadian = (minute + second / 60.0f) / 60.0f * 360.0f;
        float hourRadian = (hour + minute / 60.0f + second / 3600.0f) / 12.0f * 360.0f;

        drawPic(canvas, mPaint, hourRadian + 90, mClockHour);
        drawPic(canvas, mPaint, minuteRadian - 90, mClockMinute);
        drawPic(canvas, mPaint, secondRadian - 90, mClockSecond);
        //drawPic(canvas, mPaint, 0, mClockPoint);
        mHandler.sendEmptyMessageDelayed(EVENT_UPDATE_TIME, 1000);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerTimeReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterTimeReciever();
    }

    private void drawPic(Canvas canvas, Paint paint, float radian, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        canvas.save();
        canvas.translate(((float) (getHeight() - mClockDial.getIntrinsicWidth())) / 2, 0);
        canvas.rotate(radian, mCenterX, mCenterY);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void registerTimeReceiver() {
        if (!mRegisterReceiver) {
            mRegisterReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mTimeReceiver, filter);
        }
    }

    private void unregisterTimeReciever() {
        if (mRegisterReceiver) {
            mRegisterReceiver = false;
            getContext().unregisterReceiver(mTimeReceiver);
        }
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, " action : " + action);
            mHandler.removeMessages(EVENT_UPDATE_TIME);
            mHandler.sendEmptyMessage(EVENT_UPDATE_TIME);
        }
    };

    public static final int EVENT_UPDATE_TIME = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_TIME:
                    AnalogClock.this.invalidate();
                    break;
            }
        }
    };
}
