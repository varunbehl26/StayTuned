package varunbehl.showstime.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import varunbehl.showstime.pojo.Cast.CastInfo;
import varunbehl.showstime.pojo.Episode.EpisodeInfo;
import varunbehl.showstime.pojo.MovieDetail;
import varunbehl.showstime.pojo.Picture.Picture_Detail;
import varunbehl.showstime.pojo.Reviews.Reviews;
import varunbehl.showstime.pojo.Search.SearchResult;
import varunbehl.showstime.pojo.Tv.Tv;
import varunbehl.showstime.pojo.TvDetails.TvInfo;
import varunbehl.showstime.pojo.TvSeason.TvSeasonInfo;
import varunbehl.showstime.pojo.Video.Videos;

/**
 * Created by varunbehl on 07/03/17.
 */
public class RetrofitManager {

    private static DataInterface dataInterface = null;
    private static String API_BASE_URL = "http://api.themoviedb.org/";
    private static String API_KEY = "29c90a4aee629499a2149041cc6a0ffd";
    private static RetrofitManager retrofitManager;

    private RetrofitManager() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        dataInterface = retrofit.create(DataInterface.class);
    }


    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            retrofitManager = new RetrofitManager();
        }
        return retrofitManager;
    }



    public Observable<Reviews> getComments(int movieId, String apiKey) {
        return dataInterface.listReviews(movieId, apiKey);
    }

    public Observable<Videos> getTrailer(int movieId) {
        return dataInterface.listVideos(movieId, API_KEY);
    }


    public Observable<Tv> listTvShows(String tvId, int page) {
        return dataInterface.listTvShows(tvId, API_KEY, page);
    }

    public Observable<TvInfo> getTvInfo(String tvId) {
        return dataInterface.getTvInfo(tvId, API_KEY, "credits");
    }

    public Observable<Tv> getSimilarTvShows(String tvId) {
        return dataInterface.getSimilarTvShows(tvId, API_KEY);
    }

    public Observable<Tv> getRecommendedTvShows(String tvId) {
        return dataInterface.getRecommendedTvShows(tvId, API_KEY);
    }
    public Observable<Picture_Detail> getSimilarMovies(int  movieId) {
        return dataInterface.getSimilarMovies(movieId, API_KEY);
    }

    public Observable<Picture_Detail> getRecommendedMovies(int movieId) {
        return dataInterface.getRecommendedMovies(movieId, API_KEY);
    }

    public Observable<TvSeasonInfo> getTvSeasonInfo(String tvId, String season) {
        return dataInterface.getTvSeasonInfo(tvId, season, API_KEY);
    }

    public Observable<EpisodeInfo> getEpisodeInfo(String tvId, String season, String episode) {
        return dataInterface.getEpisodeInfo(tvId, season, episode, API_KEY);
    }


    public Observable<SearchResult> searchTvShows(String query) {
        return dataInterface.searchTvShows(API_KEY, query);
    }

    public Observable<CastInfo> getCastInfo(String castId) {
        return dataInterface.getCastInfo(castId, API_KEY, "en-US", "combined_credits");
    }

    public Observable<Picture_Detail> listMoviesInfo(String categories, int page) {
        Observable<Picture_Detail> moviesInfoCall = dataInterface.listMoviesInfo(categories, page, API_KEY);
        return moviesInfoCall;
    }

    public Observable<MovieDetail> getMoviesDetail(int id) {
        Observable<MovieDetail> moviesInfoCall = dataInterface.getMovieDetail(id,API_KEY,"videos,images,credits,reviews");
        return moviesInfoCall;
    }


}

