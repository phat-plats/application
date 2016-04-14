package com.phat_plats.scanitfortheplanet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.phat_plats.scanitfortheplanet.views.SoftKeyboardLsnedLayout;
import com.phat_plats.scanitfortheplanet.views.anim.ExpandAnimation;
import com.phat_plats.scanitfortheplanet.views.anim.MarginAnimation;

public class OptionsActivity extends ActionBarActivity {

    private static int RC_BARCODE_CAPTURE = 10231;
    private static final int COLLAPSE_DURATION = 150;  // animation duration in milliseconds

    private ExpandAnimation expand;
    private ExpandAnimation collapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        final ViewGroup layout_context = (ViewGroup)findViewById(R.id.collapsing_layout);

        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });

        //setup animations
        final ScaleAnimation expand_fab =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        expand_fab.setDuration(COLLAPSE_DURATION);
        expand_fab.setInterpolator(new AccelerateInterpolator());
        expand_fab.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        expand = new ExpandAnimation(layout_context, 0, 3);
        collapse = new ExpandAnimation(layout_context, 3, 0);
        expand.setDuration(COLLAPSE_DURATION);
        collapse.setDuration(COLLAPSE_DURATION);
        expand.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.startAnimation(expand_fab);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        final EditText searchbox = (EditText)findViewById(R.id.product_search);
        searchbox.clearFocus();

        final ViewGroup searchWrapper = (ViewGroup)findViewById(R.id.search_wrapper);
        final float startMargin = ((RelativeLayout.LayoutParams)searchWrapper.getLayoutParams()).leftMargin;
        final Animation grow = new MarginAnimation(searchWrapper, startMargin, 0);
        final Animation thin = new MarginAnimation(searchWrapper, 0, startMargin);
        thin.setDuration(COLLAPSE_DURATION);
        grow.setDuration(COLLAPSE_DURATION);
        // done with animations

        SoftKeyboardLsnedLayout layout = (SoftKeyboardLsnedLayout) findViewById(R.id.search_layout);
        layout.addSoftKeyboardLsner(new SoftKeyboardLsnedLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {
                layout_context.startAnimation(collapse);
                searchbox.setText("");
                searchWrapper.startAnimation(grow);
                fab.setVisibility(View.GONE);
            }

            @Override
            public void onSoftKeyboardHide() {
                layout_context.startAnimation(expand);
                searchWrapper.startAnimation(thin);
            }
        });
    }

    private void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    findProductFromUPC(barcode.displayValue);
                }
            } else {
                Toast.makeText(OptionsActivity.this, "The barcode reader had an unexpected error",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void findProductFromUPC(String value) {
        Toast.makeText(OptionsActivity.this, "UPC: " + value,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
