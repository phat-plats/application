package com.phat_plats.scanitfortheplanet;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.phat_plats.scanitfortheplanet.search.adapter.SearchAdapter;
import com.phat_plats.scanitfortheplanet.search.contract.CustomSearchableConstants;
import com.phat_plats.scanitfortheplanet.search.model.CustomSearchableInfo;
import com.phat_plats.scanitfortheplanet.search.model.QueryItem;
import com.phat_plats.scanitfortheplanet.search.model.ResultItem;
import com.phat_plats.scanitfortheplanet.search.util.ManifestParser;
import com.phat_plats.scanitfortheplanet.search.util.RecyclerViewOnItemClickListener;
import com.phat_plats.scanitfortheplanet.views.SoftKeyboardLsnedLayout;
import com.phat_plats.scanitfortheplanet.views.anim.ExpandAnimation;
import com.phat_plats.scanitfortheplanet.views.anim.MarginAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OptionsActivity extends ActionBarActivity {

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

    private QueryItem query;
    private String providerName;
    private String providerAuthority;
    private String searchableActivity;
    private Boolean isRecentSuggestionsProvider = Boolean.TRUE;
    private String search_query;

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

        final ViewGroup searchWrapper = (ViewGroup)findViewById(R.id.cs_header);
        searchWrapper.bringToFront();

        final EditText searchbox = (EditText)findViewById(R.id.product_search);
        searchbox.clearFocus();

        final float startMargin = getResources().getDimension(R.dimen.search_box_margin);
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

        initSearch();
    }

    private void gotoProductPage(QueryItem value) {
        Toast.makeText(OptionsActivity.this, "UPC: " + value,
                Toast.LENGTH_LONG).show();
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

        SearchAdapter adapter = new SearchAdapter(new ArrayList<ResultItem>());
        searchResultList.setAdapter(adapter);

        this.searchInput.setMaxLines(1);

        implementSearchTextListener();
        implementVoiceInputListener();
        implementResultListOnItemClickListener();

        getManifestConfig();
    }

    // Sends an intent with the typed query to the searchable Activity
    private void sendSuggestionIntent(ResultItem item) {
        try {
            Intent sendIntent = new Intent(this, Class.forName(searchableActivity));
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Bundle b = new Bundle();
            b.putParcelable(CustomSearchableConstants.CLICKED_RESULT_ITEM, item);

            sendIntent.putExtras(b);
            startActivity(sendIntent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Sends an intent with the typed query to the searchable Activity
    private void sendSearchIntent () {
        // If it is set one-line mode, directly saves the suggestion in the provider
        if (!CustomSearchableInfo.getIsTwoLineExhibition()) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, providerAuthority, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
            suggestions.saveRecentQuery(query.name, query.upc);
        }
        gotoProductPage(query);
    }

    // Listeners implementation ____________________________________________________________________
    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendSearchIntent();
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!"".equals(searchInput.getText().toString())) {
                    search_query = searchInput.getText().toString();

                    setClearTextIcon();

                    if (isRecentSuggestionsProvider) {
                        // Provider is descendant of SearchRecentSuggestionsProvider
                        mapResultsFromRecentProviderToList();
                    } else {
                        // Provider is custom and shall follow the contract
                        mapResultsFromCustomProviderToList();
                    }
                } else {
                    setMicIcon();
                }
            }

            // DO NOTHING
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            // DO NOTHING
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VOICE_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(text.get(0));
                }
                break;
            }
            case RC_BARCODE_CAPTURE: {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    getItemFromBarcodeScan(barcode.displayValue);
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
                        sendSuggestionIntent(clickedItem);
                    }
                }));
    }

    // Util ________________________________________________________________________________________
    // Retrieve the priority provider, searchable activity and provider authority from the AndroidManifest.xml
    private void getManifestConfig () {
        try {
            Map<String, String> providers = ManifestParser.getProviderNameAndAuthority(this);

            OUTER: for (String key : providers.keySet()) {
                providerAuthority = providers.get(key).toString();
                providerName = key;

                if (Class.forName(providerName).getSuperclass().equals(SearchRecentSuggestionsProvider.class)) {
                    isRecentSuggestionsProvider = Boolean.TRUE;

                    break OUTER;
                } else {
                    isRecentSuggestionsProvider = Boolean.FALSE;
                }
            }

            searchableActivity = ManifestParser.getSearchableActivity(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Look for search suggestions in client's provider (the one the implements the RecentSuggestionsProvider interface)
    private Cursor queryRecentSuggestionsProvider () {
        Uri uri = Uri.parse("content://".concat(providerAuthority.concat("/suggestions")));

        String[] selection;

        if (CustomSearchableInfo.getIsTwoLineExhibition()) {
            selection = SearchRecentSuggestions.QUERIES_PROJECTION_2LINE;
        } else {
            selection = SearchRecentSuggestions.QUERIES_PROJECTION_1LINE;
        }

        String[] selectionArgs = new String[] {"%" + query + "%"};

        return OptionsActivity.this.getContentResolver().query(
                uri,
                selection,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Look for search suggestions in client's provider (Custom one)
    private Cursor queryCustomSuggestionProvider () {
        Uri uri = Uri.parse("content://".concat(providerAuthority).concat("/suggestions/").concat(search_query));

        String[] selection = {"display1"};
        String[] selectionArgs = new String[] {"%" + query + "%"};

        return OptionsActivity.this.getContentResolver().query(
                uri,
                SearchRecentSuggestions.QUERIES_PROJECTION_1LINE,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Given provider is custom and must follow the column contract
    private void mapResultsFromCustomProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = results = queryCustomSuggestionProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer headerIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                Integer subHeaderIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                while (results.moveToNext()) {
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);
                    Integer leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(new QueryItem(header, subHeader), leftIcon, rightIcon);
                    resultList.add(aux);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    private void getItemFromBarcodeScan(String upc) {
        new AsyncTask<Void, Void, QueryItem>() {
            @Override
            protected void onPostExecute(QueryItem value) {
                gotoProductPage(value);
            }

            @Override
            protected QueryItem doInBackground(Void[] params) {
                Cursor results = results = queryCustomSuggestionProvider();

                Integer headerIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                Integer subHeaderIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                String header = results.getString(headerIdx);
                String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);

                results.close();
                return new QueryItem(header, subHeader);
            }
        }.execute();
    }

    // Given provider is descendant of SearchRecentSuggestionsProvider (column scheme differs)
    private void mapResultsFromRecentProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = queryRecentSuggestionsProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer headerIdx = results.getColumnIndex("display1");
                Integer subHeaderIdx = results.getColumnIndex("display2");
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                while (results.moveToNext()) {
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);
                    Integer leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(query, leftIcon, rightIcon);
                    resultList.add(aux);
                }

                results.close();
                return resultList;
            }
        }.execute();
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
