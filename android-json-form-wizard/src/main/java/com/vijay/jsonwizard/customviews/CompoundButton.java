package com.vijay.jsonwizard.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.rey.material.drawable.RippleDrawable;
import com.rey.material.widget.RippleManager;

public class CompoundButton extends android.widget.CompoundButton {

    protected Drawable mButtonDrawable;
    private RippleManager mRippleManager = new RippleManager();

    public CompoundButton(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // a fix to reset paddingLeft attribute
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.padding,
                    android.R.attr.paddingLeft}, defStyleAttr, defStyleRes);

            if (!a.hasValue(0) && !a.hasValue(1)) {
                setPadding(0, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            }

            a.recycle();
        }

        setClickable(true);
        applyStyle(context, attrs, defStyleAttr, defStyleRes);
    }

    public void applyStyle(int resId) {
        applyStyle(getContext(), null, 0, resId);
    }

    private void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mRippleManager.onCreate(this, context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setBackgroundDrawable(Drawable drawable) {
        Drawable background = getBackground();
        if (background instanceof RippleDrawable && !(drawable instanceof RippleDrawable))
            ((RippleDrawable) background).setBackgroundDrawable(drawable);
        else
            super.setBackgroundDrawable(drawable);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        if (l.equals(mRippleManager))
            super.setOnClickListener(l);
        else {
            mRippleManager.setOnClickListener(l);
            setOnClickListener(mRippleManager);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        return mRippleManager.onTouchEvent(this, event) || result;
    }

    @Override
    public void setButtonDrawable(Drawable d) {
        mButtonDrawable = d;
        super.setButtonDrawable(d);
    }

    @Override
    public int getCompoundPaddingLeft() {
        int padding = super.getCompoundPaddingLeft();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return padding;

        if (mButtonDrawable != null)
            padding += mButtonDrawable.getIntrinsicWidth();

        return padding;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        mButtonDrawable = right;
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }
}