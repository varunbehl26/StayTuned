package varunbehl.staytuned.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.staytuned.R;
import varunbehl.staytuned.adapter.TvSeasonsEpisodeAdapter;
import varunbehl.staytuned.eventbus.MessageEvent;
import varunbehl.staytuned.network.RetrofitManager;
import varunbehl.staytuned.pojo.Episode.EpisodeInfo;

public class EpisodeActivity extends AppCompatActivity {


    public static final String TAG = TvSeasonDetail.class.getSimpleName();
    public static final String TV_ID = "TV_ID";
    public static final String SEASON_ID = "SEASON_ID";
    public static final String EPISODE_ID = "EPISODE_ID";

    private EventBus eventBus;
    private EpisodeInfo episodeInfo = new EpisodeInfo();
    private int tvId;
    private RetrofitManager retrofitManager;
    private TextView title;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    Button fav_button;
    private SimpleDraweeView draweeView;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean threadAlreadyRunning;
    private GridView tvSeasonsGridView;
    private TvSeasonsEpisodeAdapter tvSeasonsEpisodeAdapter;
    private ProgressBar tvSeasonsProgressBar;
    private TextView tvSeasonsHeading;
    private int seasonId;
    private String episodeId;

    @Override
    public void onStart() {
        super.onStart();
        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus = EventBus.getDefault();
        eventBus.unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        retrofitManager = RetrofitManager.getInstance();
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);


        title = (TextView) findViewById(R.id.title);
        releaseDate = (TextView) findViewById(R.id.release_date);
        vote = (TextView) findViewById(R.id.vote);
        plotSynopsis = (TextView) findViewById(R.id.plot_synopsis);
//        fav_button = (Button) findViewById(R.id.b11);
        draweeView = (SimpleDraweeView) findViewById(R.id.movie_poster);


//        tvSeasonsGridView = (GridView) findViewById(R.id.episodesCard).findViewById(R.id.list_view);
//        tvSeasonsHeading = (TextView) findViewById(R.id.episodesCard).findViewById(R.id.heading);
//        tvSeasonsProgressBar = (ProgressBar) findViewById(R.id.episodesCard).findViewById(R.id.progress_main);
//        tvSeasonsProgressBar.setVisibility(View.VISIBLE);

//        ViewCompat.setNestedScrollingEnabled(tvSeasonsGridView, true);


        tvId = (int) getIntent().getExtras().get(TV_ID);
        seasonId = (int) getIntent().getExtras().get(SEASON_ID);
        episodeId = getIntent().getExtras().get(EPISODE_ID).toString();


//            collapsingToolbar.setTitle(picture.getTitle());

        new LoadEpisodeInfoThread(1).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getRequest() == 1) {
            threadAlreadyRunning = false;
            collapsingToolbar.setTitle(episodeInfo.getName());
            draweeView.setImageURI("http://image.tmdb.org/t/p/w780" + episodeInfo.getStillPath());
            releaseDate.setText("Episode Air Date: " + episodeInfo.getAirDate() + "");
            plotSynopsis.setText(episodeInfo.getOverview());
        }
    }

    private class LoadEpisodeInfoThread extends Thread {
        int requestType;

        LoadEpisodeInfoThread(int requestType) {
            this.requestType = requestType;
        }

        @Override
        public void run() {
            super.run();
            if (threadAlreadyRunning) {
                return;
            } else {
                threadAlreadyRunning = true;
                Observable<EpisodeInfo> tvSeasonInfoObservable = retrofitManager.getEpisodeInfo(tvId + "", seasonId + "", episodeId);
                tvSeasonInfoObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<EpisodeInfo>() {
                                       @Override
                                       public void onCompleted() {
                                           if (episodeInfo != null) {
                                               eventBus.post(new MessageEvent(1));
                                           }
                                       }

                                       @Override
                                       public void onError(Throwable e) {
                                           Log.v("Exception", e.toString());
                                       }

                                       @Override
                                       public void onNext(EpisodeInfo einfo) {
                                           episodeInfo = einfo;
                                       }
                                   }
                        );

            }

        }
    }
}

