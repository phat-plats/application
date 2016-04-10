package com.phat_plats.scanitfortheplanet.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phat_plats.scanitfortheplanet.R;


public class HeaderView extends LinearLayout{

    TextView title;
    TextView subTitle;


    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title = (TextView)findViewById(R.id.header_view_title);
        subTitle = (TextView)findViewById(R.id.header_view_sub_title);
    }

    public void bindTo(String title) {
        bindTo(title, "");
    }

    public void bindTo(String title, String subTitle) {
        hideOrSetText(this.title, title);
        hideOrSetText(this.subTitle, subTitle);
    }

    private void hideOrSetText(TextView tv, String text) {
        if (text == null || text.equals(""))
            tv.setVisibility(GONE);
        else
            tv.setText(text);
    }

    public void setScaleXTitle(float scaleXTitle) {
        title.setScaleX(scaleXTitle);
        title.setPivotX(0);
    }

    public void setScaleYTitle(float scaleYTitle) {
        title.setScaleY(scaleYTitle);
        title.setPivotY(30);
    }
}
