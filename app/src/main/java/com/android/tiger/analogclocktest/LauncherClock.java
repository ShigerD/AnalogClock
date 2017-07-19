package com.android.tiger.analogclocktest;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class LauncherClock extends RelativeLayout implements OnClickListener {


    private AnalogClock mAnalogClock;
    private Context mContext;

    public LauncherClock(Context context) {
        this(context, null);
    }

    public LauncherClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public LauncherClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setUpview();
    }

    private void setUpview() {
        mAnalogClock = (AnalogClock) findViewById(R.id.analogclock);
        mAnalogClock.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.analogclock:
                startDateAndTime();
                break;
        }
    }

    private void startDateAndTime() {
        Intent intent = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
        getContext().startActivity(intent);
    }
}
