package varunbehl.showstime.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.showstime.R;
import varunbehl.showstime.adapter.TvCastAdapter;
import varunbehl.showstime.adapter.TvDataAdapter;
import varunbehl.showstime.adapter.TvSeasonsAdapter;
import varunbehl.showstime.adapter.VideoAdapter;
import varunbehl.showstime.data.ShowsTimeContract;
import varunbehl.showstime.data.ShowsTimeDBHelper;
import varunbehl.showstime.eventbus.MessageEvent;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Tv.Tv;
import varunbehl.showstime.pojo.TvDetails.TvInfo;
import varunbehl.showstime.pojo.TvSeason.TvSeasonInfo;
import varunbehl.showstime.pojo.Video.Videos;
import varunbehl.showstime.util.Constants;
import varunbehl.showstime.util.DateTimeHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class TvDetailActivityFragment extends Fragment {

    public static final String TAG = TvDetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_TV = "DETAIL_TV";

    private EventBus eventBus;
    private TvSeasonInfo.Episode episode = null;
    private int is_fav;
    private Videos videos;
    private TvInfo tvInformation;
    private int tvId;
    private RetrofitManager retrofitManager;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    private Button fav_button;
    private SimpleDraweeView draweeView, nextEpisodeImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean threadAlreadyRunning;
    private HorizontalGridView videosHzGridView, recommendedTvShowsHzGridView, similarTvShowsHzGridView, tvSeasonsGridView, tvCastGridView;
    private List<TvInfo> recommendedTvList = new ArrayList<>();
    private List<TvInfo> similarTvList = new ArrayList<>();
    private ProgressBar similarTvShowsProgressBar, recommendedTvShowsProgressBar, videosProgressBar, tvSeasonsProgressBar, tvCastProgressBar;
    private TextView tvCastHeading, similarTvShowsHeading, recommendedTvShowsHeading, videosHeading, tvSeasonsHeading, nextEpisodeEpisodeName, nextEpisodeEpisodeDate, nextEpisodeEpisodeOverview;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CardView videosCardView;
    private CardView tvSeasonsCardView;
    private CardView similarTvShowsCardView;
    private CardView recommendedTvShowsCardView;
    private ProgressBar progress_fragment;
    private View cordinatorLayout;
    private String episodeDate;
    private LinearLayout nextEpisodeCardView;

    public TvDetailActivityFragment() {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ((DetailActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        retrofitManager = RetrofitManager.getInstance();
        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);

        CardView infoCardView = (CardView) rootView.findViewById(R.id.info_card_view);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        releaseDate = (TextView) rootView.findViewById(R.id.release_date);
        vote = (TextView) rootView.findViewById(R.id.vote);
        plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
        fav_button = (Button) rootView.findViewById(R.id.b11);
        draweeView = (SimpleDraweeView) getActivity().findViewById(R.id.movie_poster);
        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));

        progress_fragment = (ProgressBar) rootView.findViewById(R.id.progress_fragment);
        cordinatorLayout = getActivity().findViewById(R.id.app_bar);

        prefs = getActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        nextEpisodeCardView = (LinearLayout) rootView.findViewById(R.id.nextAirLayout);
        nextEpisodeImage = (SimpleDraweeView) nextEpisodeCardView.findViewById(R.id.img_episode_poster);
        nextEpisodeEpisodeName = (TextView) nextEpisodeCardView.findViewById(R.id.episode_name);
        nextEpisodeEpisodeDate = (TextView) nextEpisodeCardView.findViewById(R.id.episode_date);
        nextEpisodeEpisodeOverview = (TextView) nextEpisodeCardView.findViewById(R.id.episode_desc);


        videosCardView = (CardView) rootView.findViewById(R.id.videosCard);
        videosHzGridView = (HorizontalGridView) videosCardView.findViewById(R.id.horizontal_grid_view);
        videosHeading = (TextView) videosCardView.findViewById(R.id.heading);
        videosProgressBar = (ProgressBar) videosCardView.findViewById(R.id.progress_main);
        videosProgressBar.setVisibility(View.VISIBLE);

        tvSeasonsCardView = (CardView) rootView.findViewById(R.id.tvSeasonsCard);
        tvSeasonsGridView = (HorizontalGridView) tvSeasonsCardView.findViewById(R.id.horizontal_grid_view);
        tvSeasonsHeading = (TextView) tvSeasonsCardView.findViewById(R.id.heading);
        tvSeasonsProgressBar = (ProgressBar) tvSeasonsCardView.findViewById(R.id.progress_main);
        tvSeasonsProgressBar.setVisibility(View.VISIBLE);

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
                tvId = arguments.getParcelable(TvDetailActivityFragment.DETAIL_TV);
            } else {
                tvId = (int) getActivity().getIntent().getExtras().get(TvDetailActivityFragment.DETAIL_TV);
            }
//            collapsingToolbar.setTitle(picture.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }


        new LoadDetailPageThread(1).start();

        return rootView;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getRequest() == 1) {
            cordinatorLayout.setVisibility(View.VISIBLE);
            progress_fragment.setVisibility(View.GONE);
            threadAlreadyRunning = false;
            collapsingToolbar.setTitle(tvInformation.getName());
            draweeView.setImageURI(getString(R.string.image_path) + tvInformation.getBackdropPath());
            releaseDate.setText(getString(R.string.firstAir) + DateTimeHelper.parseDate(tvInformation.getFirstAirDate()) + "");
            vote.setText(getString(R.string.rating) + tvInformation.getVoteAverage() + "/10");
            plotSynopsis.setText(tvInformation.getOverview());
            is_fav = (prefs.getInt("is_fav" + "_" + tvId, 0));
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
                        deleteFromDb();
                        is_fav = 0;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("is_fav" + "_" + tvId, 0);
                        editor.apply();
                    } else {
                        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
                        ShowsTimeDBHelper.addintoDB(tvInformation, getContext(), tvId);
                        is_fav = 1;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("is_fav" + "_" + tvId, 1);
                        editor.apply();
                    }
                }
            });
            List<TvInfo.Season> tvSeasons = tvInformation.getSeasons();
            if (tvSeasons != null && tvSeasons.get(0).getSeasonNumber() == 0) {
                tvSeasons.remove(0);
            }
//            Collections.sort(tvSeasons);
            if (getActivity() != null || (tvSeasons != null ? tvSeasons.size() : 0) < 1) {
                TvSeasonsAdapter tvSeasonsAdapter = new TvSeasonsAdapter(getActivity(), tvSeasons, tvId);
                tvSeasonsGridView.setAdapter(tvSeasonsAdapter);
                tvSeasonsAdapter.notifyDataSetChanged();
                tvSeasonsProgressBar.setVisibility(View.GONE);
                tvSeasonsHeading.setText(R.string.tv_season_heading);
            } else {
                tvSeasonsCardView.setVisibility(View.GONE);
            }

            if (getActivity() != null || tvInformation.getCredits().getCast().size() < 1) {
                TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), tvInformation.getCredits().getCast(), tvId);
                tvCastGridView.setAdapter(tvCastAdapter);
                tvCastAdapter.notifyDataSetChanged();
                tvCastProgressBar.setVisibility(View.GONE);
                tvCastHeading.setText(R.string.tv_casts_heading);
            } else {
                tvSeasonsCardView.setVisibility(View.GONE);
            }
        } else if (event.getRequest() == 2) {
            if (getActivity() != null) {
                TvDataAdapter similarTvDataAdapter = new TvDataAdapter(getActivity(), similarTvList,1);
                similarTvShowsHzGridView.setAdapter(similarTvDataAdapter);
                similarTvDataAdapter.notifyDataSetChanged();
                similarTvShowsHzGridView.setVisibility(View.VISIBLE);
                similarTvShowsProgressBar.setVisibility(View.GONE);
                similarTvShowsHeading.setText(R.string.more_tv_show_heading);
            }
        } else if (event.getRequest() == 3) {
            if (getActivity() != null) {
                TvDataAdapter recommendedTvDataAdapter = new TvDataAdapter(getActivity(), recommendedTvList,2);
                recommendedTvShowsHzGridView.setAdapter(recommendedTvDataAdapter);
                recommendedTvDataAdapter.notifyDataSetChanged();
                recommendedTvShowsHzGridView.setVisibility(View.VISIBLE);
                recommendedTvShowsProgressBar.setVisibility(View.GONE);
                recommendedTvShowsHeading.setText(R.string.you_must_watch_heading);
            }
        } else if (event.getRequest() == 4) {
            if (getActivity() != null) {
                VideoAdapter videoAdapter = new VideoAdapter(getActivity(), videos.getResults());
                videosHzGridView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
                videosHzGridView.setVisibility(View.VISIBLE);
                videosProgressBar.setVisibility(View.GONE);
                videosHeading.setText(R.string.videos_heading);
            }
        } else if (event.getRequest() == 5) {
            if (episode == null) {
                nextEpisodeCardView.setVisibility(View.GONE);
            } else {
                nextEpisodeEpisodeDate.setText(DateTimeHelper.parseDate(episode.getAirDate()));
                nextEpisodeEpisodeName.setText(episode.getName());
                nextEpisodeEpisodeOverview.setText(episode.getOverview());
                if (episode.getStillPath() != null && !"".equals(episode.getStillPath())) {
                    String imageUrl = new TvSeasonsAdapter(getContext()).getImageUri(episode.getStillPath());
                    nextEpisodeImage.setImageURI(imageUrl);
                    nextEpisodeImage.setVisibility(View.VISIBLE);
                } else {
                    nextEpisodeImage.setVisibility(View.GONE);
                }
                if (!episode.getOverview().equals("")) {
                    nextEpisodeEpisodeOverview.setVisibility(View.GONE);
                } else {
                    nextEpisodeEpisodeOverview.setText(episode.getOverview());
                }
            }
        }
    }

    private void deleteFromDb() {

        ShowsTimeDBHelper dbHelper = new ShowsTimeDBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete(ShowsTimeContract.StayTunedEntry.TABLE_NAME, ShowsTimeContract.StayTunedEntry.TV_ID + "=" + tvId, null);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }

    }


    private void fetchDataForTvInfo() {
        Observable<TvInfo> tvInfoObservable = retrofitManager.getTvInfo(tvId + "");
        tvInfoObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<TvInfo>() {
                               @Override
                               public void onCompleted() {
                                   if (tvInformation != null) {
                                       if (tvInformation.getOverview().equals("")) {
//                                           tv.setVisibility(View.GONE);
                                       } else {
                                           eventBus.post(new MessageEvent(1));
                                       }
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);

                                   Log.v("Exception", Arrays.toString(e.getStackTrace()));
                               }

                               @Override
                               public void onNext(TvInfo tvInfo) {
                                   tvInformation = tvInfo;
                               }
                           }
                );
    }

    private void fetchSimilarTvShows() {
        Observable<Tv> similarTvShowsObservable = retrofitManager.getSimilarTvShows(tvId + "");
        similarTvShowsObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (similarTvList != null) {
                                       if (similarTvList.size() < 1) {
                                           similarTvShowsCardView.setVisibility(View.GONE);
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
                               public void onNext(Tv tv) {
                                   similarTvList = tv.getTvShows();
                               }
                           }
                );
    }

    private void fetchRecommendedTvShows() {
        Observable<Tv> recommendedTvShowsObservable = retrofitManager.getRecommendedTvShows(tvId + "");
        recommendedTvShowsObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Tv>() {
                               @Override
                               public void onCompleted() {
                                   if (recommendedTvList != null) {
                                       if (recommendedTvList.size() < 1) {
                                           recommendedTvShowsCardView.setVisibility(View.GONE);
                                       } else {
                                           eventBus.post(new MessageEvent(3));
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
                                   recommendedTvList = tv.getTvShows();
                               }
                           }
                );
    }

    private void fetchVideos() {
        Observable<Videos> videosObservable = retrofitManager.getTrailer(tvId);

        videosObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Videos>() {
                    @Override
                    public void onCompleted() {

                        if (videos.getResults().size() < 1) {
                            videosCardView.setVisibility(View.GONE);
                        } else {
                            eventBus.post(new MessageEvent(4));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }

                    @Override
                    public void onNext(Videos vid) {
                        videos = vid;
                    }
                });
    }

    private void fetchNextAirEpisode() {
        try {
            if (prefs.contains(Constants.NEXT_AIR_DATE + "_" + tvId)) {
                JSONObject episodeJson = new JSONObject(prefs.getString(Constants.NEXT_AIR_DATE + "_" + tvId, ""));
                episode = new Gson().fromJson(episodeJson.toString(), TvSeasonInfo.Episode.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventBus.post(new MessageEvent(5));

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
            } else {

                Intent intent = new Intent(getContext(), NextAirService.class);
                intent.putExtra("tvId", tvId);
                getContext().startService(intent);

                threadAlreadyRunning = true;
                try {
                    fetchDataForTvInfo();
                    fetchVideos();
                    fetchSimilarTvShows();
                    fetchRecommendedTvShows();
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }
                fetchNextAirEpisode();

//                if (tvInformation == null) {
//                    String tvInformationJSONList = prefs.getString("tvInformation_" + tvId, "");
//                    if (!tvInformationJSONList.equals("null")) {
//                        tvInformation =
//                                new Gson().fromJson(tvInformationJSONList, new TypeToken<TvInfo>() {
//                                }.getType());
//                        eventBus.post(new MessageEvent(1));
//                    } else {
//                        fetchDataForTvInfo();
//                        tvInformationJSONList = new Gson().toJson(tvInformation);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("tvInformation_" + tvId, tvInformationJSONList);
//                        editor.apply();
//                    }
//                }
//
//                if (recommendedTvList.isEmpty()) {
//                    String recommendedTvListJSONList = prefs.getString("recommendedTvList_" + tvId, "");
//                    if (!recommendedTvListJSONList.equals("")) {
//                        recommendedTvList =
//                                new Gson().fromJson(recommendedTvListJSONList, new TypeToken<List<Tv.TvShow>>() {
//                                }.getType());
//                        eventBus.post(new MessageEvent(3));
//                    } else {
//                        fetchRecommendedTvShows();
//                        recommendedTvListJSONList = new Gson().toJson(recommendedTvList);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("recommendedTvList_" + tvId, recommendedTvListJSONList);
//                        editor.apply();
//                    }
//                }
//
//
//                if (similarTvList.isEmpty()) {
//
//                    String similarTvListJSONList = prefs.getString("similarTvList_" + tvId, "");
//                    if (!similarTvListJSONList.equals("")) {
//                        similarTvList =
//                                new Gson().fromJson(similarTvListJSONList, new TypeToken<List<Tv.TvShow>>() {
//                                }.getType());
//                        eventBus.post(new MessageEvent(2));
//                    } else {
//                        fetchSimilarTvShows();
//                        similarTvListJSONList = new Gson().toJson(similarTvList);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("similarTvList_" + tvId, similarTvListJSONList);
//                        editor.apply();
//                    }
//                }
//
//
//                if (videos != null) {
//                    String vidoesJSONList = prefs.getString("videos_" + tvId, "");
//                    if (!vidoesJSONList.isEmpty()) {
//                        videos =
//                                new Gson().fromJson(vidoesJSONList, new TypeToken<Videos>() {
//                                }.getType());
//                        eventBus.post(new MessageEvent(4));
//                    } else {
//                        fetchVideos();
//                        vidoesJSONList = new Gson().toJson(videos);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("videos_" + tvId, vidoesJSONList);
//                        editor.apply();
//                    }
//                }
            }
        }
    }
}
