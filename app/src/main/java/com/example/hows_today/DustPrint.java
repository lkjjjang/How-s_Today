package com.example.hows_today;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DustPrint {

    private final LinearLayout linearLayout;
    private final ImageView imageView;
    private final TextView textView;

    private final int COLOR_GOOD = R.drawable.back_good;
    private final int COLOR_NORMAL = R.drawable.back_normal;
    private final int COLOR_BAD = R.drawable.back_bad;

    private final int IMG_GOOD = R.drawable.good;
    private final int IMG_NORMAL = R.drawable.normal;
    private final int IMG_BAD = R.drawable.bad;

    private final String GOOD_STR = "좋음";
    private final String NORMAL_STR = "보통";
    private final String BAD_STR = "나쁨";

    private int grade;

    public DustPrint(String dustInfo, LinearLayout linearLayout, ImageView imageView, TextView textView) {
        this.linearLayout = linearLayout;
        this.imageView = imageView;
        this.textView = textView;
        setGrade(dustInfo);
    }

    public void print() {
        setLinearLayout();
        setImageView();
        setTextView();
    }

    private void setLinearLayout() {
        int result = 0;
        if (this.grade == 1) {
            result = this.COLOR_GOOD;
        } else if (this.grade == 2 || this.grade == 3) {
            result = this.COLOR_NORMAL;
        } else {
            result = this.COLOR_BAD;
        }

        this.linearLayout.setBackgroundResource(result);
    }

    private void setImageView() {
        int result = 0;
        if (this.grade == 1) {
            result = this.IMG_GOOD;
        } else if (this.grade == 2 || this.grade == 3) {
            result = this.IMG_NORMAL;
        } else {
            result = this.IMG_BAD;
        }

        this.imageView.setImageResource(result);
    }

    private void setTextView() {
        String result = "";
        if (this.grade == 1) {
            result = this.GOOD_STR;
        } else if (this.grade == 2 || this.grade == 3) {
            result = this.NORMAL_STR;
        } else {
            result = this.BAD_STR;
        }

        this.textView.setText(result);
    }

    private void setGrade(String grade) {
        this.grade = Integer.parseInt(grade);
    }
}
