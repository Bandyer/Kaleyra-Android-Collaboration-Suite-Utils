package com.bandyer.demo_app;

import android.content.Context;
import android.graphics.Canvas;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.bandyer.android_common.Roundable;
import com.bandyer.android_common.RoundedView;


public class RoundedImageView extends AppCompatImageView implements RoundedView {

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setRoundClip(this, canvas);
        super.onDraw(canvas);
    }

    @Override
    public <T extends View & Roundable> void setRoundClip(T $receiver, Canvas canvas) {
        RoundedView.DefaultImpls.setRoundClip(this, this, canvas);
    }

    @Override
    public <T extends View & Roundable> void round(T $receiver, boolean rounded) {
        RoundedView.DefaultImpls.round(this, this, rounded);
    }

    @Override
    public <T extends View & Roundable> void setCornerRadius(T $receiver, float radius) {
        RoundedView.DefaultImpls.setCornerRadius(this, this, radius);
    }


    @Override
    public <T extends View & Roundable> void round(boolean rounded) {
        RoundedView.DefaultImpls.round(this, rounded);
    }

    @Override
    public <T extends View & Roundable> void setCornerRadius(float radius) {
        RoundedView.DefaultImpls.setCornerRadius(this, radius);
    }
}