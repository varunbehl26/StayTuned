package varunbehl.staytuned.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import varunbehl.staytuned.pojo.Cast.CastInfo;
import varunbehl.staytuned.pojo.Episode.EpisodeInfo;
import varunbehl.staytuned.pojo.MovieDetail;
import varunbehl.staytuned.pojo.Picture.Picture_Detail;
import varunbehl.staytuned.pojo.Picture.Pictures;
import varunbehl.staytuned.pojo.Reviews.Reviews;
import varunbehl.staytuned.pojo.Search.SearchResult;
import varunbehl.staytuned.pojo.Tv.Tv;
import varunbehl.staytuned.pojo.TvDetails.TvInfo;
import varunbehl.staytuned.pojo.TvSeason.TvSeasonInfo;
import varunbehl.staytuned.pojo.Video.Videos;

/**
 * Created by varunbehl on 07/03/17.
 */

interface DataInterface {

    @GET("3/movie/{categories}")
    Observable<Picture_Detail> listMoviesInfo(@Path("categories") String categories, @Query("page") int page, @Query("api_key") String apiKey);

    @GET("3/tv/{id}/videos")
    Observable<Videos> listVideos(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Observable<Reviews> listReviews(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("3/tv/{id}")
    Observable<Tv> listTvShows(@Path("id") String id, @Query("api_key") String apiKey,@Query("page") int page);


    @GET("3/tv/{id}")
    Observable<TvInfo> getTvInfo(@Path("id") String id, @Query("api_key") String apiKey, @Query("append_to_response") String credits);


    @GET("3/movie/{id}")
    Observable<MovieDetail> getMovieDetail(@Path("id") int id, @Query("api_key") String apiKey, @Query("append_to_response") String credits);


    @GET("3/tv/{id}/season/{season}")
    Observable<TvSeasonInfo> getTvSeasonInfo(@Path("id") String id, @Path("season") String season, @Query("api_key") String apiKey);



//    @GET("3/person/{id}/combined_credits")
//    Observable<CastDetail> getCastDetail(@Path("id") String id, @Query("api_key") String apiKey, @Query("language") String language);


    @GET("3/tv/{id}/similar")
    Observable<Tv> getSimilarTvShows(@Path("id") String id, @Query("api_key") String apiKey);

    @GET("3/tv/{id}/recommendations")
    Observable<Tv> getRecommendedTvShows(@Path("id") String id, @Query("api_key") String apiKey);


    @GET("3/movie/{id}/similar")
    Observable<Picture_Detail> getSimilarMovies(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/recommendations")
    Observable<Picture_Detail> getRecommendedMovies(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("3/tv/{id}/season/{season}/episode/{episodeId}")
    Observable<EpisodeInfo> getEpisodeInfo(@Path("id") String id, @Path("season") String episodeId, @Path("episodeId") String season, @Query("api_key") String apiKey);

    @GET("3/search/tv")
    Observable<SearchResult> searchTvShows(@Query("api_key")String apiKey,@Query("query")String query);

    @GET("3/person/{id}")
    Observable<CastInfo> getCastInfo(@Path("id") String id, @Query("api_key") String apiKey, @Query("language") String language, @Query("append_to_response") String credits);
}
