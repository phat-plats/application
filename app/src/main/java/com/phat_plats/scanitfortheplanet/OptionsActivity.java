package com.phat_plats.scanitfortheplanet;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.phat_plats.scanitfortheplanet.network.LoginHandler;
import com.phat_plats.scanitfortheplanet.network.ProductHandler;
import com.phat_plats.scanitfortheplanet.network.util.Callback;
import com.phat_plats.scanitfortheplanet.search.adapter.SearchAdapter;
import com.phat_plats.scanitfortheplanet.search.model.QueryItem;
import com.phat_plats.scanitfortheplanet.search.model.ResultItem;
import com.phat_plats.scanitfortheplanet.search.util.RecyclerViewOnItemClickListener;
import com.phat_plats.scanitfortheplanet.views.SoftKeyboardLsnedLayout;
import com.phat_plats.scanitfortheplanet.views.anim.ExpandAnimation;
import com.phat_plats.scanitfortheplanet.views.anim.MarginAnimation;

import java.util.ArrayList;
import java.util.List;

public class OptionsActivity extends ActionBarActivity implements PopupMenu.OnMenuItemClickListener{

    public static final int RC_BARCODE_CAPTURE = 10231;
    private static final int COLLAPSE_DURATION = 150;  // animation duration in milliseconds

    private ExpandAnimation expand;
    private ExpandAnimation collapse;

    // CONSTANTS
    private static final String TAG = "SearchActivity";
    public static final int VOICE_RECOGNITION_CODE = 1;

    // UI ELEMENTS
    private RecyclerView searchResultList;
    private EditText searchInput;
    private RelativeLayout voiceInput;
    private ImageView micIcon;

    private boolean isExpanded = false;
//    private QueryItem query;
    private String search_query;
    private SearchAdapter adapter;
    private List<ResultItem> suggestions;
    private ViewGroup searchWrapper;
    private EditText searchbox;
    private FloatingActionButton fab;
    private ViewGroup layout_context;
    private MarginAnimation grow;
    private MarginAnimation thin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        layout_context = (ViewGroup)findViewById(R.id.collapsing_layout);

        fab = (FloatingActionButton)findViewById(R.id.fab);
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

        searchWrapper = (ViewGroup)findViewById(R.id.cs_header);
        searchWrapper.bringToFront();

        searchbox = (EditText)findViewById(R.id.product_search);
        searchbox.clearFocus();

        final float startMargin = getResources().getDimension(R.dimen.search_box_margin);
        grow = new MarginAnimation(searchWrapper, startMargin, 0);
        thin = new MarginAnimation(searchWrapper, 0, startMargin);
        thin.setDuration(COLLAPSE_DURATION);
        grow.setDuration(COLLAPSE_DURATION);
        // done with animations

        searchbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onCollapse();
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        });

        SoftKeyboardLsnedLayout layout = (SoftKeyboardLsnedLayout) findViewById(R.id.search_layout);
        layout.addSoftKeyboardLsner(new SoftKeyboardLsnedLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {}
            @Override
            public void onSoftKeyboardHide() {
                onExpand();
            }
        });

        initSearch();
        initMenu();
    }

    @Override
    public void onBackPressed() {
        if(isExpanded) {
            searchbox.clearFocus();
            onExpand();
        } else {
            super.onBackPressed();
        }
    }

    private void onExpand() {
        layout_context.startAnimation(expand);
        searchWrapper.startAnimation(thin);
        searchbox.clearFocus();
        searchbox.setText("");
        isExpanded = false;
    }

    private void onCollapse() {
        layout_context.startAnimation(collapse);
        searchWrapper.startAnimation(grow);
        fab.setVisibility(View.GONE);
        isExpanded = true;
    }

    private void initMenu() {
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(OptionsActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(OptionsActivity.this);
                inflater.inflate(R.menu.menu_options, popup.getMenu());
                if(LoginHandler.currentUser != null) {
                    popup.getMenu().findItem(R.id.login).setVisible(false);
                    popup.getMenu().findItem(R.id.signed).setTitle("Signed in as " + LoginHandler.currentUser);
                    popup.getMenu().findItem(R.id.signed).setEnabled(false);
                } else {
                    popup.getMenu().findItem(R.id.signed).setVisible(false);
                    popup.getMenu().findItem(R.id.logout).setVisible(false);
                }
                popup.show();
            }
        });
    }

    private void gotoProductPage(QueryItem value) {
        Intent i = new Intent(OptionsActivity.this, ProductInfoActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("query_item", value);
        i.putExtras(b);
        this.startActivity(i);
    }

    private void initSearch() {
        this.search_query = "";
        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        this.searchInput = (EditText) this.findViewById(R.id.product_search);
        this.voiceInput = (RelativeLayout) this.findViewById(R.id.custombar_mic_wrapper);
        this.micIcon = (ImageView) this.findViewById(R.id.custombar_mic);
        this.micIcon.setSelected(Boolean.FALSE);

        // Initialize result list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        // init RecyclerView adapter
        suggestions = new ArrayList<>();
        adapter = new SearchAdapter(suggestions);

        searchResultList.setAdapter(adapter);

        this.searchInput.setMaxLines(1);

        implementSearchTextListener();
        implementVoiceInputListener();
        implementResultListOnItemClickListener();
    }

    // Listeners implementation ____________________________________________________________________
    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    fillRecommendList();
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!"".equals(searchInput.getText().toString())) {
                    suggestions.clear();
                    search_query = searchInput.getText().toString();
                    fillRecommendList();
                    setClearTextIcon();
                } else {
                    suggestions.clear();
                    adapter.notifyDataSetChanged();
                    setMicIcon();
                }
            }

            // DO NOTHING
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // DO NOTHING
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fillRecommendList() {
        getSearchResults();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VOICE_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(text.get(0));
                    searchInput.requestFocus();
                }
                break;
            }
            case RC_BARCODE_CAPTURE: {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    searchInput.setText(barcode.displayValue);
                    searchInput.requestFocus();
                }
                break;
            }
        }
    }

    // Implements speech-to-text
    private void implementVoiceInputListener () {
        this.voiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (micIcon.isSelected()) {
                    searchInput.setText("");
                    search_query = "";
                    micIcon.setSelected(Boolean.FALSE);
                    micIcon.setImageResource(R.drawable.mic_icon);
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

                    OptionsActivity.this.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
                }
            }
        });
    }

    // Sends intent to searchableActivity with the selected result item
    private void implementResultListOnItemClickListener () {
        searchResultList.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this,
                new RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ResultItem clickedItem = ((SearchAdapter) searchResultList.getAdapter()).getItem(position);
                        gotoProductPage(clickedItem.query);
                    }
                }));
    }

    // Given provider is custom and must follow the column contract
    private void getSearchResults() {
        ProductHandler.doSearch(search_query, new Callback() {
            @Override
            public void run(boolean success, Object result) {
                if(success) {
                    List<QueryItem> resultList = (List<QueryItem>)result;
                    for(QueryItem i : resultList)
                        suggestions.add(new ResultItem(i, R.drawable.ic_action_search, R.drawable.arrow_left_up_icon));
                    Log.d("suggestions", suggestions.toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Set X as the icon for the right icon in the app bar
    private void setClearTextIcon () {
        micIcon.setSelected(Boolean.TRUE);
        micIcon.setImageResource(R.drawable.delete_icon);
        micIcon.invalidate();
    }

    // Set the micrphone icon as the right icon in the app bar
    private void setMicIcon () {
        micIcon.setSelected(Boolean.FALSE);
        micIcon.setImageResource(R.drawable.mic_icon);
        micIcon.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchInput.clearFocus();
        initMenu();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.login) {
            LoginHandler.showLoginDialog(OptionsActivity.this, new Callback() {
                @Override
                public void run(boolean success, Object result) {
                    initMenu();
                    if(success) {
                        Toast.makeText(OptionsActivity.this, "Logged in as " + LoginHandler.currentUser, Toast.LENGTH_LONG).show();
                    }
                }
            });
            return true;
        }
        if(id == R.id.logout) {
            LoginHandler.currentUser = null;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}