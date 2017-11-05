package varunbehl.showstime.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.List;

import varunbehl.showstime.FullscreenImageViewActivity;
import varunbehl.showstime.R;
import varunbehl.showstime.activity.MovieDetailActivity;
import varunbehl.showstime.adapter.MovieListDataAdapter;
import varunbehl.showstime.pojo.Cast.Cast;
import varunbehl.showstime.pojo.CombinedMovieDetail;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.pojo.Video.VideoResult;
import varunbehl.showstime.util.Constants;
import varunbehl.showstime.util.DateTimeHelper;
import varunbehl.showstime.util.ImageUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public static final String TAG = MovieDetailActivityFragment.class.getSimpleName();
    public static final String DETAIL_TV = "DETAIL_TV";

    private int is_fav;
    private int movieId;
    private TextView releaseDate;
    private TextView vote;
    private TextView plotSynopsis;
    private Button fav_button;
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
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private ConstraintLayout similarTvShowsCardView;
    private ConstraintLayout recommendedTvShowsCardView;
    private String episodeDate;
    private CombinedMovieDetail combinedMovieDetail;
    private CarouselView carouselView;


    public MovieDetailActivityFragment() {
    }


    public static MovieDetailActivityFragment newInstance(CombinedMovieDetail combinedMovieDetail) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("combinedMovieDetail", combinedMovieDetail);
        MovieDetailActivityFragment movieDetailActivityFragment = new MovieDetailActivityFragment();
        movieDetailActivityFragment.setArguments(bundle);
        return movieDetailActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ((MovieDetailActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((MovieDetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        LinearLayout detailLayout = rootView.findViewById(R.id.detail_layout);
//        CardView infoCardView = (CardView) rootView.findViewById(R.id.info_card_view);
        TextView title = rootView.findViewById(R.id.title);
        releaseDate = rootView.findViewById(R.id.release_date);
        vote = rootView.findViewById(R.id.vote);
        plotSynopsis = rootView.findViewById(R.id.plot_synopsis);
        fav_button = rootView.findViewById(R.id.b11);
        SimpleDraweeView draweeView = getActivity().findViewById(R.id.movie_poster);
        fav_button.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
        fav_button.setVisibility(View.GONE);
        ProgressBar progressBar = rootView.findViewById(R.id.progress_fragment);
        View cordinatorLayout = getActivity().findViewById(R.id.app_bar);
        carouselView = getActivity().findViewById(R.id.carouselView);

        prefs = getActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);

        LinearLayout nextEpisodeCardView = rootView.findViewById(R.id.nextAirLayout);
        nextEpisodeCardView.setVisibility(View.GONE);

        ConstraintLayout tvSeasonsCardView = rootView.findViewById(R.id.tvSeasonsCard);
        ProgressBar tvSeasonsProgressBar = tvSeasonsCardView.findViewById(R.id.progress_main);
        tvSeasonsProgressBar.setVisibility(View.VISIBLE);
        tvSeasonsCardView.setVisibility(View.GONE);

        ConstraintLayout tvCastCardView = rootView.findViewById(R.id.tvCastCard);
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
        combinedMovieDetail = arguments.getParcelable("combinedMovieDetail");

        if (combinedMovieDetail != null) {
            movieId = combinedMovieDetail.getId();
            loadMovieDetails();
            loadSimilarRecommendations(combinedMovieDetail);
        }
        return rootView;
    }


    private void loadMovieDetails() {
//            cordinatorLayout.setVisibility(View.VISIBLE);
        boolean threadAlreadyRunning = false;
//            collapsingToolbar.setTitle(combinedMovieDetail.getTitle());
//            ImageUtil.loadImageWithFullScreen(getActivity(), draweeView, combinedMovieDetail.getBackdropPath());
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

//            if (getActivity() != null || (CombinedMovieDetailVideos != null ? CombinedMovieDetailVideos.size() : 0) < 1) {
//                VideoAdapter videoAdapter = new VideoAdapter(getActivity(), CombinedMovieDetailVideos);
//                videosHzGridView.setAdapter(videoAdapter);
//                videoAdapter.notifyDataSetChanged();
//                videosHzGridView.setVisibility(View.VISIBLE);
//                videosProgressBar.setVisibility(View.GONE);
//                videosHeading.setText(R.string.videos_heading);
//                tvSeasonsCardView.setVisibility(View.GONE);
//            } else {
//                tvSeasonsCardView.setVisibility(View.GONE);
//            }
        List<Cast> movieCast = combinedMovieDetail.getCredits().getCast();

//            if (getActivity() != null || combinedMovieDetail.getCredits().getCast().size() < 1) {
//                TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), movieCast, movieId);
//                tvCastGridView.setAdapter(tvCastAdapter);
//                tvCastAdapter.notifyDataSetChanged();
//                tvCastProgressBar.setVisibility(View.GONE);
//                tvCastHeading.setText(R.string.tv_casts_heading);
//            } else {
//                tvSeasonsCardView.setVisibility(View.GONE);
//            }

        if (getActivity() != null || combinedMovieDetail.getImages().getBackdrops().size() < 1) {

            carouselView.setViewListener(new ViewListener() {
                @Override
                public View setViewForPosition(final int position) {
                    View itemView = getActivity().getLayoutInflater().inflate(R.layout.caraousel_movie_layout, null);
                    TextView tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
                    SimpleDraweeView draweeView = itemView.findViewById(R.id.img_movie_poster);
                    tvMovieTitle.setVisibility(View.GONE);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), FullscreenImageViewActivity.class);
                            intent.putExtra("Image_Path", combinedMovieDetail.getImages().getBackdrops().get(position).getFilePath());
                            intent.putExtra("orientartion", 1);
                            startActivity(intent);
                        }
                    });
                    itemView.setTag(position);
                    ImageUtil.loadImageWithFullScreen(getActivity(), draweeView, combinedMovieDetail.getImages().getBackdrops().get(position).getFilePath());
                    return itemView;

                }
            });

            carouselView.setPageCount(combinedMovieDetail.getImages().getBackdrops().size());

//            TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), tvInformation.getCredits().getCast(), tvId);
//            tvCastGridView.setAdapter(tvCastAdapter);
//            tvCastAdapter.notifyDataSetChanged();
//            tvCastProgressBar.setVisibility(View.GONE);
//            tvCastHeading.setText(R.string.tv_casts_heading);
        }

    }


    private void loadSimilarRecommendations(CombinedMovieDetail combinedMovieDetail) {
        List<Pictures> similarMoviesList = combinedMovieDetail.getSimilar().getResults();

        if (similarMoviesList.size() < 1) {
            similarTvShowsCardView.setVisibility(View.GONE);
        } else {
            if (getActivity() != null) {
                MovieListDataAdapter popularTvDataAdapter = new MovieListDataAdapter(getContext(), similarMoviesList, 1);
                similarTvShowsHzGridView.setAdapter(popularTvDataAdapter);
                popularTvDataAdapter.notifyDataSetChanged();
                similarTvShowsHzGridView.setVisibility(View.VISIBLE);
                similarTvShowsProgressBar.setVisibility(View.GONE);
                similarTvShowsHeading.setText(R.string.more_tv_show_heading);
            }
        }
        List<Pictures> recommendedMoviesList = combinedMovieDetail.getRecommendations().getResults();
        if (recommendedMoviesList.size() < 1) {
            recommendedTvShowsCardView.setVisibility(View.GONE);
        } else {
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

}
