package com.phat_plats.scanitfortheplanet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.phat_plats.scanitfortheplanet.network.ProductHandler;
import com.phat_plats.scanitfortheplanet.network.model.Product;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.search.model.QueryItem;
import com.phat_plats.scanitfortheplanet.views.HeaderView;
import com.phat_plats.scanitfortheplanet.views.TabPagerAdapter;

import java.net.URL;

public class ProductInfoActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private boolean isHideToolbarView = false;
    private HeaderView toolbarHeaderView;
    private HeaderView floatHeaderView;
    private FloatingActionButton fab;
    private ImageView image;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        init();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.bringToFront();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void init() {
        toolbarHeaderView = (HeaderView)findViewById(R.id.toolbar_header_view);
        floatHeaderView = (HeaderView)findViewById(R.id.float_header_view);
        fab = (FloatingActionButton)findViewById(R.id.product_fab);
        animateFab(-1);

        Bundle b = getIntent().getExtras();
        QueryItem data = (QueryItem)b.getSerializable("query_item");

        toolbarHeaderView.bindTo(data.name, "UPC " + data.upc);
        floatHeaderView.bindTo(data.name, "UPC " + data.upc);

        image = (ImageView)findViewById(R.id.header);

        ProductHandler.getProduct(data.upc, new Callback() {
            @Override
            public void run(boolean success, Object result) {
                if (success) {
                    Product product = (Product)result;
                    loadImage(product.imageURL);
                    Bundle b = new Bundle();
                    b.putSerializable("product", product);
                    viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), b));
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
        });
    }

    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        if(fab.getVisibility() != View.GONE) {
            ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            shrink.setDuration(150);     // animation duration in milliseconds
            shrink.setInterpolator(new DecelerateInterpolator());
            shrink.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (getFABDrawable(position) != -1) {
                        fab.setVisibility(View.VISIBLE);
                        fab.setImageDrawable(getResources().getDrawable(getFABDrawable(position), null));
                        // Scale up animation
                        ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        expand.setDuration(100);     // animation duration in milliseconds
                        expand.setInterpolator(new AccelerateInterpolator());
                        fab.startAnimation(expand);
                    } else {
                        fab.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fab.startAnimation(shrink);
        } else {
            fab.setVisibility(View.VISIBLE);
            fab.setImageDrawable(getResources().getDrawable(getFABDrawable(position), null));
            // Scale up animation
            ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            expand.setDuration(100);     // animation duration in milliseconds
            expand.setInterpolator(new AccelerateInterpolator());
            fab.startAnimation(expand);
        }
    }

    private int getFABDrawable(int position) {
        switch (position) {
            case 0:
                return -1;
            case 1:
                return R.drawable.ic_action_add;
            default:
                return -1;
        }
    }

    private void loadImage (String url) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected void onPostExecute(Bitmap bmp) {
                image.setImageBitmap(bmp);
                Palette.from(bmp).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int color = palette.getVibrantColor(getResources().getColor(R.color.background_material_light));
                        collapsingToolbar.setContentScrimColor(color);
                        appBarLayout.setBackgroundColor(color);
                        tabLayout.setTabTextColors(lighten(color, .4f), color);
                        tabLayout.setSelectedTabIndicatorColor(color);
                        ProductInfoActivity.this.getWindow().setStatusBarColor(color);
                        //fab.setBackgroundTintList(ColorStateList.valueOf(palette.getDarkVibrantColor(getResources().getColor(R.color.accent_material_light))));
                        findViewById(R.id.overlay).setBackgroundColor(palette.getDarkVibrantColor(Color.BLACK));
                    }
                });
            }

            @Override
            protected Bitmap doInBackground(String...params) {
                try {
                    URL url = new URL(params[0]);
                    return BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(url);
    }

    private int lighten(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage >= 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                this.finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
