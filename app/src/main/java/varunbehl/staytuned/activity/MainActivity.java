package varunbehl.staytuned.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.TvDataAdapter;
import varunbehl.staytuned.data.StayTunedContract;
import varunbehl.staytuned.data.StayTunedDBHelper;
import varunbehl.staytuned.eventbus.MessageEvent;
import varunbehl.staytuned.network.RetrofitManager;
import varunbehl.staytuned.pojo.Tv.Tv;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;
import varunbehl.staytuned.util.DateTimeHelper;
import varunbehl.staytuned.util.NetworkCommon;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EventBus eventBus;
    private SharedPreferences.Editor editor;
    private boolean threadAlreadyRunning = false;
    private RetrofitManager retrofitManager;
    private ArrayList<TvInfo> topRatedTvList = new ArrayList<>();
    private ArrayList<TvInfo> popularTvList = new ArrayList<>();
    private ArrayList<TvInfo> airingTodayList = new ArrayList<>();
    private HorizontalGridView popularTvShowsHzGridView, topRatedTvshowsHzGridView, todayAirTvShowsHzGridView;
    private ProgressBar popularTvShowsProgressBar, topRatedTvShowsProgressBar, todayAirTvShowsProgressBar;
    private SharedPreferences prefs;
    private TextView popularTvShowHeading;
    private TextView topRatedTvshowHeading;
    private TextView todayAirTvTvShowHeading;
    private LinearLayout layout;
    private Context mContext;
    private ArrayList<TvInfo> dataList = new ArrayList<>();


    @Override
    public void onStart() {
        super.onStart();
//        eventBus = EventBus.getDefault();
//        eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus = EventBus.getDefault();
//        eventBus.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        new StayTunedDBHelper(this);
        mContext = this;
        eventBus = EventBus.getDefault();

        eventBus.register(this);

        prefs = this.getSharedPreferences(
                "varunbehl.staytuned", Context.MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Fresco.initialize(this);
        retrofitManager = RetrofitManager.getInstance();
        layout = (LinearLayout) findViewById(R.id.layout_main);

        popularTvShowsHzGridView = (HorizontalGridView) findViewById(R.id.popularTvShowsCard).findViewById(R.id.horizontal_grid_view);
        popularTvShowsProgressBar = (ProgressBar) findViewById(R.id.popularTvShowsCard).findViewById(R.id.progress_main);
        popularTvShowsProgressBar.setVisibility(View.VISIBLE);
        popularTvShowHeading = (TextView) findViewById(R.id.popularTvShowsCard).findViewById(R.id.heading);
        TextView popular_view_all_tx = (TextView) findViewById(R.id.popularTvShowsCard).findViewById(R.id.view_all_tx);
        popular_view_all_tx.setVisibility(View.VISIBLE);
        popular_view_all_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionOnclick("popular");
            }
        });

        topRatedTvshowsHzGridView = (HorizontalGridView) findViewById(R.id.topRatedTvShowsCard).findViewById(R.id.horizontal_grid_view);
        topRatedTvShowsProgressBar = (ProgressBar) findViewById(R.id.topRatedTvShowsCard).findViewById(R.id.progress_main);
        topRatedTvShowsProgressBar.setVisibility(View.VISIBLE);
        topRatedTvshowHeading = (TextView) findViewById(R.id.topRatedTvShowsCard).findViewById(R.id.heading);
        TextView topRated_view_all_tx = (TextView) findViewById(R.id.topRatedTvShowsCard).findViewById(R.id.view_all_tx);
        topRated_view_all_tx.setVisibility(View.VISIBLE);
        topRated_view_all_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionOnclick("top_rated");
            }
        });

        todayAirTvShowsHzGridView = (HorizontalGridView) findViewById(R.id.airTodayTvShowsCard).findViewById(R.id.horizontal_grid_view);
        todayAirTvShowsProgressBar = (ProgressBar) findViewById(R.id.airTodayTvShowsCard).findViewById(R.id.progress_main);
        todayAirTvShowsProgressBar.setVisibility(View.VISIBLE);
        todayAirTvTvShowHeading = (TextView) findViewById(R.id.airTodayTvShowsCard).findViewById(R.id.heading);
        TextView todayAir_view_all_tx = (TextView) findViewById(R.id.airTodayTvShowsCard).findViewById(R.id.view_all_tx);
        todayAir_view_all_tx.setVisibility(View.VISIBLE);
        todayAir_view_all_tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionOnclick("airing_today");
            }
        });

        popularTvShowsHzGridView.setVisibility(View.INVISIBLE);
        topRatedTvshowsHzGridView.setVisibility(View.INVISIBLE);
        todayAirTvShowsHzGridView.setVisibility(View.INVISIBLE);
        new MainPageThread(1).start();
        readFromDatabase();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
//                Intent intent= new Intent(MainActivity.this,SearchResultsActivity.class);
//                intent.putExtra("search",newText);
//                startActivity(intent);
//                Log.v("newText-",newText+"");
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);
                //Here u can get the value "query" which is entered in the search box.
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, FavouriteActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void readFromDatabase() {
        StayTunedDBHelper dbHelper = new StayTunedDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + StayTunedContract.StayTunedEntry.TABLE_NAME, null);
        Log.v("count ", cursor.getCount() + "");
        cursor.close();
    }

    private void fetchListTypeDataFromServer(String listType, int eventType) {
        Random rand = new Random();
        int pageToQuery = rand.nextInt(5) + 1;

        switch (eventType) {
            case 2:
                listType = "top_rated";
                break;


        }

        Observable<Tv> topRatedObservable = retrofitManager.listTvShows(listType, pageToQuery);

        topRatedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (dataList != null) {
                                       eventBus.post(new MessageEvent(2));
                                   }
                                   String topRatedTvJSONList = new Gson().toJson(topRatedTvList);
                                   SharedPreferences.Editor editor = prefs.edit();
                                   editor.putString("topRatedTvList", topRatedTvJSONList);
                                   editor.apply();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Tv tv) {
                                   dataList = tv.getTvShows();
                               }
                           }
                );


    }

    private void fetchTopRatedDataFromServer() {
        Random rand = new Random();
        int pageToQuery = rand.nextInt(5) + 1;
        Observable<Tv> topRatedObservable = retrofitManager.listTvShows("top_rated", pageToQuery);

        topRatedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (topRatedTvList != null)
                                       eventBus.post(new MessageEvent(2));

                                   String topRatedTvJSONList = new Gson().toJson(topRatedTvList);
                                   SharedPreferences.Editor editor = prefs.edit();
                                   editor.putString("topRatedTvList", topRatedTvJSONList);
                                   editor.apply();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Tv tv) {
                                   topRatedTvList = tv.getTvShows();
                               }
                           }
                );


    }


    private void fetchPopularDataFromServer() {
        Random rand = new Random();
        int pageToQuery = rand.nextInt(5) + 1;
        Observable<Tv> popularObservable = retrofitManager.listTvShows("popular", pageToQuery);

        popularObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (popularTvList != null)
                                       eventBus.post(new MessageEvent(1));
                                   String topRatedTvJSONList = new Gson().toJson(popularTvList);
                                   SharedPreferences.Editor editor = prefs.edit();
                                   editor.putString("popularTvList", topRatedTvJSONList);
                                   editor.apply();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   Log.v("Exception", "NullPointerEx/ception");
                               }


                               @Override
                               public void onNext(Tv tv) {
                                   popularTvList = tv.getTvShows();
                               }
                           }

                );
    }

    private void fetchAiringTodayDataFromServer() {
        Observable<Tv> airingTodayObservable = retrofitManager.listTvShows("airing_today", 1);

        airingTodayObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (airingTodayList != null)
                                       eventBus.post(new MessageEvent(3));
                                   String topRatedTvJSONList = new Gson().toJson(airingTodayList);
                                   SharedPreferences.Editor editor = prefs.edit();
                                   editor.putString("airingTodayList", topRatedTvJSONList);
                                   editor.apply();
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Tv tv) {
                                   airingTodayList = tv.getTvShows();
                               }
                           }

                );

    }

    private void showSnakeBar(View view) {
        try {
            Snackbar snackbar = Snackbar
                    .make(view, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setAction(null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                            context.startActivity(intent);

                        }
                    });
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.cardview_dark_background, null));

            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            Log.v("exception", e + "");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        threadAlreadyRunning = false;
        if (event.getRequest() == 1) {
            TvDataAdapter popularTvDataAdapter = new TvDataAdapter(getApplicationContext(), popularTvList);
            popularTvShowsHzGridView.setAdapter(popularTvDataAdapter);
            popularTvDataAdapter.notifyDataSetChanged();
            popularTvShowsHzGridView.setVisibility(View.VISIBLE);
            popularTvShowsProgressBar.setVisibility(View.GONE);
            popularTvShowHeading.setText(R.string.popular_tv_heading);
        } else if (event.getRequest() == 2) {
            TvDataAdapter topRatedTvDataAdapter = new TvDataAdapter(getApplicationContext(), topRatedTvList);
            topRatedTvshowsHzGridView.setAdapter(topRatedTvDataAdapter);
            topRatedTvDataAdapter.notifyDataSetChanged();
            topRatedTvshowsHzGridView.setVisibility(View.VISIBLE);
            topRatedTvShowsProgressBar.setVisibility(View.GONE);
            topRatedTvshowHeading.setText(R.string.top_rated_heading);
        } else if (event.getRequest() == 3) {
            TvDataAdapter todayAirTvDataAdapter = new TvDataAdapter(getApplicationContext(), airingTodayList);
            todayAirTvShowsHzGridView.setAdapter(todayAirTvDataAdapter);
            todayAirTvDataAdapter.notifyDataSetChanged();
            todayAirTvShowsHzGridView.setVisibility(View.VISIBLE);
            todayAirTvShowsProgressBar.setVisibility(View.GONE);
            todayAirTvTvShowHeading.setText(R.string.air_today_heading);

        } else if (event.getRequest() == 4) {
            showSnakeBar(layout);
        }
    }

    private void actionOnclick(String type) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("listType", type);
        startActivity(intent);
    }

    private class MainPageThread extends Thread {
        int requestType;

        MainPageThread(int requestType) {
            this.requestType = requestType;
        }

        @Override
        public void run() {
            super.run();
            if (threadAlreadyRunning) {
                return;
            }
            if (DateTimeHelper.getDifference(prefs.getLong("Last-Sync-Time", 0)) || NetworkCommon.isConnected(mContext)) {
                fetchPopularDataFromServer();
                fetchTopRatedDataFromServer();
                fetchAiringTodayDataFromServer();
                Date date = new Date();
                prefs.edit().putLong("Last-Sync-Time", date.getTime()).apply();
            } else {
                if (requestType == 1) {
                    threadAlreadyRunning = true;
                    if (popularTvList.isEmpty()) {
                        String popularTvJSONList = prefs.getString("popularTvList", "");
                        popularTvList =
                                new Gson().fromJson(popularTvJSONList, new TypeToken<List<TvInfo>>() {
                                }.getType());
                        eventBus.post(new MessageEvent(1));

                    }

                    if (topRatedTvList.isEmpty()) {
                        String topRatedTvJSONList = prefs.getString("topRatedTvList", "");
                        topRatedTvList =
                                new Gson().fromJson(topRatedTvJSONList, new TypeToken<List<TvInfo>>() {
                                }.getType());
                        eventBus.post(new MessageEvent(2));

                    }
                    if (airingTodayList.isEmpty()) {
                        String airingTodayJSONList = prefs.getString("airingTodayList", "");
                        if (!airingTodayJSONList.isEmpty()) {
                            airingTodayList =
                                    new Gson().fromJson(airingTodayJSONList, new TypeToken<List<TvInfo>>() {
                                    }.getType());
                            eventBus.post(new MessageEvent(3));
                        }
                    }
                }
            }

        }
    }
}


