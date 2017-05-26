package varunbehl.staytuned.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

import varunbehl.staytuned.pojo.TvDetails.TvInfo;

/**
 * Created by varunbehl on 18/03/17.
 */

public class StayTunedDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "StayTuned.db";
    private static final String PRIMARY_KEY = "PRIMARY_KEY";


    public StayTunedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void addintoDB(TvInfo tvInformation, Context context, int tvId) {
        StayTunedDBHelper dbHelper = new StayTunedDBHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(StayTunedContract.StayTunedEntry.RELEASE_DATE, tvInformation.getFirstAirDate());
        values.put(StayTunedContract.StayTunedEntry.RATING, tvInformation.getVoteCount());
        values.put(StayTunedContract.StayTunedEntry.DESC, tvInformation.getOverview());
        values.put(StayTunedContract.StayTunedEntry.SEASONS, tvInformation.getNumberOfSeasons());
        values.put(StayTunedContract.StayTunedEntry.IMAGE, tvInformation.getBackdropPath());
        values.put(StayTunedContract.StayTunedEntry.EPISODE_NO, 0);
        values.put(StayTunedContract.StayTunedEntry.SEASON_NO, 0);
        values.put(StayTunedContract.StayTunedEntry.IS_WATCHED, 0);
        values.put(StayTunedContract.StayTunedEntry.IS_FAVORITE, 1);
        values.put(StayTunedContract.StayTunedEntry.IS_NOTIFIED, 0);
        values.put(StayTunedContract.StayTunedEntry.DATE_ADDED, new Date().toString());
        values.put(StayTunedContract.StayTunedEntry.DATE_MODIFIED, new Date().toString());
        values.put(StayTunedContract.StayTunedEntry.USER_ID, Math.random());
        values.put(StayTunedContract.StayTunedEntry.TV_ID, tvId);
        values.put(StayTunedContract.StayTunedEntry.NAME, tvInformation.getName());

        long rowUpdated = db.insert(StayTunedContract.StayTunedEntry.TABLE_NAME, null, values);

        Log.v("Row Updated: ", rowUpdated + "");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE " + StayTunedContract.StayTunedEntry.TABLE_NAME + "("
                + StayTunedContract.StayTunedEntry.EPISODE_NO + " INTEGER NOT NULL,"
                + StayTunedContract.StayTunedEntry.SEASON_NO + " INTEGER NOT NULL,"
                + StayTunedContract.StayTunedEntry.IS_WATCHED + " INTEGER ,"
                + StayTunedContract.StayTunedEntry.IS_FAVORITE + " INTEGER ,"
                + StayTunedContract.StayTunedEntry.IS_NOTIFIED + " INTEGER ,"
                + StayTunedContract.StayTunedEntry.DATE_ADDED + " DATE NOT NULL,"
                + StayTunedContract.StayTunedEntry.DATE_MODIFIED + " DATE NOT NULL,"
                + StayTunedContract.StayTunedEntry.USER_ID + " INTEGER ,"
                + StayTunedContract.StayTunedEntry.RELEASE_DATE + " DATE ,"
                + StayTunedContract.StayTunedEntry.RATING + " INTEGER,"
                + StayTunedContract.StayTunedEntry.DESC + " TEXT,"
                + StayTunedContract.StayTunedEntry.SEASONS + " INTEGER,"
                + StayTunedContract.StayTunedEntry.IMAGE + " TEXT,"
                + StayTunedContract.StayTunedEntry.NAME + " TEXT NOT NULL ,"
                + StayTunedContract.StayTunedEntry.NEXT_AIR_DATE + " DATE ,"
                + StayTunedContract.StayTunedEntry.TV_ID + " INTEGER NOT NULL,PRIMARY KEY (tv_id,episode_no,season_no))";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StayTunedContract.StayTunedEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertIntoDb(Date date, int tvId, Context context) {

            StayTunedDBHelper dbHelper = new StayTunedDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StayTunedContract.StayTunedEntry.NEXT_AIR_DATE, date.toString());
            db.update(StayTunedContract.StayTunedEntry.TABLE_NAME, values, StayTunedContract.StayTunedEntry.TV_ID + "=" + tvId, null);

    }
}
