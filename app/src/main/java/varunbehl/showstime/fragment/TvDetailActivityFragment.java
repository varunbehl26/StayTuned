package varunbehl.showstime.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import varunbehl.showstime.FullscreenImageViewActivity;
import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvDetailActivity;
import varunbehl.showstime.adapter.TvDataAdapter;
import varunbehl.showstime.data.ShowsTimeContract;
import varunbehl.showstime.data.ShowsTimeDBHelper;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.pojo.TvDetails.CombinedTvDetail;
import varunbehl.showstime.pojo.TvSeason.TvSeasonInfo;
import varunbehl.showstime.pojo.Video.VideoResult;
import varunbehl.showstime.util.Constants;
import varunbehl.showstime.util.DateTimeHelper;
import varunbehl.showstime.util.ImageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class TvDetailActivityFragment extends Fragment {

    public static final String TAG = TvDetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_TV = "DETAIL_TV";
    private TvSeasonInfo.Episode episode = null;
    private int is_fav;
    private List<VideoResult> videos;
    private CombinedTvDetail tvInformation;
    private int tvId;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    private Button fav_button;
    private ImageView nextEpisodeImage;
    private CollapsingToolbarLayout collapsingToolbar;
    private HorizontalGridView videosHzGridView;
    private HorizontalGridView recommendedTvShowsHzGridView;
    private HorizontalGridView similarTvShowsHzGridView;
    private ProgressBar similarTvShowsProgressBar;
    private ProgressBar recommendedTvShowsProgressBar;
    private ProgressBar videosProgressBar;
    private TextView similarTvShowsHeading;
    private TextView recommendedTvShowsHeading;
    private TextView videosHeading;
    private TextView nextEpisodeEpisodeName;
    private TextView nextEpisodeEpisodeDate;
    private TextView nextEpisodeEpisodeOverview;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private View videosCardView;
    private View tvSeasonsCardView;
    private View similarTvShowsCardView;
    private View recommendedTvShowsCardView;
    private ProgressBar progress_fragment;
    //    private View coordinatorLayout;
    private LinearLayout nextEpisodeCardView;
    private TextView genreTextview;
    private TextView numberOfEpisodes;
    private CarouselView carouselView;

    public TvDetailActivityFragment() {
    }

    public static TvDetailActivityFragment newInstance(CombinedTvDetail tvInformation) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("tvInformation", tvInformation);
        TvDetailActivityFragment tvDetailActivityFragment = new TvDetailActivityFragment();
        tvDetailActivityFragment.setArguments(bundle);
        return tvDetailActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ((TvDetailActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((TvDetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RetrofitManager retrofitManager = RetrofitManager.getInstance();
//        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);

//        CardView infoCardView = (CardView) rootView.findViewById(R.id.info_card_view);
        TextView title = rootView.findViewById(R.id.title);
        releaseDate = rootView.findViewById(R.id.release_date);

        genreTextview = rootView.findViewById(R.id.genre_textView);
//        numberOfEpisodes = (TextView) rootView.findViewById(R.id.episode_number_tx);

        vote = rootView.findViewById(R.id.vote);
        plotSynopsis = rootView.findViewById(R.id.plot_synopsis);
        fav_button = rootView.findViewById(R.id.b11);
        ImageView draweeView = getActivity().findViewById(R.id.movie_poster);

        carouselView = getActivity().findViewById(R.id.carouselView);


        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));

        progress_fragment = rootView.findViewById(R.id.progress_fragment);
//        coordinatorLayout = getActivity().findViewById(R.id.app_bar);

        prefs = getActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        nextEpisodeCardView = rootView.findViewById(R.id.nextAirLayout);
        nextEpisodeImage = nextEpisodeCardView.findViewById(R.id.img_episode_poster);
        nextEpisodeEpisodeName = nextEpisodeCardView.findViewById(R.id.episode_name);
        nextEpisodeEpisodeDate = nextEpisodeCardView.findViewById(R.id.episode_date);
        nextEpisodeEpisodeOverview = nextEpisodeCardView.findViewById(R.id.episode_desc);


//        videosCardView = (CardView) rootView.findViewById(R.id.videosCard);
//        videosHzGridView = (HorizontalGridView) videosCardView.findViewById(R.id.horizontal_grid_view);
//        videosHeading = (TextView) videosCardView.findViewById(R.id.heading);
//        videosProgressBar = (ProgressBar) videosCardView.findViewById(R.id.progress_main);
//        videosProgressBar.setVisibility(View.VISIBLE);

        tvSeasonsCardView = rootView.findViewById(R.id.tvSeasonsCard);
        HorizontalGridView tvSeasonsGridView = tvSeasonsCardView.findViewById(R.id.horizontal_grid_view);
        TextView tvSeasonsHeading = tvSeasonsCardView.findViewById(R.id.heading);
        ProgressBar tvSeasonsProgressBar = tvSeasonsCardView.findViewById(R.id.progress_main);
        tvSeasonsProgressBar.setVisibility(View.VISIBLE);

        View tvCastCardView = rootView.findViewById(R.id.tvCastCard);
        HorizontalGridView tvCastGridView = tvCastCardView.findViewById(R.id.horizontal_grid_view);
        TextView tvCastHeading = tvCastCardView.findViewById(R.id.heading);
        ProgressBar tvCastProgressBar = tvCastCardView.findViewById(R.id.progress_main);
        tvCastProgressBar.setVisibility(View.VISIBLE);

        similarTvShowsCardView = rootView.findViewById(R.id.similarTvShowsCard);
        similarTvShowsHzGridView = similarTvShowsCardView.findViewById(R.id.horizontal_grid_view);
        similarTvShowsHeading = similarTvShowsCardView.findViewById(R.id.heading);
        similarTvShowsProgressBar = similarTvShowsCardView.findViewById(R.id.progress_main);
        similarTvShowsProgressBar.setVisibility(View.VISIBLE);

        recommendedTvShowsCardView = rootView.findViewById(R.id.recommendedTvShowsCard);
        recommendedTvShowsHzGridView = recommendedTvShowsCardView.findViewById(R.id.horizontal_grid_view);
        recommendedTvShowsHeading = recommendedTvShowsCardView.findViewById(R.id.heading);
        recommendedTvShowsProgressBar = recommendedTvShowsCardView.findViewById(R.id.progress_main);
        recommendedTvShowsProgressBar.setVisibility(View.VISIBLE);


        Bundle arguments = getArguments();
        tvInformation = arguments.getParcelable("tvInformation");
        if (tvInformation != null) {
            tvId = tvInformation.getId();
            loadTvDetails();
            loadSimilarRecommendations(tvInformation);
            fetchNextAirEpisode();
        }
        return rootView;
    }

    private void fetchNextAirEpisode() {
        try {
            if (prefs.contains(Constants.NEXT_AIR_DATE + "_" + tvId)) {
                JSONObject episodeJson = new JSONObject(prefs.getString(Constants.NEXT_AIR_DATE + "_" + tvId, ""));
                episode = new Gson().fromJson(episodeJson.toString(), TvSeasonInfo.Episode.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }
        if (episode == null) {
            nextEpisodeCardView.setVisibility(View.GONE);
        } else {
            nextEpisodeEpisodeDate.setText(DateTimeHelper.parseDate(episode.getAirDate()));
            nextEpisodeEpisodeName.setText(episode.getName());
            nextEpisodeEpisodeOverview.setText(episode.getOverview());
            if (episode.getStillPath() != null && !"".equals(episode.getStillPath())) {
                ImageUtil.loadImageWithFullScreen(getActivity(), nextEpisodeImage, episode.getStillPath());
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

    private void loadTvDetails() {
//        coordinatorLayout.setVisibility(View.VISIBLE);
        progress_fragment.setVisibility(View.GONE);
        boolean threadAlreadyRunning = false;
//        collapsingToolbar.setTitle(tvInformation.getName());
//        draweeView.setImageURI(getString(R.string.image_path) + tvInformation.getBackdropPath());
        releaseDate.setText(getString(R.string.firstAir) + DateTimeHelper.parseDate(tvInformation.getFirstAirDate()) + "");
        vote.setText(getString(R.string.rating) + tvInformation.getVoteAverage() + "/10");
        plotSynopsis.setText(tvInformation.getOverview());
        is_fav = (prefs.getInt("is_fav" + "_" + tvId, 0));
        if (is_fav == 1) {
            fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
        } else {
            fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
        }
//            numberOfEpisodes.setText(tvInformation.getNumberOfEpisodes() + "Episodes");

        genreTextview.setText(tvInformation.getGenres().toString());

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
        List<CombinedTvDetail.Season> tvSeasons = tvInformation.getSeasons();
        if (tvSeasons != null && tvSeasons.get(0).getSeasonNumber() == 0) {
            tvSeasons.remove(0);
        }
//        if (getActivity() != null || (tvSeasons != null ? tvSeasons.size() : 0) < 1) {
//            TvSeasonsAdapter tvSeasonsAdapter = new TvSeasonsAdapter(getActivity(), tvSeasons, tvId);
//            tvSeasonsGridView.setAdapter(tvSeasonsAdapter);
//            tvSeasonsAdapter.notifyDataSetChanged();
//            tvSeasonsProgressBar.setVisibility(View.GONE);
//            tvSeasonsHeading.setText(R.string.tv_season_heading);
//        } else {
//            tvSeasonsCardView.setVisibility(View.GONE);
//        }

//        if (getActivity() != null || tvInformation.getCredits().getCast().size() < 1) {
//            TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), tvInformation.getCredits().getCast(), tvId);
//            tvCastGridView.setAdapter(tvCastAdapter);
//            tvCastAdapter.notifyDataSetChanged();
//            tvCastProgressBar.setVisibility(View.GONE);
//            tvCastHeading.setText(R.string.tv_casts_heading);
//        } else {
//            tvSeasonsCardView.setVisibility(View.GONE);
//        }

        if (getActivity() != null || tvInformation.getImages().getBackdrops().size() < 1) {

            carouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(final int position) {
                    View itemView = getActivity().getLayoutInflater().inflate(R.layout.caraousel_movie_layout, null);
                    TextView tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
                    ImageView draweeView = itemView.findViewById(R.id.img_movie_poster);
                    tvMovieTitle.setVisibility(View.GONE);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FullscreenImageViewActivity.class);
                            intent.putExtra("Image_Path", tvInformation.getImages().getBackdrops().get(position).getFilePath());
                            intent.putExtra("orientartion", 1);
                            startActivity(intent);
                        }
                    });
                    itemView.setTag(position);
                    ImageUtil.loadImageWithFullScreen(getActivity(), draweeView, tvInformation.getImages().getBackdrops().get(position).getFilePath());
                    return itemView;

                }
            });

            carouselView.setPageCount(tvInformation.getImages().getBackdrops().size());

//            TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), tvInformation.getCredits().getCast(), tvId);
//            tvCastGridView.setAdapter(tvCastAdapter);
//            tvCastAdapter.notifyDataSetChanged();
//            tvCastProgressBar.setVisibility(View.GONE);
//            tvCastHeading.setText(R.string.tv_casts_heading);
        } else {
            tvSeasonsCardView.setVisibility(View.GONE);
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


    private void loadSimilarRecommendations(CombinedTvDetail tvInformation) {
        if (tvInformation != null) {
            if (tvInformation.getSimilar().getResults().size() < 1) {
                similarTvShowsCardView.setVisibility(View.GONE);
            } else {
                List<Pictures> similarTvList = tvInformation.getSimilar().getResults();
                if (getActivity() != null) {
                    TvDataAdapter similarTvDataAdapter = new TvDataAdapter(getActivity(), similarTvList, 1);
                    similarTvShowsHzGridView.setAdapter(similarTvDataAdapter);
                    similarTvDataAdapter.notifyDataSetChanged();
                    similarTvShowsHzGridView.setVisibility(View.VISIBLE);
                    similarTvShowsProgressBar.setVisibility(View.GONE);
                    similarTvShowsHeading.setText(R.string.more_tv_show_heading);
                }
            }
            if (tvInformation.getRecommendations().getResults().size() < 1) {
                recommendedTvShowsCardView.setVisibility(View.GONE);
            } else {
                List<Pictures> recommendedTvList = tvInformation.getRecommendations().getResults();
                if (getActivity() != null) {
                    TvDataAdapter recommendedTvDataAdapter = new TvDataAdapter(getActivity(), recommendedTvList, 2);
                    recommendedTvShowsHzGridView.setAdapter(recommendedTvDataAdapter);
                    recommendedTvDataAdapter.notifyDataSetChanged();
                    recommendedTvShowsHzGridView.setVisibility(View.VISIBLE);
                    recommendedTvShowsProgressBar.setVisibility(View.GONE);
                    recommendedTvShowsHeading.setText(R.string.you_must_watch_heading);
                }
            }
        }
    }

}
