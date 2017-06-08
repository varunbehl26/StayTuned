package varunbehl.staytuned.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.MoviesInfoAdapter;
import varunbehl.staytuned.adapter.TvInfoAdapter;
import varunbehl.staytuned.eventbus.MessageEvent;
import varunbehl.staytuned.network.RetrofitManager;
import varunbehl.staytuned.pojo.Picture.Picture_Detail;
import varunbehl.staytuned.pojo.Picture.Pictures;
import varunbehl.staytuned.pojo.Tv.Tv;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class ViewAllActivity extends AppCompatActivity {

    private boolean threadRunning = false;
    private ArrayList<TvInfo> tvInfoList;
    private ArrayList<Pictures> moviesList;
    private EventBus eventBus;
    private RetrofitManager retrofitManager;
    private int page = 1;
    private TvInfoAdapter tvInfoAdapter;
    private MoviesInfoAdapter moviesInfoAdapter;
    private GridView myGrid;
    private boolean drawnPrevious = false;
    private boolean isLoading;
    private String listType;
    private int categoryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("View All");
        myGrid = (GridView) findViewById(R.id.grid_view);
        retrofitManager = RetrofitManager.getInstance();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        Context context = this;
        setSupportActionBar(toolbar);
        tvInfoList = new ArrayList<>();
        moviesList = new ArrayList<>();

        Intent intent = getIntent();
        listType = intent.getStringExtra("listType");
        categoryType = intent.getIntExtra("categoryType", 1);

        myGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount >= totalItemCount) && firstVisibleItem != 0 && drawnPrevious) {
                    Log.v("firstVisibleItem--", firstVisibleItem + "");
                    Log.v("visibleItemCount--", visibleItemCount + "");
                    Log.v("totalItemCount--", totalItemCount + "");
                    // End has been reached
                    new ViewAllThread(categoryType).start();

                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        tvInfoAdapter = new TvInfoAdapter(getApplicationContext(), tvInfoList);
        moviesInfoAdapter = new MoviesInfoAdapter(getApplicationContext(), moviesList);

        new ViewAllThread(categoryType).start();
        drawnPrevious = true;

    }

    private void fetchPopularDataFromServer(String listType, int page) {
        Observable<Tv> popularObservable = retrofitManager.listTvShows(listType, page);

        popularObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (tvInfoList != null)
                                       eventBus.post(new MessageEvent(1));

                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);

                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Tv tv) {
                                   tvInfoList.addAll(tv.getTvShows());
                               }
                           }

                );
    }

    private void fetchMoviesDataFromServer(String listType, int page) {
        Observable<Picture_Detail> popularObservable = popularObservable = retrofitManager.listMoviesInfo(listType, page);

        popularObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Picture_Detail>() {
                               @Override
                               public void onCompleted() {
                                   if (moviesList != null)
                                       eventBus.post(new MessageEvent(2));

                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);

                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Picture_Detail picture_detail) {
                                   moviesList.addAll(picture_detail.getResults());
                               }
                           }

                );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getRequest() == 1) {
            myGrid.setAdapter(tvInfoAdapter);
            tvInfoAdapter.notifyDataSetChanged();
            boolean isRefreshing = false;
            threadRunning = false;
        } else {
            myGrid.setAdapter(moviesInfoAdapter);

            moviesInfoAdapter.notifyDataSetChanged();
            boolean isRefreshing = false;
            threadRunning = false;
        }
    }

    private class ViewAllThread extends Thread {
        int request;

        public ViewAllThread(int request) {
            this.request = request;
        }

        @Override
        public void run() {
            super.run();
            if (threadRunning) {
                return;
            }
            threadRunning = true;
            if (request == 1) {
                fetchMoviesDataFromServer(listType, page++);
            } else {
                fetchPopularDataFromServer(listType, page++);
            }
        }
    }
}
