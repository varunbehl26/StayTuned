package varunbehl.showstime.activity;

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
import varunbehl.showstime.R;
import varunbehl.showstime.adapter.MoviesInfoAdapter;
import varunbehl.showstime.adapter.TvInfoAdapter;
import varunbehl.showstime.eventbus.MessageEvent;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Picture.Picture_Detail;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.pojo.Tv.Tv;
import varunbehl.showstime.pojo.TvDetails.TvInfo;

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
                    new ViewAllThread(categoryType, true).start();

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

    private void fetchTvDataFromServer(String listType, int page, final boolean scroll) {
        Observable<Tv> popularObservable = retrofitManager.listTvShows(listType, page);

        popularObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (tvInfoList != null) {
                                       if (scroll) {
                                           eventBus.post(new MessageEvent(3));
                                       } else {
                                           eventBus.post(new MessageEvent(1));
                                       }
                                   }
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

    private void fetchMoviesDataFromServer(String listType, int page, final boolean scroll) {
        Observable<Picture_Detail> popularObservable = retrofitManager.listMoviesInfo(listType, page);

        popularObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Picture_Detail>() {
                               @Override
                               public void onCompleted() {
                                   if (moviesList != null){
                                       if (scroll) {
                                           eventBus.post(new MessageEvent(4));
                                       } else {
                                           eventBus.post(new MessageEvent(2));
                                       }
                                   }
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
        } else if (event.getRequest() == 2) {
            myGrid.setAdapter(moviesInfoAdapter);
            moviesInfoAdapter.notifyDataSetChanged();
        } else if (event.getRequest() == 3) {
            tvInfoAdapter.notifyDataSetChanged();
        } else {
            moviesInfoAdapter.notifyDataSetChanged();
        }
        boolean isRefreshing = false;
        threadRunning = false;
    }

    private class ViewAllThread extends Thread {
        final int request;
        boolean scroll;

        public ViewAllThread(int request) {
            this.request = request;
        }

        public ViewAllThread(int request, boolean scroll) {
            this.request = request;
            this.scroll = scroll;
        }

        @Override
        public void run() {
            super.run();
            if (threadRunning) {
                return;
            }
            threadRunning = true;
            if (request == 1) {
                fetchMoviesDataFromServer(listType, page++, scroll);
            } else {
                fetchTvDataFromServer(listType, page++, scroll);
            }
        }
    }
}
