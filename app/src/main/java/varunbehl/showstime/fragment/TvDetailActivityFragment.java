package varunbehl.showstime.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import varunbehl.showstime.FullscreenImageViewActivity;
import varunbehl.showstime.R;
import varunbehl.showstime.activity.TvDetailActivity;
import varunbehl.showstime.adapter.TvDataAdapter;
import varunbehl.showstime.data.ShowsTimeContract;
import varunbehl.showstime.data.ShowsTimeDBHelper;
import varunbehl.showstime.databinding.FragmentDetailBinding;
import varunbehl.showstime.pojo.Picture.Pictures;
import varunbehl.showstime.pojo.TvDetails.CombinedTvDetail;
import varunbehl.showstime.pojo.TvSeason.TvSeasonInfo;
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
    private CombinedTvDetail tvInformation;
    private int tvId;

    private CollapsingToolbarLayout collapsingToolbar;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private FragmentDetailBinding binding;
//    private CarouselView carouselView;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

        ((TvDetailActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((TvDetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        collapsingToolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
//        collapsingToolbar.setTitle("");
        binding.b11.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));

//        carouselView = getActivity().findViewById(R.id.carouselView);

        prefs = getActivity().getSharedPreferences(
                Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);


        binding.similarTvShowsCard.progressMain.setVisibility(View.VISIBLE);

        binding.recommendedTvShowsCard.progressMain.setVisibility(View.VISIBLE);


        Bundle arguments = getArguments();
        tvInformation = arguments.getParcelable("tvInformation");
        if (tvInformation != null) {
            tvId = tvInformation.getId();
            loadTvDetails();
            loadSimilarRecommendations(tvInformation);
            fetchNextAirEpisode();
        }
        return binding.getRoot();
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
            binding.nextAirLayout.setVisibility(View.GONE);
        } else {
            binding.episodeLayout.episodeDate.setText(DateTimeHelper.parseDate(episode.getAirDate()));
            binding.episodeLayout.episodeName.setText(episode.getName());
            if (episode.getStillPath() != null && !"".equals(episode.getStillPath())) {
                ImageUtil.loadImageWithFullScreen(getActivity(), binding.episodeLayout.imgEpisodePoster, episode.getStillPath());
                binding.episodeLayout.imgEpisodePoster.setVisibility(View.VISIBLE);
            } else {
                binding.episodeLayout.imgEpisodePoster.setVisibility(View.GONE);
            }
            if (!episode.getOverview().equals("")) {
                binding.episodeLayout.episodeDesc.setVisibility(View.GONE);
            } else {
                binding.episodeLayout.episodeDesc.setText(episode.getOverview());
            }
        }
    }

    private void loadTvDetails() {
        binding.progressBarMain.setVisibility(View.GONE);
        boolean threadAlreadyRunning = false;

//        collapsingToolbar.setTitle(tvInformation.getName());

        binding.releaseDate.setText(getString(R.string.firstAir) + DateTimeHelper.parseDate(tvInformation.getFirstAirDate()) + "");

        binding.vote.setText(getString(R.string.rating) + tvInformation.getVoteAverage() + "/10");
        binding.plotSynopsis.setText(tvInformation.getOverview());
        binding.genreTextView.setText(tvInformation.getGenres().toString());


        is_fav = (prefs.getInt("is_fav" + "_" + tvId, 0));
        if (is_fav == 1) {
            binding.b11.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
        } else {
            binding.b11.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
        }

        binding.b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_fav == 1) {
                    binding.b11.setBackground(getContext().getResources().getDrawable(R.drawable.unfav));
                    deleteFromDb();
                    is_fav = 0;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("is_fav" + "_" + tvId, 0);
                    editor.apply();
                } else {
                    binding.b11.setBackground(getContext().getResources().getDrawable(R.drawable.fav));
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

        if (getActivity() != null || tvInformation.getImages().getBackdrops().size() < 1) {

//            carouselView.setViewListener(new ViewListener() {
//                @Override
//                public View setViewForPosition(final int position) {
//                    View itemView = getActivity().getLayoutInflater().inflate(R.layout.caraousel_movie_layout, null);
//                    TextView tvMovieTitle = itemView.findViewById(R.id.tv_movie_title);
//                    ImageView draweeView = itemView.findViewById(R.id.img_movie_poster);
//                    tvMovieTitle.setVisibility(View.GONE);
//
//                    itemView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), FullscreenImageViewActivity.class);
//                            intent.putExtra("Image_Path", tvInformation.getImages().getBackdrops().get(position).getFilePath());
//                            intent.putExtra("orientartion", 1);
//                            startActivity(intent);
//                        }
//                    });
//                    itemView.setTag(position);
//                    ImageUtil.loadImageWithFullScreen(getActivity(), draweeView, tvInformation.getImages().getBackdrops().get(position).getFilePath());
//                    return itemView;
//
//                }
//            });
//
//            carouselView.setPageCount(tvInformation.getImages().getBackdrops().size());

//            TvCastAdapter tvCastAdapter = new TvCastAdapter(getActivity(), tvInformation.getCredits().getCast(), tvId);
//            tvCastGridView.setAdapter(tvCastAdapter);
//            tvCastAdapter.notifyDataSetChanged();
//            tvCastProgressBar.setVisibility(View.GONE);
//            tvCastHeading.setText(R.string.tv_casts_heading);
        } else {
            binding.tvSeasonsCard.getRoot().setVisibility(View.GONE);
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
                binding.similarTvShowsCard.getRoot().setVisibility(View.GONE);
            } else {
                List<Pictures> similarTvList = tvInformation.getSimilar().getResults();
                if (getActivity() != null) {
                    TvDataAdapter similarTvDataAdapter = new TvDataAdapter(getActivity(), similarTvList, 1);
                    binding.similarTvShowsCard.horizontalGridView.setAdapter(similarTvDataAdapter);
                    similarTvDataAdapter.notifyDataSetChanged();
                    binding.similarTvShowsCard.horizontalGridView.setVisibility(View.VISIBLE);
                    binding.similarTvShowsCard.progressMain.setVisibility(View.GONE);
                    binding.similarTvShowsCard.heading.setText(R.string.more_tv_show_heading);
                }
            }
            if (tvInformation.getRecommendations().getResults().size() < 1) {
                binding.recommendedTvShowsCard.getRoot().setVisibility(View.GONE);
            } else {
                List<Pictures> recommendedTvList = tvInformation.getRecommendations().getResults();
                if (getActivity() != null) {
                    TvDataAdapter recommendedTvDataAdapter = new TvDataAdapter(getActivity(), recommendedTvList, 2);
                    binding.recommendedTvShowsCard.horizontalGridView.setAdapter(recommendedTvDataAdapter);
                    recommendedTvDataAdapter.notifyDataSetChanged();
                    binding.recommendedTvShowsCard.horizontalGridView.setVisibility(View.VISIBLE);
                    binding.recommendedTvShowsCard.progressMain.setVisibility(View.GONE);
                    binding.recommendedTvShowsCard.heading.setText(R.string.you_must_watch_heading);
                }
            }
        }
    }

}
