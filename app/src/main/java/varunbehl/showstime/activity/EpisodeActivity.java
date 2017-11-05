package varunbehl.showstime.activity;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.showstime.R;
import varunbehl.showstime.adapter.TvSeasonsEpisodeAdapter;
import varunbehl.showstime.eventbus.MessageEvent;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Episode.EpisodeInfo;
import varunbehl.showstime.util.ImageUtil;

public class EpisodeActivity extends AppCompatActivity {


    public static final String TAG = TvSeasonDetail.class.getSimpleName();
    public static final String TV_ID = "TV_ID";
    public static final String SEASON_ID = "SEASON_ID";
    public static final String EPISODE_ID = "EPISODE_ID";
    Button fav_button;
    private EventBus eventBus;
    private EpisodeInfo episodeInfo = new EpisodeInfo();
    private int tvId;
    private RetrofitManager retrofitManager;
    private TextView releaseDate;
    private TextView plotSynopsis;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        retrofitManager = RetrofitManager.getInstance();
        collapsingToolbar = findViewById(R.id.collapsingToolbar);


        TextView title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.release_date);
        TextView vote = findViewById(R.id.vote);
        plotSynopsis = findViewById(R.id.plot_synopsis);
//        fav_button = (Button) findViewById(R.id.b11);
        draweeView = findViewById(R.id.movie_poster);


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
            ImageUtil.loadImageWithFullScreen(this, draweeView, episodeInfo.getStillPath());
            releaseDate.setText(getString(R.string.episode_air_heading) + episodeInfo.getAirDate() + "");
            plotSynopsis.setText(episodeInfo.getOverview());
        }
    }

    private class LoadEpisodeInfoThread extends Thread {
        final int requestType;


        LoadEpisodeInfoThread(int requestType) {
            this.requestType = requestType;
        }

        @Override
        public void run() {
            super.run();
            if (threadAlreadyRunning) {
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
                                           e.printStackTrace();
                                           FirebaseCrash.report(e);
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


