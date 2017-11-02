package varunbehl.showstime.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.showstime.R;
import varunbehl.showstime.adapter.TvCastInfoAdapter;
import varunbehl.showstime.adapter.TvCrewAdapter;
import varunbehl.showstime.eventbus.MessageEvent;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Cast.CastInfo;
import varunbehl.showstime.util.DateTimeHelper;
import varunbehl.showstime.util.ImageUtil;

public class TvCastDetailActivity extends AppCompatActivity {

    public static final String CAST_ID = "cast_id";
    private int castId;
    private EventBus eventBus;
    private RetrofitManager retrofitManager;
    private CastInfo casts;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    private ImageView draweeView;
    private CollapsingToolbarLayout collapsingToolbar;
    //    private CardView tvCastCardView;
    private HorizontalGridView tvCastGridView;
    private TextView tvCastHeading;
    private ProgressBar tvCastProgressBar;
    private CardView tvCrewCardView;
    private HorizontalGridView tvCrewGridView;
    private TextView tvCrewHeading;
    private ProgressBar tvCrewProgressBar;
    private TvCrewAdapter tvCrewAdapter;
    private ConstraintLayout tvCastCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_cast_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.release_date);
        vote = findViewById(R.id.vote);
        plotSynopsis = findViewById(R.id.plot_synopsis);
        draweeView = findViewById(R.id.movie_poster);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        tvCastCardView = findViewById(R.id.tvCastCard);
        tvCastGridView = tvCastCardView.findViewById(R.id.horizontal_grid_view);
        tvCastHeading = tvCastCardView.findViewById(R.id.heading);
        tvCastProgressBar = tvCastCardView.findViewById(R.id.progress_main);
        tvCastProgressBar.setVisibility(View.VISIBLE);

//        tvCrewCardView = (CardView) findViewById(R.id.tvCrewCard);
//        tvCrewGridView = (HorizontalGridView) tvCrewCardView.findViewById(R.id.horizontal_grid_view);
//        tvCrewHeading = (TextView) tvCrewCardView.findViewById(R.id.heading);
//        tvCrewProgressBar = (ProgressBar) tvCrewCardView.findViewById(R.id.progress_main);
//        tvCrewProgressBar.setVisibility(View.VISIBLE);

        retrofitManager = RetrofitManager.getInstance();
        castId = getIntent().getIntExtra(CAST_ID, 0);
        new CastLoadThread().start();

    }

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getRequest() == 1) {
//            cordinatorLayout.setVisibility(View.VISIBLE);
//            progress_fragment.setVisibility(View.GONE);
//            threadAlreadyRunning = false;
            collapsingToolbar.setTitle(casts.getName());
            ImageUtil.loadImage(this, draweeView, casts.getProfilePath());
            releaseDate.setText(getString(R.string.born_on) + DateTimeHelper.parseDate(casts.getBirthday()) + "");
            vote.setText(getString(R.string.rating) + casts.getPopularity() + "/10");
            plotSynopsis.setText(casts.getBiography());

            if (casts.getCredits().getCast().size() > 1) {
                TvCastInfoAdapter tvcastAdapter = new TvCastInfoAdapter(this, casts.getCredits().getCast(), castId);
                tvCastGridView.setAdapter(tvcastAdapter);
                tvcastAdapter.notifyDataSetChanged();
                tvCastProgressBar.setVisibility(View.GONE);
                tvCastHeading.setText(R.string.cast_heading);
            } else {
                tvCastCardView.setVisibility(View.GONE);
            }

//            if (casts.getCredits().getCrew().size() > 1) {
//                tvCrewAdapter = new TvCrewAdapter(this, casts.getCredits().getCrew(), castId);
//                tvCrewGridView.setAdapter(tvcastAdapter);
//                tvCrewAdapter.notifyDataSetChanged();
//                tvCrewProgressBar.setVisibility(View.GONE);
//                tvCrewHeading.setText("Crew ");
//            } else {
//                tvCrewGridView.setVisibility(View.GONE);
//            }


        }
    }

    private void fetchCastInfo() {
        Observable<CastInfo> castDetailObservable = retrofitManager.getCastInfo(castId + "");
        castDetailObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CastInfo>() {
                               @Override
                               public void onCompleted() {
                                   if (casts != null) {
//                                       if (casts.getCredits().getCast().size() < 1) {
//
//                                       } else {
                                       eventBus.post(new MessageEvent(1));
//                                       }
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);

                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(CastInfo castDetail) {
                                   casts = castDetail;
                               }
                           }
                );
    }

    private class CastLoadThread extends Thread {           

        @Override
        public void run() {
            super.run();

            if (castId != 0) {
                fetchCastInfo();
            }
        }
    }
}
