package com.uwaterloo.watodo.CustomizedView;
import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;

public class SquareCardView extends CardView {

    public SquareCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int newWidthMeasureSpec = heightMeasureSpec;
        super.onMeasure(newWidthMeasureSpec, heightMeasureSpec);
    }

}
