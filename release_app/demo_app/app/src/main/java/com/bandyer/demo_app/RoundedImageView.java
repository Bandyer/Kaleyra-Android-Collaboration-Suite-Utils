package com.bandyer.demo_app;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

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
    public <T extends View & Roundable> void setRoundClip(@NonNull T receiver, Canvas canvas) {
        RoundedView.DefaultImpls.setRoundClip(this, receiver, canvas);
    }

    @Override
    public <T extends View & Roundable> void round(@NonNull T receiver, boolean rounded) {
        RoundedView.DefaultImpls.round(this, receiver, rounded);
    }

    @Override
    public <T extends View & Roundable> void setCornerRadius(@NonNull T receiver, float radius) {
        RoundedView.DefaultImpls.setCornerRadius(this, receiver, radius);
    }


    @Override
    public <T extends View & Roundable> void round(boolean rounded) {
        RoundedView.DefaultImpls.round(this, rounded);
    }

    @Override
    public <T extends View & Roundable> void setCornerRadius(float radius) {
        RoundedView.DefaultImpls.setCornerRadius(this, radius);
    }

    @Override
    public <T extends View & Roundable> float radius(@NonNull T radius) {
        return RoundedView.DefaultImpls.radius(this, radius);
    }

    @Override
    public <T extends View & Roundable> boolean rounded(@NonNull T rounded) {
        return RoundedView.DefaultImpls.rounded(this, rounded);
    }
}