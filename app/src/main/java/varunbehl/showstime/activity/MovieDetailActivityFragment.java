package varunbehl.showstime.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.showstime.R;
import varunbehl.showstime.adapter.MovieListDataAdapter;
import varunbehl.showstime.adapter.TvCastAdapter;
import varunbehl.showstime.adapter.VideoAdapter;
import varunbehl.showstime.eventbus.MessageEvent;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Cast.Cast;
import varunbehl.showstime.pojo.CombinedMovieDetail;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.pojo.Video.VideoResult;
import varunbehl.showstime.util.Constants;
import varunbehl.showstime.util.DateTimeHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public static final String TAG = MovieDetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_TV = "DETAIL_TV";

    private EventBus eventBus;
    private int is_fav;
    private int movieId;
    private RetrofitManager retrofitManager;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    private Button fav_button;
    private SimpleDraweeView draweeView;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean threadAlreadyRunning;
    private HorizontalGridView videosHzGridView;
    private HorizontalGridView recommendedTvShowsHzGridView;
    private HorizontalGridView similarTvShowsHzGridView;
    private HorizontalGridView tvCastGridView;
    private List<Pictures> recommendedMoviesList = new ArrayList<>();
    private List<Pictures> similarMoviesList = new ArrayList<>();
    private ProgressBar similarTvShowsProgressBar;
    private ProgressBar recommendedTvShowsProgressBar;
    private ProgressBar videosProgressBar;
    private ProgressBar tvCastProgressBar;
    private TextView tvCastHeading;
    private TextView similarTvShowsHeading;
    private TextView recommendedTvShowsHeading;
    private TextView videosHeading;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CardView tvSeasonsCardView;
    private CardView similarTvShowsCardView;
    private CardView recommendedTvShowsCardView;
    private ProgressBar progressBar;
    private View cordinatorLayout;
    private String episodeDate;
    private CombinedMovieDetail combinedMovieDetail;
    private LinearLayout detailLayout;


    public MovieDetailActivityFragment() {
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
        eventBus.unregister(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ((DetailActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrofitManager = RetrofitManager.getInstance();
        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        detailLayout = (LinearLayout) rootView.findViewById(R.id.detail_layout);
        CardView infoCardView = (CardView) rootView.findViewById(R.id.info_card_view);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        vote = (TextView) rootView.findViewById(R.id.vote);
        plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
        fav_button = (Button) rootView.findViewById(R.id.b11);
        draweeView = (SimpleDraweeView) getActivity().findViewById(R.id.movie_poster);
        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
        fav_button.setVisibility(View.GONE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_fragment);
        cordinatorLayout = getActivity().findViewById(R.id.app_bar);

        prefs = getActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        LinearLayout nextEpisodeCardView = (LinearLayout) rootView.findViewById(R.id.nextAirLayout);
        nextEpisodeCardView.setVisibility(View.GONE);

        CardView videosCardView = (CardView) rootView.findViewById(R.id.videosCard);
        videosHzGridView = (HorizontalGridView) videosCardView.findViewById(R.id.horizontal_grid_view);
        videosHeading = (TextView) videosCardView.findViewById(R.id.heading);
        videosProgressBar = (ProgressBar) videosCardView.findViewById(R.id.progress_main);
        videosProgressBar.setVisibility(View.VISIBLE);

        tvSeasonsCardView = (CardView) rootView.findViewById(R.id.tvSeasonsCard);
        ProgressBar tvSeasonsProgressBar = (ProgressBar) tvSeasonsCardView.findViewById(R.id.progress_main);
        tvSeasonsProgressBar.setVisibility(View.VISIBLE);
        tvSeasonsCardView.setVisibility(View.GONE);

        CardView tvCastCardView = (CardView) rootView.findViewById(R.id.tvCastCard);
        tvCastGridView = (HorizontalGridView) tvCastCardView.findViewById(R.id.horizontal_grid_view);
        tvCastHeading = (TextView) tvCastCardView.findViewById(R.id.heading);
        tvCastProgressBar = (ProgressBar) tvCastCardView.findViewById(R.id.progress_main);
        tvCastProgressBar.setVisibility(View.VISIBLE);

        similarTvShowsCardView = (CardView) rootView.findViewById(R.id.similarTvShowsCard);
        similarTvShowsHzGridView = (HorizontalGridView) similarTvShowsCardView.findViewById(R.id.horizontal_grid_view);
        similarTvShowsHeading = (TextView) similarTvShowsCardView.findViewById(R.id.heading);
        similarTvShowsProgressBar = (ProgressBar) similarTvShowsCardView.findViewById(R.id.progress_main);
        similarTvShowsProgressBar.setVisibility(View.VISIBLE);

        recommendedTvShowsCardView = (CardView) rootView.findViewById(R.id.recommendedTvShowsCard);
        recommendedTvShowsHzGridView = (HorizontalGridView) recommendedTvShowsCardView.findViewById(R.id.horizontal_grid_view);
        recommendedTvShowsHeading = (TextView) recommendedTvShowsCardView.findViewById(R.id.heading);
        recommendedTvShowsProgressBar = (ProgressBar) recommendedTvShowsCardView.findViewById(R.id.progress_main);
        recommendedTvShowsProgressBar.setVisibility(View.VISIBLE);


        Bundle arguments = getArguments();
        try {
            if (arguments != null) {
                movieId = arguments.getParcelable(MovieDetailActivityFragment.DETAIL_TV);
            } else {
                movieId = (int) getActivity().getIntent().getExtras().get(MovieDetailActivityFragment.DETAIL_TV);
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }

        showProgressBar();
        new LoadDetailPageThread(1).start();

        return rootView;
    }

    private void hideProgressBar() {
        detailLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        detailLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        hideProgressBar();
        if (event.getRequest() == 1) {
            cordinatorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            threadAlreadyRunning = false;
            collapsingToolbar.setTitle(combinedMovieDetail.getTitle());
            draweeView.setImageURI(getString(R.string.image_path) + combinedMovieDetail.getBackdropPath());
            releaseDate.setText(getString(R.string.release_data) + DateTimeHelper.parseDate(combinedMovieDetail.getReleaseDate()) + "");
            vote.setText(getString(R.string.rating) + combinedMovieDetail.getVoteAverage() + "/10");
            plotSynopsis.setText(combinedMovieDetail.getOverview());
            is_fav = (prefs.getInt("is_fav" + "_" + movieId, 0));
            if (is_fav == 1) {
                fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
            } else {
                fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
            }

            fav_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (is_fav == 1) {
                        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
//                        deleteFromDb();
                        is_fav = 0;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("is_fav" + "_" + movieId, 0);
                        editor.apply();
                    } else {
                        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
//                        ShowsTimeDBHelper.addintoDB(tvInformation, getContext(), movieId);
                        is_fav = 1;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("is_fav" + "_" + movieId, 1);
                        editor.apply();
                    }
                }
            });
            List<VideoResult> CombinedMovieDetailVideos = combinedMovieDetail.getVideos().getResults();

            if (getActivity() != null || (CombinedMovieDetailVideos != null ? CombinedMovieDetailVideos.size() : 0) < 1) {
                VideoAdapter videoAdapter = new VideoAdapter(getActivity(), CombinedMovieDetailVideos);
                videosHzGridView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
                videosHzGridView.setVisibility(View.VISIBLE);
                videosProgressBar.setVisibility(View.GONE);
                videosHeading.setText(R.string.videos_heading);
                tvSeasonsCardView.setVisibility(View.GONE);
            } else {
                tvSeasonsCardView.setVisibility(View.GONE);
            }
            List<Cast> movieCast = combinedMovieDetail.getCredits().getCast();

            if (getActivity() != null || combinedMovieDetail.getCredits().getCast().size() < 1) {
                TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), movieCast, movieId);
                tvCastGridView.setAdapter(tvCastAdapter);
                tvCastAdapter.notifyDataSetChanged();
                tvCastProgressBar.setVisibility(View.GONE);
                tvCastHeading.setText(R.string.tv_casts_heading);
            } else {
                tvSeasonsCardView.setVisibility(View.GONE);
            }
        } else if (event.getRequest() == 2) {
            if (getActivity() != null) {
                MovieListDataAdapter popularTvDataAdapter = new MovieListDataAdapter(getContext(), similarMoviesList, 1);
                similarTvShowsHzGridView.setAdapter(popularTvDataAdapter);
                popularTvDataAdapter.notifyDataSetChanged();
                similarTvShowsHzGridView.setVisibility(View.VISIBLE);
                similarTvShowsProgressBar.setVisibility(View.GONE);
                similarTvShowsHeading.setText(R.string.more_tv_show_heading);
            }
        } else if (event.getRequest() == 3) {
            if (getActivity() != null) {
                MovieListDataAdapter recommendedMovieAdapter = new MovieListDataAdapter(getContext(), recommendedMoviesList, 2);
                recommendedTvShowsHzGridView.setAdapter(recommendedMovieAdapter);
                recommendedMovieAdapter.notifyDataSetChanged();
                recommendedTvShowsHzGridView.setVisibility(View.VISIBLE);
                recommendedTvShowsProgressBar.setVisibility(View.GONE);
                recommendedTvShowsHeading.setText(R.string.you_must_watch_heading);
            }
        }
    }

    public void loadSimilarRecommendations(CombinedMovieDetail combinedMovieDetail) {
        similarMoviesList = combinedMovieDetail.getSimilar().getResults();

        if (similarMoviesList.size() < 1) {
            similarTvShowsCardView.setVisibility(View.GONE);
        } else {
            eventBus.post(new MessageEvent(2));
        }
        recommendedMoviesList = combinedMovieDetail.getRecommendations().getResults();
        if (recommendedMoviesList.size() < 1) {
            recommendedTvShowsCardView.setVisibility(View.GONE);
        } else {
            eventBus.post(new MessageEvent(3));
        }

    }

    private void fetchDataForTvInfo() {
        Observable<CombinedMovieDetail> tvInfoObservable = retrofitManager.getMoviesDetail(movieId);
        tvInfoObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CombinedMovieDetail>() {
                               @Override
                               public void onCompleted() {
                                   if (combinedMovieDetail != null) {
                                       if (combinedMovieDetail.getOverview().equals("")) {
//                                           tv.setVisibility(View.GONE);
                                       } else {
                                           eventBus.post(new MessageEvent(1));
                                       }

                                       loadSimilarRecommendations(combinedMovieDetail);
                                       String tvInformationJSONList = new Gson().toJson(combinedMovieDetail);
                                       SharedPreferences.Editor editor = prefs.edit();
                                       editor.putString("movieInformation_" + movieId, tvInformationJSONList);
                                       editor.apply();

                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);
                               }

                               @Override
                               public void onNext(CombinedMovieDetail detail) {
                                   combinedMovieDetail = detail;
                               }
                           }
                );
    }


    private class LoadDetailPageThread extends Thread {
        final int requestType;

        LoadDetailPageThread(int requestType) {
            this.requestType = requestType;
        }

        @Override
        public void run() {
            super.run();
            if (threadAlreadyRunning) {
                return;
            } else {
                threadAlreadyRunning = true;
                try {
                    String tvInformationJSONList = prefs.getString("movieInformation_" + movieId, "");
                    if (prefs.contains("movieInformation_" + movieId)) {
                        combinedMovieDetail =
                                new Gson().fromJson(tvInformationJSONList, new TypeToken<CombinedMovieDetail>() {
                                }.getType());
                        if (combinedMovieDetail != null) {
                            eventBus.post(new MessageEvent(1));
                            loadSimilarRecommendations(combinedMovieDetail);
                        } else {
                            fetchDataForTvInfo();
                        }
                    } else {
                        fetchDataForTvInfo();

                    }
                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    e.printStackTrace();
                }
            }
        }
    }
}
