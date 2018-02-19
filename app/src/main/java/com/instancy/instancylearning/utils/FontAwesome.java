package com.instancy.instancylearning.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;


/**
 * Created by Upendranath on 9/18/2017 Working on Instancy-Playground-Android.
 */

public class FontAwesome extends android.support.v7.widget.AppCompatTextView {


    public FontAwesome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FontAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontAwesome(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "/fonts/fontawesome-webfont.ttf");
        setTypeface(tf);
    }
}
