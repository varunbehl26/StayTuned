package varunbehl.showstime.activity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import varunbehl.showstime.R;
import varunbehl.showstime.network.RetrofitManager;
import varunbehl.showstime.pojo.TvDetails.TvInfo;
import varunbehl.showstime.pojo.TvSeason.TvSeasonInfo;
import varunbehl.showstime.util.Constants;
import varunbehl.showstime.util.DateTimeHelper;

/**
 * Created by varunbehl on 09/05/17.
 */

public class NextAirService extends IntentService {
    private final RetrofitManager retrofitManager;
    private int tvId;
    private final Context context;
    private TvInfo tvInformation;
    private Integer lastSeasonNumber;
    private TvSeasonInfo tvSeasonInfo;
    private List<TvSeasonInfo.Episode> episodeList;
    private String json;
    private SharedPreferences prefs;


    public NextAirService() {
        super("NextAirService");
        retrofitManager = RetrofitManager.getInstance();
        context = this;

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            tvId = intent != null ? intent.getIntExtra("tvId", 0) : 0;
            fetchDataForTvInfo();
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
                                       if (tvInformation.getStatus().equals("Returning Series")) {
                                           lastSeasonNumber = tvInformation.getNumberOfSeasons();
                                       }
                                       fetchDataForSeasonInfo();
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);

                                   Log.v("Exception", "NullPointerException");
                               }

                               @Override
                               public void onNext(TvInfo tvInfo) {
                                   tvInformation = tvInfo;
                               }
                           }
                );
    }

    private void fetchDataForSeasonInfo() {
        Observable<TvSeasonInfo> tvSeasonInfoObservable = retrofitManager.getTvSeasonInfo(tvId + "", lastSeasonNumber + "");
        tvSeasonInfoObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<TvSeasonInfo>() {
                               @Override
                               public void onCompleted() {
                                   if (tvSeasonInfo != null) {
                                       episodeList = tvSeasonInfo.getEpisodes();
                                       if (episodeList != null && episodeList.size() > 0) {
                                           for (TvSeasonInfo.Episode episode : episodeList) {
                                               if (episode.getAirDate() != null) {
                                                   try {
                                                       //                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/mm/dd").parse(episode.getAirDate().toString());
                                                       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                                       Date date = simpleDateFormat.parse(episode.getAirDate());
                                                       Date currentDate = new Date();
                                                       if (date.getTime() > currentDate.getTime()) {
                                                           Log.v("Next Air date", date.toString());
                                                           SharedPreferences prefs = context.getSharedPreferences(
                                                                   Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                                                           final int is_fav = prefs.getInt("is_fav" + "_" + tvId, 0);
//                                                       if (is_fav == 1) {
//                                                           new ShowsTimeDBHelper(context).insertIntoDb(currentDate, tvId, context);
//                                                       }

                                                           Gson gson = new Gson();
                                                           json = gson.toJson(episode);
                                                           SharedPreferences.Editor editor = prefs.edit();
                                                           editor.putString(Constants.NEXT_AIR_DATE + "_" + tvId, json);
                                                           editor.apply();
                                                           buildNotification();

                                                       }


                                                   } catch (ParseException e) {
                                                       e.printStackTrace();
                                                       FirebaseCrash.report(e);

                                                   }
                                               }
                                           }
                                       }
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   FirebaseCrash.report(e);
                               }

                               @Override
                               public void onNext(TvSeasonInfo tvInfo) {
                                   tvSeasonInfo = tvInfo;
                               }
                           }
                );
    }

    private void buildNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        JSONObject episodeJson = null;
        try {
            episodeJson = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrash.report(e);

        }
        TvSeasonInfo.Episode episode = new Gson().fromJson(episodeJson != null ? episodeJson.toString() : null, TvSeasonInfo.Episode.class);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("Next episode of " + tvInformation.getName() + " arrives on ");
        mBuilder.setContentText(DateTimeHelper.parseDate(episode.getAirDate()));
        mBuilder.setColor(Color.parseColor("#3DA0E9"));
        mBuilder.setSmallIcon(R.drawable.fav);

        notificationManager.notify(1, mBuilder.build());
    }
}




