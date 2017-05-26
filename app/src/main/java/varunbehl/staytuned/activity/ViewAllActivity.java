package varunbehl.staytuned.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.GridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.TvInfoAdapter;
import varunbehl.staytuned.eventbus.MessageEvent;
import varunbehl.staytuned.network.RetrofitManager;
import varunbehl.staytuned.pojo.Tv.Tv;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;

public class ViewAllActivity extends AppCompatActivity {

    private boolean threadRunning = false;
    private ArrayList<TvInfo> tvInfoList;
    private EventBus eventBus;
    private RetrofitManager retrofitManager;
    private int page = 1;
    private TvInfoAdapter tvInfoAdapter;
    private boolean drawnPrevious = false;
    private boolean isLoading;
    private String listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        GridView myGrid = (GridView) findViewById(R.id.grid_view);
        retrofitManager = RetrofitManager.getInstance();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
        Context context = this;
        setSupportActionBar(toolbar);
        tvInfoList = new ArrayList<>();

        Intent intent= getIntent();
         listType=intent.getStringExtra("listType");


        myGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount >= totalItemCount) && firstVisibleItem != 0 && drawnPrevious) {
                    Log.v("firstVisibleItem--", firstVisibleItem + "");
                    Log.v("visibleItemCount--", visibleItemCount + "");
                    Log.v("totalItemCount--", totalItemCount + "");
                    // End has been reached
                    new ViewAllThread(2).start();

                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        tvInfoAdapter = new TvInfoAdapter(getApplicationContext(), tvInfoList);
        myGrid.setAdapter(tvInfoAdapter);

        new ViewAllThread(1).start();
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
                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(Tv tv) {
                                   tvInfoList.addAll(tv.getTvShows());
                               }
                           }

                );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        tvInfoAdapter.notifyDataSetChanged();
        boolean isRefreshing = false;
        threadRunning = false;

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
            fetchPopularDataFromServer(listType, page++);
        }
    }
}
